# CityBrain Cafeteria Demo

배재대학교 학생식당 CityBrain 시연용 프로젝트입니다. 학생 화면과 관리자 화면이 연결됩니다.

## 실행(Windows PowerShell)
```powershell
cd "프로젝트_폴더_경로"
py -m venv .venv
.\.venv\Scripts\python.exe -m pip install -r requirements.txt
.\.venv\Scripts\python.exe -m uvicorn app.main:app --reload
```

## 접속
- 홈: http://127.0.0.1:8000/
- 학생 화면: http://127.0.0.1:8000/student
- 관리자 화면: http://127.0.0.1:8000/admin
- 운영 로그: http://127.0.0.1:8000/logs

## 시연 흐름
1. 학생 화면에서 63개 남음 / 보통 / 7분 확인
2. 관리자 화면에서 판매 5건 반영
3. 관리자 화면에서 혼잡도를 혼잡으로 변경
4. 학생 화면에서 58개 남음 / 혼잡 / 15분 확인
