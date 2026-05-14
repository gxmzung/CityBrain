# CityBrain Runbook

## Purpose

이 문서는 CityBrain 시연과 심사 준비를 위한 실행 절차 문서이다.

목표는 심사 당일 또는 발표 전날에 백엔드 서버, 학생 웹, 관리자 웹, Android 앱 연결 방식을 빠르게 확인할 수 있게 만드는 것이다.

CityBrain은 단순한 화면 목업이 아니라, 학생 화면, 관리자 화면, FastAPI 백엔드, SQLite 저장 구조, Android 연동 구조를 가진 스마트캠퍼스 학생식당 MVP이다.

---

## 1. Project Location

CityBrain 프로젝트 루트로 이동한다.

```bash
cd ~/Documents/001.개발/CityBrain/CityBrain_V8.2
```

---

## 2. Backend Run Command

### macOS / zsh

```bash
cd ~/Documents/001.개발/CityBrain/CityBrain_V8.2

python3 -m venv .venv
source .venv/bin/activate

pip install -r requirements.txt

uvicorn backend.app.main:app --host 0.0.0.0 --port 8000 --reload
```

### If `requirements.txt` is missing

```bash
pip install fastapi uvicorn sqlalchemy python-multipart jinja2
```

### Server Check

브라우저에서 아래 주소에 접속한다.

```text
http://127.0.0.1:8000/
```

정상적으로 서버 응답 또는 기본 화면이 뜨면 백엔드 실행 성공이다.

---

## 3. Student Web Access

학생용 웹 화면 접속 주소:

```text
http://127.0.0.1:8000/
```

또는 프로젝트 라우팅 구조에 따라:

```text
http://127.0.0.1:8000/student
```

확인할 것:

```text
1. 오늘 메뉴가 보이는가?
2. 혼잡도 또는 식당 상태가 보이는가?
3. 만족도/반응 입력 화면으로 이동 가능한가?
4. 화면이 깨지지 않는가?
```

---

## 4. Admin Web Access

관리자 웹 접속 주소:

```text
http://127.0.0.1:8000/admin/login
```

또는 프로젝트 라우팅 구조에 따라:

```text
http://127.0.0.1:8000/admin
```

확인할 것:

```text
1. 관리자 로그인 화면이 열리는가?
2. 메뉴 등록/수정 화면에 접근 가능한가?
3. 혼잡도 또는 운영 상태 입력이 가능한가?
4. 학생 반응 또는 만족도 요약을 볼 수 있는가?
```

---

## 5. Android App Connection

Android 앱은 로컬 PC에서 실행 중인 FastAPI 서버에 접속해야 한다.

실제 Android 기기에서 테스트할 경우 `127.0.0.1`을 사용하면 안 된다.  
휴대폰 기준 `127.0.0.1`은 MacBook이 아니라 휴대폰 자기 자신을 의미하기 때문이다.

따라서 MacBook의 Wi-Fi IP 주소를 확인해야 한다.

### MacBook IP Check

```bash
ipconfig getifaddr en0
```

예시 결과:

```text
192.168.75.108
```

Android 앱의 BASE_URL은 아래처럼 설정한다.

```text
http://192.168.75.108:8000/
```

---

## 6. Android BASE_URL Setting

Android 설정 파일 예시:

```text
android/gradle.properties
```

또는 프로젝트 설정에 따라:

```text
gradle.properties
```

설정 예시:

```properties
CITYBRAIN_BASE_URL=http://192.168.75.108:8000/
```

주의:

```text
1. MacBook과 Android 기기가 같은 Wi-Fi에 있어야 한다.
2. FastAPI 서버는 --host 0.0.0.0 으로 실행해야 한다.
3. Android 앱에서는 127.0.0.1 대신 MacBook의 실제 IP를 사용해야 한다.
4. BASE_URL 끝에는 / 를 붙인다.
```

---

## 7. Android Build / Run Check

Android Studio에서 확인할 것:

```text
1. android 폴더를 Android Studio로 연다.
2. Gradle Sync가 완료되는지 확인한다.
3. CITYBRAIN_BASE_URL 값이 올바른지 확인한다.
4. 실제 기기 또는 에뮬레이터에서 앱을 실행한다.
5. 메뉴/혼잡도 데이터가 서버에서 받아지는지 확인한다.
```

터미널에서 Android Studio 열기 예시:

```bash
open -a "Android Studio" android
```

---

## 8. Demo Flow

심사 시연 순서:

```text
1. FastAPI 백엔드 실행
2. 학생 웹 접속
3. 오늘 메뉴 확인
4. 혼잡도 또는 남은 수량 확인
5. 만족도/반응 입력
6. 관리자 웹 접속
7. 메뉴 또는 상태 입력
8. 학생 화면에서 반영 확인
9. 학교 측 ROI 설명
```

---

## 9. Judge Explanation

시연 중 설명 문장:

```text
CityBrain은 단순히 메뉴를 보여주는 앱이 아니라,
학생 화면, 관리자 화면, FastAPI 백엔드, SQLite 저장 구조를 연결한 스마트캠퍼스 학생식당 MVP입니다.

학생은 메뉴와 혼잡도, 남은 수량을 확인하고,
관리자는 메뉴와 식당 상태를 입력합니다.

이 데이터가 누적되면 학교와 식당은 식수 예측, 메뉴 수량 조절, 혼잡 분산, 민원 감소에 활용할 수 있습니다.
```

---

## 10. Troubleshooting

### Backend server does not start

```bash
source .venv/bin/activate
pip install -r requirements.txt
uvicorn backend.app.main:app --host 0.0.0.0 --port 8000 --reload
```

### ModuleNotFoundError

```bash
pip install fastapi uvicorn sqlalchemy jinja2 python-multipart
```

### Android app cannot connect to server

확인할 것:

```text
1. MacBook과 Android 기기가 같은 Wi-Fi인지 확인
2. FastAPI 서버가 --host 0.0.0.0 으로 실행 중인지 확인
3. Android BASE_URL이 127.0.0.1이 아닌 MacBook IP인지 확인
4. BASE_URL 끝에 / 가 있는지 확인
```

### Admin page not found

확인할 주소:

```text
http://127.0.0.1:8000/admin/login
http://127.0.0.1:8000/admin
```

### Student page not found

확인할 주소:

```text
http://127.0.0.1:8000/
http://127.0.0.1:8000/student
```

---

## 11. Final Checklist

```text
[ ] 백엔드 실행 가능
[ ] 학생 웹 접속 가능
[ ] 관리자 웹 접속 가능
[ ] Android BASE_URL 확인
[ ] MacBook IP 확인
[ ] 같은 Wi-Fi 연결 확인
[ ] 메뉴 화면 캡처
[ ] 관리자 화면 캡처
[ ] 만족도/반응 화면 캡처
[ ] 시연 순서 1회 리허설
```

---

## Final Note

시연 전날에는 새 기능을 추가하지 않는다.

목표는 기능을 더 만드는 것이 아니라, 이미 구현된 흐름을 안정적으로 실행하고 설명하는 것이다.
