from fastapi.testclient import TestClient
from app.main import app

def test_student_page():
    with TestClient(app) as client:
        r = client.get("/")
        assert r.status_code == 200
        assert "식사" in r.text
        assert "301동식당" in r.text

def test_login_page():
    with TestClient(app) as client:
        r = client.get("/admin/login")
        assert r.status_code == 200
        assert "관리자 로그인" in r.text

def test_student_api():
    with TestClient(app) as client:
        r = client.get("/api/student/state")
        assert r.status_code == 200
        assert "remaining_count" in r.json()

def test_kiosk_mock_api():
    with TestClient(app) as client:
        payload = {"menu_name": "돈까스덮밥", "sold_delta": 2, "congestion_level": "혼잡", "wait_minutes": 15}
        r = client.post("/api/kiosk/sales", json=payload)
        assert r.status_code == 200
        assert r.json()["ok"] is True
