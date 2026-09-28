# AI Recommender Module

## Setup

```bash
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
```

## Input datasets

Place the exported backend CSV files in `ai-recommender/data/`:
- `patient_features.csv`
- `medication_features.csv`
- `patient_medication_interactions.csv`

Dataset schema is documented in `FEATURE_SCHEMA.md`.

## Train baseline model

```bash
python train.py
```

Outputs:
- `models/medication_ranker_<version>.joblib`
- `models/metadata_<version>.json`
- `models/latest_metadata.json`
- `reports/metrics_<version>.json`

## Run inference API

```bash
uvicorn inference_api:app --host 0.0.0.0 --port 8000
```

Endpoints:
- `GET /health`
- `GET /model-info`
- `POST /recommend`

## Iteration / retraining loop

1. Export fresh datasets from Spring backend recommendation export endpoints.
2. Replace CSV files under `data/`.
3. Run `python train.py`.
4. Compare latest metrics in `reports/` against previous model.
5. Deploy recommender API with the new artifact only if metrics improved.
6. Monitor fallback ratio from Spring backend to detect inference issues.
