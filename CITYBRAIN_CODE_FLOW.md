# CityBrain Code Flow

## Purpose

이 문서는 CityBrain의 코드 흐름을 한눈에 설명하기 위한 정리 문서이다.

`code-reading-log.md`가 코드별 공부 기록이라면, 이 문서는 전체 데이터 흐름과 발표용 구조 설명에 초점을 둔다.

CityBrain은 단순한 화면 목업이 아니라, 학생 화면, 관리자 화면, FastAPI 백엔드, SQLite 저장 구조, Android 연동 구조가 연결된 MVP이다.

---

## 1. Overall Architecture

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

CityBrain은 학생 화면, 관리자 화면, 백엔드 API, SQLite 저장 구조로 구성된다.

학생은 메뉴, 혼잡도, 남은 수량, 만족도 정보를 확인하고,  
관리자는 메뉴, 혼잡도, 소진 상태를 입력하며,  
백엔드는 이 데이터를 저장하고 다시 화면에 제공한다.

---

## 2. Student Flow

```text
학생이 앱 또는 웹 접속
↓
오늘 메뉴 / 혼잡도 / 남은 수량 확인
↓
FastAPI API 호출
↓
SQLite에서 메뉴·혼잡도 데이터 조회
↓
JSON 또는 화면 응답 반환
↓
학생 화면에 표시
```

학생 입장에서는 직접 식당에 가지 않아도 현재 식당 상태를 확인할 수 있다.

학생이 얻는 가치는 다음과 같다.

```text
1. 오늘 메뉴를 미리 확인할 수 있다.
2. 혼잡도를 보고 방문 시간을 조절할 수 있다.
3. 인기 메뉴의 소진 여부를 보고 헛걸음을 줄일 수 있다.
4. 만족도 정보를 참고해 식사 선택을 할 수 있다.
```

---

## 3. Admin Flow

```text
관리자가 메뉴·혼잡도·소진 상태 입력
↓
FastAPI 관리자 API 호출
↓
입력값 검증
↓
SQLite에 저장
↓
학생 화면에 최신 상태 반영
↓
관리자 대시보드에서 학생 반응 확인
```

관리자 입장에서는 운영 상태를 최소 입력으로 관리하고, 학생 반응을 운영 개선에 활용할 수 있다.

관리자가 얻는 가치는 다음과 같다.

```text
1. 메뉴 상태를 직접 관리할 수 있다.
2. 혼잡도와 소진 상태를 학생에게 빠르게 전달할 수 있다.
3. 학생 만족도와 반응을 확인할 수 있다.
4. 메뉴 수량 조절, 혼잡 대응, 운영 개선의 근거를 만들 수 있다.
```

---

## 4. Key File Roles

| File / Area | Role |
|---|---|
| `main.py` | FastAPI 서버 시작점, API 라우팅 |
| `database.py` | SQLite 연결, DB 세션 생성 |
| `models.py` | Menu, Feedback, CongestionStatus 테이블 구조 정의 |
| `schemas.py` | API 요청/응답 데이터 구조 정의 |
| Student UI | 메뉴, 혼잡도, 남은 수량 표시 |
| Admin UI | 메뉴 등록, 혼잡도 입력, 소진 상태 관리 |
| Android Retrofit | Android 앱과 FastAPI 서버 연결 |
| `gradle.properties` | Android 앱의 서버 BASE_URL 설정 |

---

## 5. Menu Read Flow

```text
GET /api/menus
↓
FastAPI route 실행
↓
DB session 생성
↓
Menu 테이블 조회
↓
메뉴 목록 반환
↓
학생 화면에 표시
```

메뉴 조회 기능은 학생이 식당에 가기 전에 오늘 메뉴를 확인할 수 있게 하는 기본 기능이다.

이 흐름에서 중요한 점은 메뉴가 단순 이미지나 공지가 아니라, DB에 저장된 구조화된 데이터라는 것이다.

---

## 6. Menu Detail Flow

```text
GET /api/menus/{menu_id}
↓
menu_id 기준으로 특정 메뉴 조회
↓
Menu 테이블에서 해당 메뉴 검색
↓
메뉴명 / 가격 / 설명 / 상태 반환
↓
학생 또는 관리자 화면에서 상세 정보 표시
```

메뉴 상세 조회는 학생이 메뉴를 선택하기 전에 필요한 정보를 확인할 수 있게 한다.

관리자 입장에서는 특정 메뉴를 수정하거나 상태를 바꿀 때도 이 구조가 필요하다.

---

## 7. Admin Menu Create Flow

```text
관리자가 메뉴 입력
↓
POST /api/admin/menus
↓
Menu 객체 생성
↓
db.add()
↓
db.commit()
↓
학생 화면에서 새 메뉴 조회 가능
```

