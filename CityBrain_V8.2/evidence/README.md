# CityBrain Evidence Package

## Purpose

이 폴더는 CityBrain 심사와 시연 때 보여줄 증거 자료를 정리하기 위한 공간이다.

CityBrain은 단순한 아이디어 설명이 아니라, 학생 화면, 관리자 화면, FastAPI 백엔드, SQLite 저장 구조, Android 연동 흐름을 가진 스마트캠퍼스 학생식당 MVP이다.

심사위원에게 보여줄 핵심은 다음과 같다.

```text
1. 실제 구현 화면이 있다.
2. 실행 방법이 정리되어 있다.
3. 코드 구조를 설명할 수 있다.
4. 학생 가치와 학교 측 ROI가 정리되어 있다.
5. 현재 한계점과 확장 방향을 알고 있다.
```

---

## Evidence Structure

```text
evidence/
├── screenshots/
│   └── 실행 화면 캡처 정리
├── demo-flow/
│   └── 90초/3분 시연 흐름 정리
├── code-structure/
│   └── 코드 구조와 데이터 흐름 정리
├── runbook/
│   └── 실행법과 시연 준비 절차 정리
└── interview-notes/
    └── 직원/학생 인터뷰 기록 정리
```

---

## Main Documents

아래 문서는 CityBrain 심사 대비 핵심 문서이다.

```text
CITYBRAIN_DEMO_NOTES.md
- 90초 시연 흐름
- 학교 측 ROI
- 현재 한계점
- 심사위원 Q&A

CITYBRAIN_RUNBOOK.md
- 백엔드 실행 명령어
- 학생 웹 접속 주소
- 관리자 웹 접속 주소
- Android 앱 연결 방식

CITYBRAIN_CODE_FLOW.md
- 전체 코드 흐름
- 학생/관리자/백엔드/DB/Android 연결 구조

code-reading-log.md
- 핵심 코드 흐름을 코드별로 공부한 기록
```

---

## Judge Message

CityBrain은 단순한 학생 편의 앱이 아니라,  
학생의 식사 선택과 식당 운영 개선을 연결하는 스마트캠퍼스 서비스이다.

학생은 메뉴, 혼잡도, 남은 수량, 만족도 정보를 확인해 더 나은 식사 선택을 할 수 있고,  
학교와 식당은 메뉴 만족도, 혼잡 시간대, 소진 불만, 학생 반응 데이터를 기반으로 운영을 개선할 수 있다.

핵심 문장:

> CityBrain의 학교 측 ROI는 이미지가 아니라 운영 데이터다.
