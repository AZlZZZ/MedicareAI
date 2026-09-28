from __future__ import annotations

import json
from datetime import datetime, timezone
from pathlib import Path

import joblib
from sklearn.model_selection import train_test_split

from data_cleaning import clean_interactions, clean_medication_features, clean_patient_features
from recommender import build_model, build_training_frame, evaluate_ranking


ROOT = Path(__file__).resolve().parent
DATA_DIR = ROOT / "data"
MODEL_DIR = ROOT / "models"
REPORTS_DIR = ROOT / "reports"


def main() -> None:
    MODEL_DIR.mkdir(parents=True, exist_ok=True)
    REPORTS_DIR.mkdir(parents=True, exist_ok=True)

    patients = clean_patient_features(DATA_DIR / "patient_features.csv")
    medications = clean_medication_features(DATA_DIR / "medication_features.csv")
    interactions = clean_interactions(DATA_DIR / "patient_medication_interactions.csv")
    training_frame = build_training_frame(patients, medications, interactions)

    train_df, test_df = train_test_split(
        training_frame,
        test_size=0.2,
        random_state=42,
        stratify=training_frame["label"] if training_frame["label"].nunique() > 1 else None,
    )

    model = build_model()
    feature_columns = [c for c in train_df.columns if c not in ["label", "patient_id", "medicament_id"]]
    model.fit(train_df[feature_columns], train_df["label"])

    scored_test = test_df.copy()
    scored_test["score"] = model.predict_proba(test_df[feature_columns])[:, 1]
    metrics = evaluate_ranking(scored_test[["patient_id", "label", "score"]], k=5)

    model_version = datetime.now(timezone.utc).strftime("%Y%m%d%H%M%S")
    model_path = MODEL_DIR / f"medication_ranker_{model_version}.joblib"
    metadata_path = MODEL_DIR / f"metadata_{model_version}.json"
    report_path = REPORTS_DIR / f"metrics_{model_version}.json"

    joblib.dump({"model": model, "feature_columns": feature_columns}, model_path)

    metadata = {
        "model_version": model_version,
        "created_at_utc": datetime.now(timezone.utc).isoformat(),
        "artifact": model_path.name,
        "feature_columns": feature_columns,
        "train_size": int(len(train_df)),
        "test_size": int(len(test_df)),
    }
    report = {"model_version": model_version, **metrics.to_dict()}

    metadata_path.write_text(json.dumps(metadata, indent=2), encoding="utf-8")
    report_path.write_text(json.dumps(report, indent=2), encoding="utf-8")

    latest_metadata = ROOT / "models" / "latest_metadata.json"
    latest_metadata.write_text(json.dumps(metadata, indent=2), encoding="utf-8")
    print(json.dumps(report, indent=2))


if __name__ == "__main__":
    main()
