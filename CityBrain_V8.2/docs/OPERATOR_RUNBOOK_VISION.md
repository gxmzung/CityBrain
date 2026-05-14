# CityBrain Vision Operator Runbook

## 1. 목적

이 문서는 CityBrain의 YOLO 기반 혼잡도 추정 기능을 로컬 환경에서 실행하고, 학생 화면, 관리자 리포트, 기록 저장, CSV 내보내기를 확인하기 위한 운영 매뉴얼입니다.

## 2. 시스템 구성

CityBrain Vision 기능은 두 개의 서버로 구성됩니다.

    YOLO Vision Module
    http://127.0.0.1:8081

    CityBrain Backend
    http://127.0.0.1:8080

전체 흐름은 다음과 같습니다.

    Camera / Webcam / RTSP
    -> YOLO Person Detection
    -> /api/congestion/latest
    -> CityBrain Backend Proxy
    -> /api/vision/congestion
    -> Student Page / Admin Report / History Log

## 3. 실행 전 확인

다음 사항을 확인합니다.

- Python 가상환경이 생성되어 있는가
- vision/congestion_demo requirements 설치가 되어 있는가
- backend requirements 설치가 되어 있는가
- Mac 카메라 권한이 허용되어 있는가
- 8080, 8081 포트가 비어 있는가

포트 정리:

    lsof -ti :8080 | xargs kill -9 2>/dev/null || true
    lsof -ti :8081 | xargs kill -9 2>/dev/null || true

## 4. YOLO Vision Module 실행

터미널 1에서 실행합니다.

    cd ~/Documents/001.개발/CityBrain/CityBrain_V8.2/vision/congestion_demo
    source .venv/bin/activate
    python app.py

확인 주소:

    http://127.0.0.1:8081/api/congestion/latest

## 5. CityBrain Backend 실행

터미널 2에서 실행합니다.

    cd ~/Documents/001.개발/CityBrain/CityBrain_V8.2/backend
    source ../.venv/bin/activate
    uvicorn app.main:app --host 127.0.0.1 --port 8080 --reload

확인 주소:

    http://127.0.0.1:8080/api/vision/congestion

## 6. 주요 화면

학생용 AI 혼잡도 화면:

    http://127.0.0.1:8080/student/vision-status

관리자용 AI 혼잡도 운영 리포트:

    http://127.0.0.1:8080/admin/vision-report

관리자용 혼잡도 기록 화면:

    http://127.0.0.1:8080/admin/vision-history

## 7. 주요 API

YOLO 원본 API:

    GET http://127.0.0.1:8081/api/congestion/latest

CityBrain Proxy API:

    GET http://127.0.0.1:8080/api/vision/congestion

현재 혼잡도 저장:

    GET http://127.0.0.1:8080/api/vision/history/save

최근 혼잡도 기록 조회:

    GET http://127.0.0.1:8080/api/vision/history

혼잡도 요약 조회:

    GET http://127.0.0.1:8080/api/vision/history/summary

CSV 내보내기:

    GET http://127.0.0.1:8080/api/vision/history/export.csv

## 8. 정상 작동 기준

- YOLO 서버가 8081에서 실행된다.
- /api/congestion/latest가 200 OK를 반환한다.
- CityBrain 서버가 8080에서 실행된다.
- /api/vision/congestion이 200 OK를 반환한다.
- 학생 화면에 혼잡도와 사람 수가 표시된다.
- 관리자 리포트에 운영 판단 메시지가 표시된다.
- 혼잡도 기록 저장 버튼이 작동한다.
- CSV 다운로드가 가능하다.

## 9. 장애 대응

8080 포트 충돌:

    lsof -ti :8080 | xargs kill -9 2>/dev/null || true

8081 포트 충돌:

    lsof -ti :8081 | xargs kill -9 2>/dev/null || true

카메라 권한 문제:

    시스템 설정
    -> 개인정보 보호 및 보안
    -> 카메라
    -> Terminal / Python / VS Code 권한 허용

YOLO 서버가 꺼진 경우 CityBrain /api/vision/congestion은 fallback 응답을 제공합니다.

## 10. 운영 후 정리

YOLO 실행 중 자동으로 yolo11n.pt 파일이 다운로드될 수 있습니다. 이 파일은 GitHub에 올리지 않습니다.

    cd ~/Documents/001.개발/CityBrain/CityBrain_V8.2
    rm -f vision/congestion_demo/yolo11n.pt
    git status

절대 사용 금지:

    git add .

수정한 파일만 명시적으로 추가합니다.

## 11. 파일럿 운영 체크리스트

- [ ] YOLO 8081 서버 실행
- [ ] CityBrain 8080 서버 실행
- [ ] 학생 화면 표시 확인
- [ ] 관리자 리포트 표시 확인
- [ ] 혼잡도 기록 저장 확인
- [ ] CSV 다운로드 확인
- [ ] 영상 원본 저장 여부 확인
- [ ] 안내문 부착 필요 여부 검토
- [ ] 운영 시간대 설정
- [ ] 장애 시 수동 모드 대응 방식 확인
