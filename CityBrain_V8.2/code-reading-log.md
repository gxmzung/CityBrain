# CityBrain Code Reading Log

## Purpose

이 문서는 CityBrain 프로젝트의 핵심 코드 흐름을 공부하면서 정리한 코드 리딩 로그이다.

목적은 단순히 기능 목록을 적는 것이 아니라, 학생 화면, 관리자 화면, FastAPI 백엔드, SQLite 저장 구조, Android 연동 구조가 어떻게 연결되는지 설명할 수 있게 만드는 것이다.

> Note  
> 이 문서는 실제 구현 코드를 읽기 위한 학습용 정리이며, 파일명이나 함수명은 실제 프로젝트 구조에 맞춰 계속 보완한다.

---

## 01. FastAPI Backend Entry Point

### File
`main.py` 또는 FastAPI 앱 실행 파일

### 역할
CityBrain 백엔드 서버의 시작점이다.  
학생 화면, 관리자 화면, Android 앱에서 들어오는 요청을 처리하는 FastAPI 애플리케이션을 생성한다.

### Code-by-Code Reading

```python
from fastapi import FastAPI
```

FastAPI 프레임워크에서 서버 애플리케이션을 만들기 위한 클래스를 가져오는 코드다.  
CityBrain 백엔드는 이 FastAPI 객체를 중심으로 메뉴 조회, 혼잡도 조회, 관리자 입력, 만족도 저장 같은 기능을 API로 제공한다.

```python
app = FastAPI()
```

FastAPI 애플리케이션 객체를 생성하는 부분이다.  
이 `app` 객체에 `@app.get`, `@app.post` 같은 라우터가 붙으면서 실제 API 서버가 된다.  
처음에는 단순한 한 줄처럼 보이지만, CityBrain의 백엔드 흐름 전체가 이 객체를 기준으로 시작된다.

```python
@app.get("/")
```

루트 경로에 대한 GET 요청을 처리한다.  
브라우저에서 서버 주소에 접속했을 때 서버가 정상 실행 중인지 확인하는 기본 진입점으로 사용할 수 있다.

```python
def root():
```

루트 경로 요청이 들어왔을 때 실행되는 함수다.  
FastAPI에서는 데코레이터 아래에 있는 함수가 해당 URL의 처리 로직이 된다.

```python
return {"message": "CityBrain"}
```

Python 딕셔너리를 반환하면 FastAPI가 자동으로 JSON 응답으로 변환한다.  
이 구조 덕분에 Android 앱이나 웹 화면에서 API 응답을 받아 사용할 수 있다.

### 이해한 점
CityBrain은 단순 HTML 화면이 아니라, FastAPI 서버를 기준으로 학생용 화면과 관리자용 화면, Android 앱이 데이터를 주고받을 수 있는 구조다.

### 발표 포인트
화면만 만든 것이 아니라 FastAPI 기반 백엔드 API를 통해 학생 화면과 관리자 화면이 데이터를 주고받는 구조로 설계했다.

---

## 02. Database Connection

### File
`database.py` 또는 DB 연결 파일

### 역할
SQLite 데이터베이스와 연결하고, 메뉴·혼잡도·만족도 데이터를 저장하거나 조회할 수 있게 하는 기반 코드다.

### Code-by-Code Reading

```python
from sqlalchemy import create_engine
```

SQLAlchemy에서 데이터베이스 연결 엔진을 만들기 위한 함수를 가져온다.  
FastAPI 자체는 DB 기능을 제공하지 않기 때문에 SQLAlchemy 같은 ORM을 사용해 DB와 연결한다.

```python
from sqlalchemy.orm import sessionmaker
```

DB 요청마다 사용할 세션을 만들기 위한 도구다.  
세션은 Python 코드와 실제 DB 사이에서 데이터를 읽고 쓰는 작업 단위라고 볼 수 있다.

```python
from sqlalchemy.ext.declarative import declarative_base
```

SQLAlchemy 모델 클래스들이 상속받을 기본 클래스를 만들기 위한 함수다.  
메뉴, 피드백, 혼잡도 같은 테이블 구조를 Python 클래스로 정의할 수 있게 해준다.

```python
DATABASE_URL = "sqlite:///./citybrain.db"
```

사용할 데이터베이스 위치를 지정한다.  
SQLite는 별도 서버가 필요 없기 때문에 MVP나 로컬 데모에 적합하다.  
CityBrain 같은 시범 운영용 프로젝트에서는 빠르게 구현하고 검증하기 좋다.

