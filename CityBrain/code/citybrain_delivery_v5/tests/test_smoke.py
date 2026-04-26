from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)

def test_student_page():
    r = client.get("/")
    assert r.status_code == 200
    assert "학생 화면" in r.text

def test_login_page():
    r = client.get("/admin/login")
    assert r.status_code == 200
    assert "관리자 로그인" in r.text

def test_kiosk_mock_api():
    payload = {"menu_name": "에비동", "sold_delta": 2, "congestion_level": "혼잡"}
    r = client.post("/api/kiosk/sales", json=payload)
    assert r.status_code == 200
    assert r.json()["ok"] is True