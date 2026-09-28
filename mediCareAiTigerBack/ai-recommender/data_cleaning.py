from __future__ import annotations

from pathlib import Path

import pandas as pd


def clean_patient_features(path: Path) -> pd.DataFrame:
    df = pd.read_csv(path, sep=";")
    df["age"] = pd.to_numeric(df["age"], errors="coerce").fillna(-1).astype(int)
    df["gender"] = df["gender"].fillna("UNKNOWN").astype(str).str.upper()
    numeric_cols = [
        "medical_history_length",
        "active_schedule_count",
        "total_alerts",
        "read_alerts",
        "read_rate",
    ]
    for col in numeric_cols:
        df[col] = pd.to_numeric(df[col], errors="coerce").fillna(0)
    df["patient_id"] = pd.to_numeric(df["patient_id"], errors="coerce").astype("Int64")
    return df.dropna(subset=["patient_id"]).astype({"patient_id": int})


def clean_medication_features(path: Path) -> pd.DataFrame:
    df = pd.read_csv(path, sep=";")
    df["name"] = df["name"].fillna("unknown").astype(str).str.lower()
    for col in ["description_length", "stock"]:
        df[col] = pd.to_numeric(df[col], errors="coerce").fillna(0)
    df["medicament_id"] = pd.to_numeric(df["medicament_id"], errors="coerce").astype("Int64")
    return df.dropna(subset=["medicament_id"]).astype({"medicament_id": int})


def clean_interactions(path: Path) -> pd.DataFrame:
    df = pd.read_csv(path, sep=";")
    for col in [
        "patient_id",
        "medicament_id",
        "schedule_count",
        "total_alerts",
        "read_alerts",
        "adherence_rate",
        "label",
    ]:
        df[col] = pd.to_numeric(df[col], errors="coerce").fillna(0)
    df["is_active"] = df["is_active"].astype(str).str.lower().isin(["true", "1", "yes"])
    df["patient_id"] = df["patient_id"].astype(int)
    df["medicament_id"] = df["medicament_id"].astype(int)
    df["label"] = df["label"].astype(int)
    return df