```python
engine = create_engine(DATABASE_URL)
```

실제 DB 연결 엔진을 생성한다.  
API에서 메뉴를 조회하거나 관리자 입력을 저장할 때 이 엔진을 통해 DB와 연결된다.

```python
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
```

DB 세션을 생성하는 설정이다.  
요청이 들어올 때마다 세션을 열고, 필요한 데이터를 조회하거나 저장한 뒤 닫는 흐름으로 사용된다.

```python
Base = declarative_base()
```

모델 클래스들이 상속받는 기본 클래스다.  
예를 들어 `class Menu(Base)`처럼 작성하면 Menu 클래스가 실제 DB 테이블과 연결될 수 있다.

### 이해한 점
CityBrain은 데이터를 화면에 하드코딩해서 보여주는 구조가 아니라, DB에 저장하고 API를 통해 조회하는 구조로 확장할 수 있다.

### 발표 포인트
초기 MVP는 SQLite를 사용해 가볍게 시작하지만, 메뉴·만족도·혼잡도 데이터를 저장하고 조회할 수 있는 구조로 설계했다.

---

## 03. Menu Model

### File
`models.py`

### 역할
학생식당 메뉴 데이터를 저장하기 위한 테이블 구조를 정의한다.

### Code-by-Code Reading

```python
class Menu(Base):
```

메뉴 테이블에 대응되는 Python 클래스를 정의한다.  
SQLAlchemy에서는 테이블을 클래스로 표현하고, 컬럼을 클래스 속성으로 정의한다.

```python
__tablename__ = "menus"
```

실제 DB에서 사용할 테이블 이름을 지정한다.  
이 경우 메뉴 데이터는 `menus`라는 테이블에 저장된다.

```python
id = Column(Integer, primary_key=True, index=True)
```

메뉴 데이터를 구분하는 고유 ID다.  
각 메뉴를 조회하거나 수정할 때 기준이 된다.  
`primary_key=True`는 이 값이 테이블의 기본 식별자라는 의미다.

```python
name = Column(String)
```

메뉴명을 저장하는 컬럼이다.  
학생 화면에서 가장 직접적으로 보이는 데이터다.

```python
price = Column(Integer)
```

메뉴 가격을 저장하는 컬럼이다.  
학생이 메뉴를 고를 때 가격은 중요한 판단 기준이 된다.

```python
description = Column(String)
```

메뉴 설명을 저장하는 컬럼이다.  
반찬 구성이나 메뉴 특징을 보여줄 때 사용할 수 있다.

```python
status = Column(String)
```

메뉴의 현재 상태를 저장할 수 있는 컬럼이다.  
예를 들어 `available`, `low_stock`, `sold_out` 같은 상태로 확장할 수 있다.

```python
created_at = Column(DateTime)
```

메뉴 데이터가 언제 생성되었는지 저장하는 컬럼이다.  
운영 데이터가 누적되면 날짜별 메뉴 반응 분석에 활용할 수 있다.

### 이해한 점
메뉴는 단순 텍스트가 아니라 ID, 이름, 가격, 설명, 상태값을 가진 구조화된 데이터로 다뤄야 한다.

### 발표 포인트
메뉴 데이터를 구조화해두면 이후 만족도, 소진 여부, 혼잡도와 연결해 운영 데이터로 확장할 수 있다.

---

## 04. Menu List API

### File
`main.py`, `routes/menu.py` 또는 메뉴 API 파일

### 역할
학생 화면이나 Android 앱에서 오늘 메뉴 목록을 조회할 수 있게 한다.

### Code-by-Code Reading

```python
@app.get("/api/menus")
```

메뉴 목록 조회 API를 정의한다.  
학생 웹이나 Android 앱은 이 주소로 요청을 보내 메뉴 데이터를 받을 수 있다.

```python
def get_menus():
```

메뉴 조회 요청이 들어왔을 때 실행되는 함수다.  
함수 내부에서 DB를 조회하고 결과를 반환한다.

```python
db = SessionLocal()
```

DB 세션을 여는 코드다.  
메뉴 데이터를 조회하기 위해 데이터베이스와 연결한다.

```python
menus = db.query(Menu).all()
```

`menus` 테이블에 저장된 모든 메뉴 데이터를 조회한다.  
초기 MVP에서는 전체 조회로 시작할 수 있고, 이후 날짜별·식당별 조회로 확장할 수 있다.

```python
return menus
```

조회한 메뉴 목록을 클라이언트에 반환한다.  
FastAPI는 반환값을 JSON 형태로 바꿔 웹이나 앱에서 사용할 수 있게 한다.

