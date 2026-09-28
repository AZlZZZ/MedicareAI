from __future__ import annotations

import json
from pathlib import Path
from typing import List

import joblib
import pandas as pd
from fastapi import FastAPI
from pydantic import BaseModel, Field


ROOT = Path(__file__).resolve().parent
MODELS_DIR = ROOT / "models"
LATEST_METADATA = MODELS_DIR / "latest_metadata.json"


class PatientFeatures(BaseModel):
    patient_id: int
    age: int = -1
    gender: str = "UNKNOWN"
    medical_history_length: int = 0
    active_schedule_count: int = 0
    total_alerts: int = 0
    read_alerts: int = 0
    read_rate: float = 0.0


class MedicationCandidate(BaseModel):
    medicament_id: int
    name: str
    description_length: int = 0
    stock: int = 0
    schedule_count: int = 0
    is_active: bool = False
    total_alerts: int = 0
    read_alerts: int = 0
    adherence_rate: float = 0.0


class RecommendationRequest(BaseModel):
    patient: PatientFeatures
    candidates: List[MedicationCandidate]
    top_k: int = Field(default=5, ge=1, le=50)


class RecommendationItem(BaseModel):
    medicament_id: int
    score: float
    reason: str


class RecommendationResponse(BaseModel):
    model_version: str
    used_fallback: bool
    recommendations: List[RecommendationItem]


app = FastAPI(title="Medication Recommender API", version="1.0.0")


def load_model_bundle() -> tuple[dict, dict]:
    if not LATEST_METADATA.exists():
        return {}, {}
    metadata = json.loads(LATEST_METADATA.read_text(encoding="utf-8"))
    artifact_path = MODELS_DIR / metadata["artifact"]
    if not artifact_path.exists():
        return {}, metadata
    bundle = joblib.load(artifact_path)
    return bundle, metadata


def fallback_rank(candidates: List[MedicationCandidate], top_k: int) -> List[RecommendationItem]:
    ranked = sorted(
        candidates,
        key=lambda c: (c.is_active, c.adherence_rate, c.stock, c.schedule_count),
        reverse=True,
    )
    return [
        RecommendationItem(
            medicament_id=item.medicament_id,
            score=float(0.5 + 0.2 * int(item.is_active) + 0.2 * item.adherence_rate),
            reason="fallback_rule_based",
        )
        for item in ranked[:top_k]
    ]


@app.get("/health")
def health() -> dict:
    return {"status": "ok"}


@app.get("/model-info")
def model_info() -> dict:
    _, metadata = load_model_bundle()
    return metadata if metadata else {"model_version": "fallback-only"}


@app.post("/recommend", response_model=RecommendationResponse)
def recommend(payload: RecommendationRequest) -> RecommendationResponse:
    bundle, metadata = load_model_bundle()
    if not bundle:
        return RecommendationResponse(
            model_version="fallback-only",
            used_fallback=True,
            recommendations=fallback_rank(payload.candidates, payload.top_k),
        )

    rows = []
    for candidate in payload.candidates:
        rows.append(
            {
                "age": payload.patient.age,
                "gender": payload.patient.gender.upper(),
                "medical_history_length": payload.patient.medical_history_length,
                "active_schedule_count": payload.patient.active_schedule_count,
                "total_alerts_y": payload.patient.total_alerts,
                "read_alerts_y": payload.patient.read_alerts,
                "read_rate": payload.patient.read_rate,
                "name": candidate.name.lower(),
                "description_length": candidate.description_length,
                "stock": candidate.stock,
                "schedule_count": candidate.schedule_count,
                "is_active": candidate.is_active,
                "total_alerts_x": candidate.total_alerts,
                "read_alerts_x": candidate.read_alerts,
                "adherence_rate": candidate.adherence_rate,
                "medicament_id": candidate.medicament_id,
            }
        )
    df = pd.DataFrame(rows)
    feature_columns = bundle["feature_columns"]
    model = bundle["model"]
    df["score"] = model.predict_proba(df[feature_columns])[:, 1]
    ranked = df.sort_values("score", ascending=False).head(payload.top_k)

    recommendations = [
        RecommendationItem(
            medicament_id=int(row["medicament_id"]),
            score=float(row["score"]),
            reason="model_score",
        )
        for _, row in ranked.iterrows()
    ]

    return RecommendationResponse(
        model_version=metadata.get("model_version", "unknown"),
        used_fallback=False,
        recommendations=recommendations,
    )
