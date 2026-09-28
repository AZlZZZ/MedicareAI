# Medication Recommendation Feature Schema

This document defines the canonical training datasets exported by backend endpoints.

## 1) patient_features.csv

Export endpoint: `GET /api/v1/agent/manageUser/exportRecommendationPatientFeatures`

Columns:
- `patient_id` (long): primary patient identifier.
- `age` (int): computed from birth date; `-1` when missing.
- `gender` (string): patient gender enum or `UNKNOWN`.
- `medical_history_length` (int): character count of medical history text.
- `active_schedule_count` (long): active medication schedules count.
- `total_alerts` (long): total medication alerts generated.
- `read_alerts` (long): alerts marked as read.
- `read_rate` (float): `read_alerts / total_alerts`, `0.0` if no alerts.

## 2) medication_features.csv

Export endpoint: `GET /api/v1/agent/manageUser/exportRecommendationMedicationFeatures`

Columns:
- `medicament_id` (long): medication identifier.
- `name` (string): medication name.
- `description_length` (int): description text length.
- `stock` (int): current stock level.

## 3) patient_medication_interactions.csv

Export endpoint: `GET /api/v1/agent/manageUser/exportRecommendationInteractions`

Columns:
- `patient_id` (long)
- `medicament_id` (long)
- `schedule_count` (long): number of schedules recorded for pair.
- `is_active` (boolean): at least one active schedule exists.
- `total_alerts` (long): alerts generated for patient-medication pair.
- `read_alerts` (long): pair alerts read.
- `adherence_rate` (float): `read_alerts / total_alerts`, `0.0` if no alerts.
- `label` (int): training target, `1` when pair is active or `adherence_rate >= 0.5`, else `0`.

## Labeling strategy

The `label` field is a pragmatic supervised target for first model iteration:
- Positive (`1`): currently used pair or historically strong adherence.
- Negative (`0`): non-active pair with weak adherence.

Later iterations can replace this with stronger labels (e.g., physician-validated outcomes).