### 이해한 점
학생 화면에서 보이는 메뉴는 API를 통해 가져오는 데이터여야 하며, 이렇게 해야 관리자 입력과 학생 화면이 연결된다.

### 발표 포인트
학생은 종이 공지나 단순 이미지가 아니라 API를 통해 최신 메뉴 데이터를 확인할 수 있다.

---

## 05. Menu Detail API

### File
`routes/menu.py` 또는 메뉴 상세 API 파일

### 역할
특정 메뉴의 상세 정보를 조회한다.

### Code-by-Code Reading

```python
@app.get("/api/menus/{menu_id}")
```

특정 메뉴 ID를 기준으로 상세 정보를 조회하는 API다.  
`{menu_id}`는 URL 경로에서 변수처럼 사용된다.

```python
def get_menu(menu_id: int):
```

URL에서 받은 `menu_id`를 정수형으로 받아 처리한다.  
FastAPI는 타입 힌트를 보고 자동으로 값 검증을 수행한다.

```python
menu = db.query(Menu).filter(Menu.id == menu_id).first()
```

DB에서 ID가 일치하는 메뉴 하나를 찾는다.  
메뉴 상세 화면이나 관리자 수정 화면에서 필요한 조회 방식이다.

```python
if menu is None:
```

해당 ID의 메뉴가 없을 때를 처리하기 위한 조건문이다.  
실제 서비스에서는 없는 데이터를 요청했을 때 오류 처리가 필요하다.

```python
return menu
```

찾은 메뉴 정보를 반환한다.  
학생은 메뉴명뿐 아니라 가격, 상태, 설명, 만족도 같은 정보를 확인할 수 있다.

### 이해한 점
목록 조회와 상세 조회를 분리하면 화면 구조가 더 명확해진다.  
목록은 빠르게 훑는 용도, 상세는 선택 전 판단용이다.

### 발표 포인트
메뉴 상세 정보는 학생이 식당에 가기 전에 선택을 돕는 정보로 활용될 수 있다.

---

## 06. Admin Menu Create API

### File
관리자 메뉴 등록 API

### 역할
운영자가 오늘 메뉴를 등록할 수 있게 한다.

### Code-by-Code Reading

```python
@app.post("/api/admin/menus")
```

관리자 메뉴 등록 API를 정의한다.  
GET이 조회라면 POST는 새로운 데이터를 생성하는 요청이다.

```python
def create_menu(menu: MenuCreate):
```

클라이언트가 보낸 메뉴 데이터를 `MenuCreate` 형태로 받는다.  
스키마를 사용하면 입력 데이터의 형식을 검증할 수 있다.

```python
new_menu = Menu(
    name=menu.name,
    price=menu.price,
    description=menu.description,
)
```

입력받은 데이터를 기반으로 DB에 저장할 Menu 객체를 만든다.  
관리자가 입력한 메뉴 정보가 실제 DB 저장 객체로 변환되는 부분이다.

```python
db.add(new_menu)
```

새 메뉴 객체를 DB 세션에 추가한다.  
아직 최종 저장은 아니고 저장 대기 상태다.

```python
db.commit()
```

DB에 변경사항을 실제로 반영한다.  
이 시점부터 학생 화면에서도 새 메뉴를 조회할 수 있다.

```python
db.refresh(new_menu)
```

DB에 저장된 최신 상태를 다시 불러온다.  
자동 생성된 ID 같은 값이 반영된다.

```python
return new_menu
```

등록된 메뉴 정보를 응답으로 반환한다.  
관리자 화면에서는 등록 성공 여부를 확인할 수 있다.

### 이해한 점
관리자 입력이 DB에 저장되고, 그 데이터가 학생 화면에서 조회되는 구조가 CityBrain의 핵심 연결 지점이다.

### 발표 포인트
운영자가 메뉴를 등록하면 학생 화면에 반영되는 관리자-학생 연결 구조를 만들었다.

---

## 07. Admin Menu Update API

### File
관리자 메뉴 수정 API

### 역할
기존 메뉴 정보를 수정한다.

### Code-by-Code Reading

```python
@app.put("/api/admin/menus/{menu_id}")
```

특정 메뉴 ID를 기준으로 메뉴 정보를 수정하는 API다.  
PUT은 기존 데이터를 갱신할 때 사용한다.

```python
def update_menu(menu_id: int, payload: MenuUpdate):
```

수정할 메뉴 ID와 새로 입력된 데이터를 함께 받는다.

