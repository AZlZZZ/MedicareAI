from fastapi.testclient import TestClient

from inference_api import app


client = TestClient(app)


def test_recommend_contract_uses_fallback_without_model() -> None:
    payload = {
        "patient": {
            "patient_id": 10,
            "age": 35,
            "gender": "FEMALE",
            "medical_history_length": 120,
            "active_schedule_count": 1,
            "total_alerts": 5,
            "read_alerts": 4,
            "read_rate": 0.8,
        },
        "candidates": [
            {
                "medicament_id": 1,
                "name": "med_a",
                "description_length": 100,
                "stock": 50,
                "schedule_count": 1,
                "is_active": True,
                "total_alerts": 3,
                "read_alerts": 3,
                "adherence_rate": 1.0,
            },
            {
                "medicament_id": 2,
                "name": "med_b",
                "description_length": 50,
                "stock": 10,
                "schedule_count": 0,
                "is_active": False,
                "total_alerts": 1,
                "read_alerts": 0,
                "adherence_rate": 0.0,
            },
        ],
        "top_k": 2,
    }
    response = client.post("/recommend", json=payload)
    assert response.status_code == 200
    body = response.json()
    assert "model_version" in body
    assert "used_fallback" in body
    assert isinstance(body["recommendations"], list)
    assert len(body["recommendations"]) == 2
    assert {"medicament_id", "score", "reason"} <= set(body["recommendations"][0].keys())
