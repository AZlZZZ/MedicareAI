from __future__ import annotations

from dataclasses import dataclass
from typing import Dict, List

import numpy as np
import pandas as pd
from sklearn.compose import ColumnTransformer
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import OneHotEncoder, StandardScaler


@dataclass
class RankingMetrics:
    precision_at_k: float
    recall_at_k: float
    map_at_k: float
    ndcg_at_k: float

    def to_dict(self) -> Dict[str, float]:
        return {
            "precision_at_k": float(self.precision_at_k),
            "recall_at_k": float(self.recall_at_k),
            "map_at_k": float(self.map_at_k),
            "ndcg_at_k": float(self.ndcg_at_k),
        }


def build_training_frame(
    patients: pd.DataFrame, medications: pd.DataFrame, interactions: pd.DataFrame
) -> pd.DataFrame:
    df = interactions.merge(patients, on="patient_id", how="left").merge(
        medications, on="medicament_id", how="left"
    )
    df["name"] = df["name"].fillna("unknown")
    df["gender"] = df["gender"].fillna("UNKNOWN")
    return df


def build_model() -> Pipeline:
    numeric_features = [
        "age",
        "medical_history_length",
        "active_schedule_count",
        "total_alerts_y",
        "read_alerts_y",
        "read_rate",
        "description_length",
        "stock",
        "schedule_count",
        "total_alerts_x",
        "read_alerts_x",
        "adherence_rate",
    ]
    categorical_features = ["gender", "name", "is_active"]
    preprocessor = ColumnTransformer(
        transformers=[
            ("num", StandardScaler(), numeric_features),
            (
                "cat",
                OneHotEncoder(handle_unknown="ignore", sparse_output=False),
                categorical_features,
            ),
        ]
    )
    return Pipeline(
        steps=[
            ("preprocessor", preprocessor),
            ("classifier", LogisticRegression(max_iter=1000)),
        ]
    )


def average_precision(labels: List[int]) -> float:
    positives = sum(labels)
    if positives == 0:
        return 0.0
    running_hits = 0
    score = 0.0
    for idx, label in enumerate(labels, start=1):
        if label == 1:
            running_hits += 1
            score += running_hits / idx
    return score / positives


def dcg_at_k(labels: List[int], k: int) -> float:
    score = 0.0
    for i, rel in enumerate(labels[:k], start=1):
        score += rel / np.log2(i + 1)
    return score


def evaluate_ranking(df_scored: pd.DataFrame, k: int = 5) -> RankingMetrics:
    grouped = df_scored.groupby("patient_id")
    precision_scores = []
    recall_scores = []
    ap_scores = []
    ndcg_scores = []

    for _, group in grouped:
        ordered = group.sort_values("score", ascending=False)
        topk = ordered.head(k)
        labels_topk = topk["label"].astype(int).tolist()
        total_relevant = int(group["label"].sum())
        hits = sum(labels_topk)

        precision_scores.append(hits / k if k else 0.0)
        recall_scores.append(hits / total_relevant if total_relevant > 0 else 0.0)
        ap_scores.append(average_precision(labels_topk))

        ideal_labels = sorted(group["label"].astype(int).tolist(), reverse=True)
        actual_dcg = dcg_at_k(labels_topk, k)
        ideal_dcg = dcg_at_k(ideal_labels, k)
        ndcg_scores.append(actual_dcg / ideal_dcg if ideal_dcg > 0 else 0.0)

    return RankingMetrics(
        precision_at_k=float(np.mean(precision_scores) if precision_scores else 0.0),
        recall_at_k=float(np.mean(recall_scores) if recall_scores else 0.0),
        map_at_k=float(np.mean(ap_scores) if ap_scores else 0.0),
        ndcg_at_k=float(np.mean(ndcg_scores) if ndcg_scores else 0.0),
    )