```python
menu = db.query(Menu).filter(Menu.id == menu_id).first()
```

수정 대상 메뉴를 DB에서 찾는다.

```python
menu.name = payload.name
```

기존 메뉴명의 값을 새 입력값으로 바꾼다.

```python
menu.price = payload.price
```

기존 가격 정보를 새 값으로 바꾼다.

```python
menu.status = payload.status
```

메뉴의 현재 상태를 갱신한다.  
예를 들어 판매 가능, 소진 임박, 소진 완료 같은 상태를 반영할 수 있다.

```python
db.commit()
```

수정된 내용을 DB에 저장한다.

```python
return menu
```

수정된 메뉴 정보를 반환한다.

### 이해한 점
실제 식당 운영에서는 메뉴 정보가 바뀔 수 있기 때문에 등록뿐 아니라 수정 기능도 필요하다.

### 발표 포인트
운영 중 메뉴 상태나 수량이 변할 수 있기 때문에 관리자 수정 기능이 필요하다.

---

## 08. Menu Stock / Sold Out Status

### File
메뉴 상태 갱신 API 또는 관리자 상태 관리 코드

### 역할
인기 메뉴의 남은 수량, 소진 임박, 소진 완료 상태를 관리한다.

### Code-by-Code Reading

```python
stock_status = payload.stock_status
```

관리자 화면에서 입력한 메뉴 상태값을 읽는다.  
이 값은 학생 화면에서 남은 수량 또는 소진 여부로 표시될 수 있다.

```python
menu.status = stock_status
```

메뉴 객체의 상태값을 새 상태로 갱신한다.

```python
db.commit()
```

상태 변경을 DB에 저장한다.

```python
return {"status": menu.status}
```

변경된 상태를 응답으로 반환한다.  
학생 화면은 이 값을 보고 “판매중”, “소진 임박”, “소진 완료” 같은 UI를 보여줄 수 있다.

### 이해한 점
남은 수량 기능은 학생 편의뿐 아니라 헛걸음과 불만 감소에 직접 연결된다.

### 발표 포인트
인기 메뉴 소진 정보를 제공하면 학생의 헛걸음을 줄이고, 반복 민원을 줄일 수 있다.

---

## 09. Congestion Status Model

### File
혼잡도 모델 또는 상태 테이블

### 역할
식당의 혼잡도 상태를 저장한다.

### Code-by-Code Reading

```python
class CongestionStatus(Base):
```

혼잡도 데이터를 저장하기 위한 모델 클래스다.

```python
__tablename__ = "congestion_status"
```

혼잡도 데이터가 저장될 테이블 이름을 지정한다.

```python
level = Column(String)
```

혼잡도 수준을 저장한다.  
예를 들어 `low`, `normal`, `high` 같은 값이 들어갈 수 있다.

```python
updated_at = Column(DateTime)
```

혼잡도 정보가 언제 갱신되었는지 저장한다.  
혼잡도는 시간이 중요한 데이터이므로 갱신 시각이 반드시 필요하다.

### 이해한 점
혼잡도는 단순 상태값이 아니라 시간 정보와 함께 저장되어야 운영 데이터가 된다.

### 발표 포인트
혼잡도는 현재 상태뿐 아니라 시간대별 패턴 분석으로 확장할 수 있다.

---

## 10. Congestion Read API

### File
혼잡도 조회 API

### 역할
학생이 현재 식당 혼잡도를 확인할 수 있게 한다.

### Code-by-Code Reading

```python
@app.get("/api/congestion")
```

현재 혼잡도 정보를 조회하는 API다.

```python
def get_congestion():
```

혼잡도 조회 요청이 들어왔을 때 실행되는 함수다.

```python
status = db.query(CongestionStatus).order_by(CongestionStatus.updated_at.desc()).first()
```

가장 최근에 갱신된 혼잡도 데이터를 가져온다.  
혼잡도는 최신성이 중요하기 때문에 시간 기준 정렬이 필요하다.

```python
return status
```

현재 혼잡도 정보를 반환한다.

### 이해한 점
학생에게 보여줄 혼잡도는 최신 데이터여야 하며, 갱신 시각과 함께 관리해야 신뢰도를 설명할 수 있다.

### 발표 포인트
초기 MVP에서는 운영자 입력 기반으로 시작하고, 이후 결제 건수나 방문 패턴과 연동해 신뢰도를 높일 수 있다.

---

## 11. Admin Congestion Update API

### File
관리자 혼잡도 입력 API

