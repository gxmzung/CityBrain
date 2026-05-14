# Runbook Evidence

## Purpose

이 폴더는 CityBrain 실행법과 시연 준비 절차를 정리하는 공간이다.

자세한 실행법은 프로젝트 루트의 아래 문서를 참고한다.

```text
CITYBRAIN_RUNBOOK.md
```

---

## Quick Check

```text
1. 백엔드 실행 가능
2. 학생 웹 접속 가능
3. 관리자 웹 접속 가능
4. Android BASE_URL 확인
5. MacBook IP 확인
6. 같은 Wi-Fi 연결 확인
7. 메뉴 화면 캡처
8. 관리자 화면 캡처
9. 만족도/반응 화면 캡처
10. 시연 순서 1회 리허설
```

---

## Backend Command

```bash
uvicorn backend.app.main:app --host 0.0.0.0 --port 8000 --reload
```

---

## Access URLs

```text
학생 웹:
http://127.0.0.1:8000/
http://127.0.0.1:8000/student

관리자 웹:
http://127.0.0.1:8000/admin/login
http://127.0.0.1:8000/admin
```

---

## Android Connection Rule

```text
Android 앱에서는 127.0.0.1을 사용하지 않는다.
MacBook의 실제 Wi-Fi IP를 BASE_URL로 사용한다.
FastAPI 서버는 --host 0.0.0.0 으로 실행한다.
```
