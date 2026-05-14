# Code Structure Evidence

## Purpose

이 폴더는 CityBrain의 코드 구조와 데이터 흐름을 설명하기 위한 공간이다.

자세한 코드 리딩 기록은 프로젝트 루트의 아래 문서를 참고한다.

```text
CITYBRAIN_CODE_FLOW.md
code-reading-log.md
```

---

## Core Architecture

```text
Student Web / Android App
        ↓
FastAPI Backend
        ↓
SQLite Database
        ↓
FastAPI Response
        ↓
Student UI / Admin Dashboard
```

---

## Key File Roles

```text
main.py
- FastAPI 서버 시작점
- /student, /admin, /api 요청 처리

database.py
- SQLite 연결
- DB 세션 생성

models.py
- Menu, Feedback, CongestionStatus 테이블 정의

schemas.py
- API 요청/응답 데이터 형태 정의

Student UI
- 메뉴, 혼잡도, 남은 수량 표시

Admin UI
- 메뉴 등록, 혼잡도 입력, 소진 상태 관리

Android Retrofit
- Android 앱이 FastAPI API 호출

gradle.properties
- Android 앱 서버 주소 BASE_URL 설정
```

---

## Core Flow

```text
학생은 조회한다.
관리자는 입력한다.
FastAPI는 연결한다.
SQLite는 저장한다.
Android와 Web은 보여준다.
누적 데이터는 운영 개선 근거가 된다.
```