### 역할
운영자가 현재 식당 혼잡도를 입력하거나 갱신한다.

### Code-by-Code Reading

```python
@app.post("/api/admin/congestion")
```

관리자 혼잡도 입력 API다.

```python
def update_congestion(payload: CongestionUpdate):
```

관리자가 입력한 혼잡도 값을 받아 처리한다.

```python
new_status = CongestionStatus(level=payload.level)
```

입력값을 기반으로 새로운 혼잡도 상태 객체를 만든다.

```python
new_status.updated_at = datetime.now()
```

혼잡도 갱신 시각을 현재 시간으로 기록한다.

```python
db.add(new_status)
```

새 혼잡도 상태를 DB 세션에 추가한다.

```python
db.commit()
```

혼잡도 상태를 DB에 저장한다.

```python
return new_status
```

저장된 혼잡도 정보를 반환한다.

### 이해한 점
초기 MVP에서 혼잡도 예측을 무리하게 구현하기보다, 운영자 최소 입력 방식이 더 현실적이다.

### 발표 포인트
운영자 입력 부담을 최소화하면서도 학생에게는 현재 상태를 제공하는 방식으로 시작할 수 있다.

---

## 12. Feedback Model

### File
`models.py`

### 역할
학생 만족도와 반응 데이터를 저장한다.

### Code-by-Code Reading

```python
class Feedback(Base):
```

학생 피드백 데이터를 저장하기 위한 모델 클래스다.

```python
__tablename__ = "feedbacks"
```

피드백 데이터가 저장될 테이블 이름을 지정한다.

```python
menu_id = Column(Integer)
```

어떤 메뉴에 대한 피드백인지 연결하기 위한 값이다.

```python
rating = Column(Integer)
```

학생이 남긴 만족도 점수다.  
메뉴별 평균 만족도를 계산하는 데 사용된다.

```python
comment = Column(String)
```

학생이 남긴 간단한 의견이다.  
정량 점수로 보기 어려운 불만이나 개선 의견을 확인할 수 있다.

```python
created_at = Column(DateTime)
```

피드백이 언제 작성되었는지 저장한다.

### 이해한 점
만족도 데이터는 단순 후기 기능이 아니라, 메뉴 개선과 운영 의사결정의 근거가 된다.

### 발표 포인트
학생 피드백은 운영자가 메뉴 반응을 확인할 수 있는 데이터로 전환된다.

---

## 13. Feedback Create API

### File
만족도 입력 API

### 역할
학생이 메뉴에 대한 만족도와 반응을 남길 수 있게 한다.

### Code-by-Code Reading

```python
@app.post("/api/feedback")
```

학생 만족도 입력 API다.

```python
def create_feedback(feedback: FeedbackCreate):
```

학생이 보낸 피드백 데이터를 받아 처리한다.

```python
new_feedback = Feedback(
    menu_id=feedback.menu_id,
    rating=feedback.rating,
    comment=feedback.comment,
)
```

입력 데이터를 DB에 저장할 Feedback 객체로 변환한다.

```python
db.add(new_feedback)
```

새 피드백을 DB 세션에 추가한다.

```python
db.commit()
```

피드백 데이터를 DB에 저장한다.

```python
return {"message": "feedback saved"}
```

저장 완료 응답을 반환한다.

### 이해한 점
학생 반응은 별도 설문조사가 아니라 식사 후 간단한 입력으로도 수집할 수 있다.

### 발표 포인트
학생의 작은 반응이 누적되면 학교와 식당이 메뉴 개선에 활용할 수 있는 운영 데이터가 된다.

---

## 14. Feedback Summary API

### File
관리자 피드백 요약 API

### 역할
메뉴별 만족도 평균과 응답 수를 계산해 관리자에게 보여준다.

### Code-by-Code Reading

```python
@app.get("/api/admin/feedback-summary")
```

관리자용 피드백 요약 API다.

```python
feedbacks = db.query(Feedback).all()
```

저장된 모든 피드백 데이터를 조회한다.

```python
ratings = [f.rating for f in feedbacks]
```

피드백 목록에서 만족도 점수만 뽑아낸다.

```python
average_rating = sum(ratings) / len(ratings)
```

평균 만족도를 계산한다.  
단, 실제 코드에서는 `len(ratings) == 0`인 경우도 처리해야 한다.

```python
count = len(feedbacks)
```

응답 수를 계산한다.  
평균 점수만 보는 것보다 응답 수를 함께 보는 것이 중요하다.

```python
return {"average_rating": average_rating, "count": count}
```