관리자 입력과 학생 화면을 연결하는 핵심 흐름이다.

운영자가 메뉴를 등록하면 DB에 저장되고, 학생 화면은 API를 통해 최신 메뉴를 조회한다.

---

## 8. Admin Menu Update Flow

```text
관리자가 기존 메뉴 수정
↓
PUT /api/admin/menus/{menu_id}
↓
menu_id 기준으로 메뉴 조회
↓
변경된 이름 / 가격 / 상태 반영
↓
db.commit()
↓
학생 화면에 수정된 정보 반영
```

실제 식당 운영에서는 메뉴명, 가격, 소진 상태, 운영 상태가 바뀔 수 있다.

따라서 메뉴 등록뿐 아니라 메뉴 수정 흐름도 필요하다.

---

## 9. Stock / Sold-Out Flow

```text
관리자가 메뉴 상태 입력
↓
판매 가능 / 소진 임박 / 소진 완료 상태 선택
↓
Menu.status 또는 stock_status 갱신
↓
DB 저장
↓
학생 화면에 남은 수량 또는 소진 상태 표시
```

이 기능은 학생의 헛걸음을 줄이는 데 직접적으로 연결된다.

학생은 먹고 싶은 메뉴가 남아 있는지 확인하고 식당 방문 여부를 판단할 수 있다.

---

## 10. Congestion Flow

```text
관리자가 현재 혼잡도 입력
↓
POST /api/admin/congestion
↓
CongestionStatus 저장
↓
학생이 GET /api/congestion 호출
↓
최신 혼잡도 반환
↓
학생이 방문 타이밍 판단
```

초기 MVP에서는 운영자 입력 기반으로 시작하고, 이후 결제 로그나 방문 패턴과 연동해 신뢰도를 높일 수 있다.

혼잡도는 단순 상태값이 아니라 시간 정보와 함께 저장되어야 운영 데이터가 된다.

---

## 11. Feedback Flow

```text
학생이 메뉴 만족도 입력
↓
POST /api/feedback
↓
Feedback 테이블 저장
↓
관리자 대시보드에서 집계
↓
메뉴 개선과 운영 판단에 활용
```

학생 만족도는 단순 후기 기능이 아니라, 식당 운영 개선을 위한 데이터로 전환된다.

예를 들어 특정 메뉴의 만족도는 높지만 소진 불만이 많다면, 운영자는 수량 조절을 검토할 수 있다.

---

## 12. Feedback Summary Flow

```text
관리자가 학생 반응 확인
↓
GET /api/admin/feedback-summary
↓
Feedback 데이터 조회
↓
메뉴별 평균 만족도 계산
↓
응답 수 계산
↓
관리자 대시보드에 요약 표시
```

만족도 평균만 보면 위험하다.

응답 수가 적으면 신뢰도가 낮을 수 있으므로 평균 만족도와 응답 수를 함께 봐야 한다.

---

## 13. Android API Flow

```text
Android App 실행
↓
BASE_URL 확인
↓
Retrofit으로 FastAPI API 호출
↓
JSON 응답 수신
↓
Compose 상태값 업데이트
↓
메뉴·혼잡도 화면 표시
```

Android 앱은 직접 DB에 접근하지 않고 FastAPI API를 통해 데이터를 가져온다.

이 구조를 사용하면 웹 화면과 Android 앱이 같은 백엔드 데이터를 공유할 수 있다.

---

## 14. Android BASE_URL Flow

```text
gradle.properties에 서버 주소 설정
↓
BuildConfig.CITYBRAIN_BASE_URL로 앱에서 주소 사용
↓
Retrofit baseUrl에 연결
↓
Android 앱이 FastAPI 서버 호출
```

시연 환경에서는 로컬 PC에서 FastAPI 서버를 실행하고, Android 기기와 같은 Wi-Fi에 연결해 테스트할 수 있다.

이때 서버 IP 주소와 포트가 정확해야 한다.

---

## 15. Student UI State Flow

```text
화면 진입
↓
isLoading = true
↓
API 호출
↓
menus 상태값 업데이트
↓
isLoading = false
↓
Compose 화면 자동 갱신
```

Compose 화면은 상태값이 바뀌면 자동으로 다시 그려진다.

따라서 서버 데이터와 UI 상태를 연결하는 방식이 중요하다.

---

## 16. Admin Input Flow

```text
관리자가 TextField에 메뉴명 입력
↓
상태값 업데이트
↓
저장 버튼 클릭
↓
API 호출
↓
DB 저장
↓
학생 화면에 반영
```

관리자 화면의 핵심은 입력 항목을 많이 만드는 것이 아니다.

운영자가 실제로 부담 없이 쓸 수 있도록 메뉴, 혼잡도, 소진 여부 같은 핵심 항목만 빠르게 입력하게 하는 것이 중요하다.

