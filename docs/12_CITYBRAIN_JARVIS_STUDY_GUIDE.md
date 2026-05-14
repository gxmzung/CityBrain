# CityBrain Jarvis 학습 가이드

## 추가된 기능

- `/jarvis`: JARVIS 스타일 학생식당 음성 비서 화면
- `/api/jarvis/chat`: 질문을 받아 학생식당 상태, 식단, 공지, 설문 데이터를 바탕으로 답변하는 API
- 브라우저 음성 인식: `SpeechRecognition` 또는 `webkitSpeechRecognition`
- 브라우저 음성 출력: `speechSynthesis`

## 파일 구조

```txt
backend/app/routes/jarvis.py          # Jarvis 화면 라우트 + 채팅 API
backend/app/templates/jarvis.html     # Jarvis UI 화면
backend/app/static/app.css            # Jarvis 디자인 CSS 추가
backend/app/static/app.js             # 음성 인식, API 호출, TTS 로직 추가
backend/app/main.py                   # jarvis router 등록
```

## 공부 순서

1. `backend/app/main.py`에서 `jarvis.router`가 어떻게 등록되는지 본다.
2. `backend/app/routes/jarvis.py`에서 `/jarvis`와 `/api/jarvis/chat`의 차이를 본다.
3. `build_jarvis_answer()` 함수가 어떤 키워드로 의도를 나누는지 본다.
4. `jarvis.html`에서 버튼, 입력창, 출력 영역의 id를 확인한다.
5. `app.js`에서 `askJarvis()` 함수가 fetch로 API를 호출하는 흐름을 따라간다.
6. `speak()` 함수와 마이크 이벤트를 분리해서 이해한다.

## 현재 한계

이 버전은 외부 AI API를 쓰지 않는다. 즉, 진짜 LLM이 아니라 규칙 기반 데모다. 포트폴리오 학습용으로는 오히려 이게 좋다. 서버, API, DOM, 음성 입출력 구조를 직접 설명하기 쉽기 때문이다.

## 다음 확장

- OpenAI/Gemini API 연결
- 학교 공지 크롤링 또는 관리자 입력 공지 연결
- Android WebView 또는 Compose 화면에 `/jarvis` 연결
- 학생 개인 시간표/공지/학식 통합 질의