관리자 화면에서 사용할 요약 데이터를 반환한다.

### 이해한 점
만족도 평균과 응답 수는 식당 운영자가 메뉴 반응을 빠르게 이해하는 데 필요한 기본 지표다.

### 발표 포인트
학교와 식당은 메뉴별 만족도와 응답 수를 함께 보면서 감이 아니라 데이터 기반으로 판단할 수 있다.

---

## 15. Admin Dashboard Flow

### File
관리자 대시보드 화면

### 역할
운영자가 메뉴, 혼잡도, 학생 반응을 한 곳에서 확인하고 관리할 수 있게 한다.

### Code-by-Code Reading

```python
@app.get("/admin")
```

관리자 화면에 접근하는 경로다.

```python
menus = db.query(Menu).all()
```

관리자 화면에서 표시할 메뉴 목록을 조회한다.

```python
feedbacks = db.query(Feedback).all()
```

학생 반응 데이터를 조회한다.

```python
congestion = db.query(CongestionStatus).first()
```

현재 혼잡도 상태를 조회한다.

```python
return templates.TemplateResponse(...)
```

조회한 데이터를 관리자 화면 템플릿에 전달해 렌더링한다.

### 이해한 점
관리자 화면은 단순 입력 화면이 아니라, 학생 반응과 운영 상태를 한 곳에서 볼 수 있는 대시보드 역할을 해야 한다.

### 발표 포인트
관리자는 메뉴 등록뿐 아니라 학생 반응과 혼잡 상태를 보고 운영을 조정할 수 있다.

---

## 16. Student Web Flow

### File
학생용 웹 화면

### 역할
학생이 오늘 메뉴, 혼잡도, 남은 수량, 만족도 정보를 확인할 수 있게 한다.

### Code-by-Code Reading

```python
@app.get("/student")
```

학생용 화면에 접근하는 경로다.

```python
menus = db.query(Menu).all()
```

학생 화면에 보여줄 메뉴 데이터를 조회한다.

```python
status = db.query(CongestionStatus).first()
```

현재 혼잡도 정보를 조회한다.

```python
return templates.TemplateResponse("student.html", {...})
```

메뉴와 혼잡도 데이터를 학생 화면에 전달한다.

### 이해한 점
학생 화면의 핵심은 “직접 가보기 전에 확인할 수 있는 정보”를 한 번에 제공하는 것이다.

### 발표 포인트
학생은 앱이나 웹에서 메뉴, 혼잡도, 소진 상태를 미리 확인해 헛걸음을 줄일 수 있다.

---

## 17. Android Retrofit Interface

### File
`ApiService.kt`

### 역할
Android 앱이 FastAPI 서버와 통신할 수 있게 API 목록을 정의한다.

### Code-by-Code Reading

```kotlin
interface ApiService
```

Android 앱에서 사용할 API 목록을 정의하는 인터페이스다.

```kotlin
@GET("api/menus")
```

서버의 메뉴 조회 API를 호출한다.

```kotlin
suspend fun getMenus(): List<Menu>
```

메뉴 목록을 비동기 방식으로 가져온다.  
`suspend`는 Kotlin Coroutine에서 사용하는 키워드로, 네트워크 요청 중 UI가 멈추지 않게 한다.

```kotlin
@GET("api/congestion")
```

현재 혼잡도 정보를 가져오는 API다.

```kotlin
@POST("api/feedback")
```

학생 만족도 데이터를 서버로 보내는 API다.

### 이해한 점
Android 앱은 직접 DB를 보는 것이 아니라 FastAPI 서버의 API를 호출해서 데이터를 가져온다.

### 발표 포인트
웹과 Android 앱이 같은 백엔드 API를 사용할 수 있도록 구조를 나눴다.

---

## 18. Android Retrofit Client

### File
`RetrofitClient.kt`

### 역할
Android 앱에서 서버와 통신할 Retrofit 객체를 생성한다.

### Code-by-Code Reading

```kotlin
Retrofit.Builder()
```

Retrofit 객체를 생성하기 위한 빌더를 시작한다.

```kotlin
.baseUrl(BuildConfig.CITYBRAIN_BASE_URL)
```

서버 기본 주소를 설정한다.  
이 값은 로컬 PC의 FastAPI 서버 주소가 될 수 있다.

```kotlin
.addConverterFactory(GsonConverterFactory.create())
```

서버에서 받은 JSON 응답을 Kotlin 객체로 변환하기 위한 설정이다.

```kotlin
.build()
```