---

## 17. Error Handling Flow

```text
잘못된 요청 발생
↓
FastAPI에서 HTTPException 반환
↓
Android 또는 Web에서 오류 감지
↓
사용자에게 안내 메시지 표시
```

MVP 단계에서는 기능 검증이 우선이지만, 정식 서비스로 가려면 오류 처리와 운영 안정성이 보강되어야 한다.

예상 가능한 오류는 다음과 같다.

```text
1. 서버가 꺼져 있는 경우
2. Android BASE_URL이 잘못된 경우
3. 메뉴 데이터가 없는 경우
4. 존재하지 않는 menu_id를 요청한 경우
5. 관리자 입력값이 비어 있는 경우
```

---

## 18. Privacy-Minimum Flow

```text
학생 만족도 입력
↓
menu_id / rating / timestamp 저장
↓
개인 식별 정보 없이 메뉴 반응 분석
↓
운영 개선 데이터로 활용
```

초기 MVP는 개인 식별 정보보다 비식별 운영 데이터 중심으로 설계하는 것이 안전하다.

학생 인증이 필요해지더라도 최소 수집 원칙을 적용해야 한다.

---

## 19. Operating Data Value

CityBrain에서 누적되는 데이터는 다음과 같다.

```text
메뉴 데이터
혼잡도 데이터
소진 상태 데이터
학생 만족도 데이터
시간대별 반응 데이터
```

이 데이터는 다음 운영 개선에 활용될 수 있다.

```text
식수 예측
메뉴 수량 조절
혼잡 분산
반복 민원 감소
메뉴 만족도 개선
```

---

## 20. Data Connection Map

```text
Menu
 ├─ Feedback
 │   └─ 메뉴별 만족도 / 응답 수
 ├─ Stock Status
 │   └─ 소진 임박 / 소진 완료
 └─ Congestion
     └─ 시간대별 혼잡 패턴
```

이 연결 구조가 CityBrain의 핵심이다.

단순히 메뉴를 보여주는 앱이 아니라, 학생 반응과 운영 상태를 연결해 식당 운영 개선의 근거를 만든다.

---

## 21. Presentation Message

CityBrain은 단순한 학생 편의 앱이 아니라,  
학생의 식사 선택과 식당 운영 개선을 연결하는 스마트캠퍼스 서비스이다.

학생은 메뉴, 혼잡도, 남은 수량, 만족도를 확인해 더 나은 식사 선택을 할 수 있고,  
학교와 식당은 메뉴 만족도, 혼잡 시간대, 소진 불만, 학생 반응 데이터를 기반으로 운영을 개선할 수 있다.

핵심은 다음 문장으로 정리된다.

> CityBrain의 학교 측 ROI는 이미지가 아니라 운영 데이터다.

---

## 22. Judge Defense Points

### Q1. 기존 학교 앱과 무엇이 다른가?

기존 학교 앱은 공지와 정보 전달 중심이다.  
CityBrain은 학생식당이라는 구체적인 생활 문제를 중심으로 메뉴, 혼잡도, 만족도, 소진 데이터를 연결해 운영 개선까지 이어지는 서비스이다.

### Q2. 혼잡도나 남은 수량은 어떻게 믿을 수 있는가?

초기 MVP에서는 운영자 입력과 학생 체감 입력으로 시작한다.  
이후에는 키오스크 로그, 결제 건수, 시간대별 방문 패턴과 연동해 신뢰도를 높일 수 있다.

### Q3. 직원 입력 부담이 커지지 않는가?

핵심은 많은 입력이 아니라 최소 입력이다.  
메뉴 상태, 혼잡도, 소진 여부처럼 운영에 필요한 항목만 빠르게 입력하도록 설계한다.

### Q4. 학교가 얻는 이득은 무엇인가?

가장 큰 이득은 운영 데이터다.  
메뉴 만족도, 혼잡 시간대, 소진 불만 데이터를 통해 식수 예측, 수량 조절, 혼잡 분산, 민원 감소에 활용할 수 있다.

### Q5. 개인정보 문제는 없는가?

초기 MVP는 개인 식별 정보 없이 메뉴 반응, 시간대, 혼잡 체감 같은 비식별 운영 데이터를 중심으로 설계한다.  
학생 인증이 필요해도 최소 수집 원칙을 적용해야 한다.

---

## 23. Final Summary

CityBrain의 구조는 다음과 같이 정리할 수 있다.

```text
학생은 조회한다.
관리자는 입력한다.
FastAPI는 연결한다.
SQLite는 저장한다.
Android와 Web은 보여준다.
누적 데이터는 운영 개선 근거가 된다.
```

따라서 CityBrain은 단순 메뉴 안내 앱이 아니라, 학생식당 운영 데이터를 만드는 스마트캠퍼스 MVP이다.
