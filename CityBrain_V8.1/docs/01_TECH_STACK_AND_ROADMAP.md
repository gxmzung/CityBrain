# CityBrain 기술 스택 및 사업화 로드맵

## A. 본선 MVP 스택

### Frontend
- HTML
- CSS
- JavaScript
- Android Kotlin
- Jetpack Compose

### Backend
- Python
- FastAPI
- Uvicorn

### Database
- SQLite

### Android 통신
- Retrofit
- OkHttp
- Gson

### Demo
- 로컬 PC 서버
- 같은 Wi-Fi 내 폰 접속
- 관리자 웹 → API → SQLite → 학생 웹/Android 반영

## B. 학교 시범운영용 스택

### Backend
- FastAPI 유지 또는 Spring Boot 전환
- REST API
- OpenAPI 문서
- 관리자 RBAC 권한

### Database
- PostgreSQL
- Redis

### Frontend Admin
- React
- Next.js
- Tailwind CSS
- Chart.js 또는 Recharts

### Mobile
- Android Kotlin
- Jetpack Compose
- Retrofit
- FCM 알림

### Infra
- Docker
- Nginx
- HTTPS
- GitHub Actions
- Linux 서버 또는 클라우드 VM

### Security
- HTTPS
- 관리자 계정 권한 분리
- 비밀번호 해시
- 감사 로그
- 개인정보 최소 수집

## C. 실제 학교 납품 수준 스택

### Authentication
- 학교 SSO
- OAuth2/OIDC
- 관리자 2FA
- 역할 기반 접근 제어

### Database
- PostgreSQL
- Redis
- 정기 백업
- 장애 복구 정책

### Storage
- 이미지 업로드 저장소
- S3 호환 Object Storage
- 파일 접근 권한 관리

### Observability
- Prometheus
- Grafana
- Sentry
- 서버 로그 수집
- API 응답 시간 모니터링

### QA
- Unit Test
- API Contract Test
- Android UI Test
- 부하 테스트
- 접근성 테스트

### Operations
- 관리자 매뉴얼
- 장애 대응 매뉴얼
- 개인정보 처리방침
- 데이터 보존 기간 정책
- 운영 주체/책임 범위 문서

## D. 스마트캠퍼스 확장 스택

### AI / Data
- Python
- pandas
- scikit-learn
- PyTorch 또는 TensorFlow
- MLflow
- 배치 분석 Job

### Image / NLP
- 시설 고장 이미지 분류
- 텍스트 민원 분류
- 위험도 우선순위 산정
- 자동 담당부서 라우팅

### IoT
- Raspberry Pi
- ESP32
- MQTT
- 익명 인원 카운터
- 조도 센서
- 방치 킥보드/자전거 신고 데이터

### GIS / Map
- Kakao Map API
- Google Maps API
- PostGIS
- 위험 구역 Heatmap
- 캠퍼스 경사/계단/우회 경로 데이터

## E. 스마트시티/사업화 확장 스택

### Architecture
- Multi-tenant SaaS
- 기관별 데이터 분리
- API Gateway
- Rate Limit
- Public API Key

### Data Platform
- PostgreSQL + PostGIS
- Data Warehouse
- BigQuery / Snowflake / Redshift 중 택1
- Kafka 또는 Managed Pub/Sub

### MLOps
- MLflow
- Model Registry
- Batch Inference
- Online Inference
- 데이터 드리프트 모니터링

### Business
- 학교별 SaaS 구독
- 지자체 실증 사업
- 공공시설 운영 데이터 플랫폼
- 유지보수 계약
- 데이터 분석 리포트 제공

## 단계별 계획

1. 본선 MVP: 학생식당 잔여 수량·혼잡도·대기시간 실시간 반영
2. 시범운영: 관리자 수동 입력 + 운영 로그 + 학생 피드백 수집
3. 자동화: 키오스크 판매 로그/API 연동, 익명 인원 카운터 연동
4. 스마트캠퍼스: 시설 신고, 위험 구역, 공간 혼잡도, 모빌리티 질서 확장
5. 스마트시티: 지자체·공공시설 운영 데이터 플랫폼으로 전환