설정한 값들을 기반으로 Retrofit 객체를 완성한다.

```kotlin
.create(ApiService::class.java)
```

앞에서 정의한 API 인터페이스를 실제 호출 가능한 객체로 만든다.

### 이해한 점
Retrofit은 Android 앱과 FastAPI 서버 사이의 통신 계층이다.

### 발표 포인트
Android 앱은 Retrofit을 통해 FastAPI 서버와 통신하며, JSON 데이터를 화면에 표시한다.

---

## 19. Android BASE_URL Setting

### File
`gradle.properties`

### 역할
Android 앱이 접속할 백엔드 서버 주소를 설정한다.

### Code-by-Code Reading

```properties
CITYBRAIN_BASE_URL=http://192.168.x.x:8000/
```

Android 앱이 접속할 서버 주소를 지정한다.  
실제 기기에서 테스트할 때는 PC와 스마트폰이 같은 Wi-Fi에 있어야 한다.

```kotlin
BuildConfig.CITYBRAIN_BASE_URL
```

Gradle 설정에 저장된 서버 주소를 앱 코드에서 불러온다.  
서버 주소를 코드에 직접 박아두지 않고 설정으로 관리할 수 있다.

### 이해한 점
로컬 서버와 실제 Android 기기를 연결하려면 IP 주소와 네트워크 환경이 중요하다.

### 발표 포인트
시연 환경에서는 로컬 FastAPI 서버와 실제 Android 기기를 같은 Wi-Fi에 연결해 테스트할 수 있다.

---

## 20. Student Compose Screen State

### File
학생용 Android Compose 화면

### 역할
학생 화면에서 메뉴 목록, 로딩 상태, 오류 상태를 관리한다.

### Code-by-Code Reading

```kotlin
var isLoading by remember { mutableStateOf(false) }
```

데이터를 불러오는 중인지 저장하는 상태값이다.

```kotlin
var menus by remember { mutableStateOf(emptyList<Menu>()) }
```

서버에서 받아온 메뉴 목록을 화면 상태로 저장한다.

```kotlin
var errorMessage by remember { mutableStateOf<String?>(null) }
```

네트워크 오류나 서버 오류가 발생했을 때 보여줄 메시지를 저장한다.

```kotlin
LaunchedEffect(Unit) { ... }
```

화면이 처음 표시될 때 API 호출 같은 작업을 실행한다.

```kotlin
menus.forEach { menu -> ... }
```

서버에서 받아온 메뉴 목록을 화면에 반복해서 표시한다.

### 이해한 점
Compose 화면은 상태값이 바뀌면 자동으로 다시 그려진다.  
따라서 서버 데이터와 UI 상태를 연결하는 방식이 중요하다.

### 발표 포인트
학생 화면은 고정 이미지가 아니라 서버에서 받은 데이터를 상태로 관리하고 화면에 반영한다.

---

## 21. Admin Input UI

### File
관리자 Android 또는 웹 입력 화면

### 역할
운영자가 메뉴명, 혼잡도, 소진 상태 등을 입력할 수 있게 한다.

### Code-by-Code Reading

```kotlin
TextField(value = menuName, onValueChange = { menuName = it })
```

관리자가 메뉴명을 입력하는 UI다.  
입력값이 바뀔 때마다 `menuName` 상태값이 갱신된다.

```kotlin
TextField(value = price, onValueChange = { price = it })
```

가격 입력 필드다.  
실제 저장 전에는 숫자 검증이 필요하다.

```kotlin
Button(onClick = { ... })
```

입력한 값을 저장하는 동작을 실행하는 버튼이다.

```kotlin
api.createMenu(...)
```

관리자 입력값을 서버의 메뉴 등록 API로 보낸다.

### 이해한 점
관리자 화면의 핵심은 많은 정보를 입력하게 하는 것이 아니라, 운영에 필요한 최소 정보만 빠르게 입력하게 하는 것이다.

### 발표 포인트
운영자 입력 부담을 줄이기 위해 필요한 항목만 빠르게 입력하는 구조가 중요하다.

---

## 22. Error Handling

### File
백엔드 API 또는 Android 네트워크 코드

### 역할
데이터 없음, 서버 오류, 네트워크 실패 상황에 대응한다.

### Code-by-Code Reading

```python
raise HTTPException(status_code=404, detail="Menu not found")
```

요청한 메뉴가 없을 때 404 오류를 반환한다.  
잘못된 요청에 대해 명확한 응답을 주는 코드다.

```python
try:
    ...
except Exception as e:
    ...
```

