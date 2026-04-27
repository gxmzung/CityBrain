# CityBrain Release V4

본 패키지는 본선 시연용 MVP와 학교 시범운영/사업화 검토 문서를 함께 포함한다.

## 빠른 실행: Windows PowerShell

```powershell
powershell -ExecutionPolicy Bypass -File .\RUN_DEMO_WINDOWS.ps1
```

이 스크립트는 다음을 자동 처리한다.

1. PC IPv4 탐지
2. `android/gradle.properties`의 `CITYBRAIN_BASE_URL` 자동 설정
3. Python 가상환경 생성
4. 의존성 설치
5. FastAPI 서버 실행: `0.0.0.0:8000`

## 접속 주소

- 학생 웹: `http://127.0.0.1:8000/`
- 관리자 웹: `http://127.0.0.1:8000/admin/login`
- API 문서: `http://127.0.0.1:8000/docs`
- 개인정보 방침 초안: `http://127.0.0.1:8000/privacy`
- 운영 상태: `http://127.0.0.1:8000/status`

## 계정

- 관리자: `admin / change-me-now`
- 운영자: `operator / operator123`
- 조회자: `viewer / viewer123`
- 학생 인증 API: `20260001 / demo1234`

## 폴더 구조

```text
backend/    FastAPI, 관리자 웹, 학생 웹, SQLite, 키오스크 API
android/    Android Studio 프로젝트, Retrofit, Jetpack Compose 학생 앱
docs/       연결 구조, 기술 스택, 디자인 시스템, 개인정보/보안/스토어 체크리스트
scripts/    서버 주소 설정, 백업, 스모크 테스트 보조 스크립트
```

## 오늘 본선용 시연 순서

1. `RUN_DEMO_WINDOWS.ps1` 실행
2. 학생 웹 또는 Android 앱에서 `63개 남음 / 보통 / 7분` 확인
3. 관리자 화면 로그인
4. `-5 판매 · 혼잡` 반영
5. 학생 화면이 `58개 남음 / 혼잡 / 15분`으로 바뀌는 장면 시연

## 정식 납품 전 주의

이 패키지는 본선 및 시범운영 제안용이다. 학교 정식 납품 전에는 SSO, HTTPS, 개인정보 검토, 장애 대응 체계, 백업 자동화, 접근성 QA, 스토어 릴리즈 검증이 필요하다.