예상하지 못한 오류를 처리하는 구조다.

```kotlin
try {
    val menus = api.getMenus()
} catch (e: Exception) {
    errorMessage = e.message
}
```

Android 앱에서 네트워크 요청 실패를 처리하는 구조다.  
서버가 꺼져 있거나 Wi-Fi가 맞지 않으면 예외가 발생할 수 있다.

### 이해한 점
정식 서비스로 가려면 오류 처리, 장애 대응, 운영 로그가 더 필요하다.

### 발표 포인트
현재 MVP는 기능 검증 중심이며, 정식 서비스 전에는 오류 처리와 운영 안정성을 더 보강해야 한다.

---

## 23. Privacy-Minimum Feedback Design

### File
피드백 저장 구조 또는 정책 메모

### 역할
개인 식별 정보를 최소화하면서도 운영 개선에 필요한 데이터를 수집한다.

### Code-by-Code Reading

```text
menu_id
```

어떤 메뉴에 대한 반응인지 알기 위한 값이다.

```text
rating
```

학생 만족도를 수치화한 값이다.

```text
timestamp
```

언제 입력된 반응인지 알기 위한 값이다.

```text
anonymous feedback
```

학생 이름이나 학번 없이도 메뉴 반응과 시간대별 만족도를 분석할 수 있다.

### 이해한 점
초기 MVP에서는 학생 개인정보보다 비식별 운영 데이터 중심으로 설계하는 것이 안전하다.

### 발표 포인트
초기 MVP는 개인 식별 정보 없이 메뉴 반응, 시간대, 혼잡 체감 같은 비식별 운영 데이터를 중심으로 설계한다.

---

## 24. Operating Data Expansion

### File
운영 리포트 또는 향후 분석 모듈

### 역할
메뉴, 만족도, 혼잡도, 소진 데이터를 연결해 운영 개선 근거로 확장한다.

### Code-by-Code Reading

```text
menus + feedback
```

메뉴와 만족도를 연결하면 어떤 메뉴가 좋은 반응을 얻었는지 확인할 수 있다.

```text
menus + stock_status
```

메뉴와 소진 상태를 연결하면 어떤 메뉴가 빨리 소진되는지 볼 수 있다.

```text
congestion + timestamp
```

혼잡도와 시간을 연결하면 특정 시간대의 반복 혼잡을 파악할 수 있다.

```text
feedback + congestion
```

혼잡한 시간대의 만족도가 낮아지는지 같은 운영 인사이트를 얻을 수 있다.

### 이해한 점
CityBrain의 핵심은 화면 기능이 아니라, 흩어진 운영 데이터를 연결해 의사결정 근거로 만드는 것이다.

### 발표 포인트
CityBrain의 학교 측 ROI는 이미지가 아니라 운영 데이터다.

---

## 25. Smart Campus Expansion

### File
로드맵 또는 아키텍처 문서

### 역할
학생식당에서 시작한 구조를 다른 캠퍼스 시설로 확장한다.

### Code-by-Code Reading

```text
cafeteria status
```

식당 상태 데이터는 메뉴, 혼잡도, 만족도, 소진 여부로 구성된다.

```text
campus facility status
```

이 구조는 카페, 열람실, 스터디룸, 강의실 빈자리, 시설 민원으로 확장될 수 있다.

```text
student feedback
```

학생 반응 데이터는 시설별 만족도나 불편사항 수집에도 활용할 수 있다.

```text
admin dashboard
```

운영자는 시설별 상태와 반복 불편을 한 화면에서 볼 수 있다.

### 이해한 점
CityBrain은 처음부터 학교 전체를 바꾸는 프로젝트가 아니라, 학생식당이라는 좁은 문제에서 시작해 검증 후 확장하는 구조다.

### 발표 포인트
학생식당이라는 제한된 영역에서 시범운영하고, 효과가 확인되면 다른 캠퍼스 시설로 확장할 수 있다.

---

## Final Summary

CityBrain은 단순한 학생 편의 앱이 아니다.

학생에게는 메뉴, 혼잡도, 남은 수량, 만족도 정보를 제공해 더 나은 식사 선택을 돕는다.  
학교와 식당에는 메뉴 만족도, 혼잡 시간대, 소진 불만, 학생 반응 데이터를 제공해 식당 운영을 데이터 기반으로 개선할 수 있게 한다.

핵심은 다음 문장으로 정리된다.

> CityBrain의 학교 측 ROI는 이미지가 아니라 운영 데이터다.

