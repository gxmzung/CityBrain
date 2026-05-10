from __future__ import annotations

from pathlib import Path
from typing import Any

from fastapi import APIRouter, Request
from fastapi.responses import HTMLResponse
from fastapi.templating import Jinja2Templates
from pydantic import BaseModel

from app.db import public_state, get_notices, get_survey_summary
from app.menu_data import get_today_menu, get_weekly_menu

router = APIRouter()
templates = Jinja2Templates(directory=str(Path(__file__).resolve().parent.parent / "templates"))


class JarvisRequest(BaseModel):
    message: str


def _flatten_menu(day: dict[str, Any]) -> str:
    order = ["breakfast", "lunch_korean", "lunch_special", "lunch_snack", "dinner"]
    lines: list[str] = []
    for key in order:
        menu = day.get(key, {})
        title = menu.get("title", key)
        main = menu.get("main", "메뉴 준비중")
        time = menu.get("time", "")
        kcal = menu.get("kcal", "")
        extra = f" / {kcal}" if kcal else ""
        lines.append(f"{title}: {main} ({time}{extra})")
    return "\n".join(lines)


def build_jarvis_answer(message: str) -> dict[str, Any]:
    text = (message or "").strip()
    lowered = text.lower()
    state = public_state()
    notices = get_notices()
    survey = get_survey_summary()
    today_menu = get_today_menu(0)
    weekly = get_weekly_menu()

    if not text:
        answer = "명령을 인식하지 못했습니다. 예: 오늘 학식, 혼잡도, 공지, 잔여 수량처럼 물어보세요."
        intent = "empty"
    elif any(k in text for k in ["학식", "식단", "메뉴", "밥", "점심", "저녁", "조식"]):
        answer = f"오늘 학생식당 기준 식단입니다.\n{_flatten_menu(today_menu)}"
        intent = "menu"
    elif any(k in text for k in ["혼잡", "대기", "줄", "사람", "몇 분"]):
        answer = (
            f"현재 혼잡도는 {state['congestion_level']}입니다. "
            f"예상 대기시간은 약 {state['wait_minutes']}분, 현재 인원은 약 {state.get('current_people', 31)}명입니다."
        )
        intent = "congestion"
    elif any(k in text for k in ["잔여", "남았", "수량", "매진", "품절"]):
        answer = (
            f"현재 메뉴는 {state['menu_name']}이고, 총 {state['total_count']}개 중 "
            f"{state['remaining_count']}개가 남았습니다. 예상 소진 시각은 {state['sellout_eta']}입니다."
        )
        intent = "stock"
    elif any(k in text for k in ["공지", "안내", "알림"]):
        if notices:
            top = notices[:3]
            answer = "최근 공지입니다.\n" + "\n".join([f"- {n['title']}: {n['body']}" for n in top])
        else:
            answer = "현재 활성화된 공지가 없습니다."
        intent = "notice"
    elif any(k in text for k in ["설문", "불만", "이탈", "만족", "데이터"]):
        answer = (
            f"설문 응답은 총 {survey['total_responses']}건입니다. "
            f"식당 이용 포기 경험은 {survey['abandon_count']}건, 정보 제공이 도움 된다는 응답은 {survey['info_help_count']}건입니다. "
            f"가장 많이 원하는 정보는 {survey['top_info']}입니다."
        )
        intent = "survey"
    elif any(k in lowered for k in ["help", "명령", "뭐 할", "사용법"]):
        answer = "가능한 명령은 오늘 학식, 혼잡도, 잔여 수량, 공지, 설문 요약입니다. 마이크 버튼으로 말하거나 직접 입력하세요."
        intent = "help"
    else:
        answer = (
            "CityBrain Jarvis는 현재 학생식당 데모 데이터에 연결되어 있습니다. "
            "질문을 '오늘 학식 알려줘', '지금 혼잡해?', '잔여 수량 몇 개야?', '공지 요약해줘'처럼 바꿔보세요."
        )
        intent = "fallback"

    return {
        "ok": True,
        "intent": intent,
        "user_message": text,
        "answer": answer,
        "state": state,
        "week_label": weekly["week_label"],
    }


@router.get("/jarvis", response_class=HTMLResponse)
def jarvis_page(request: Request):
    return templates.TemplateResponse(
        "jarvis.html",
        {
            "request": request,
            "title": "CityBrain Jarvis",
            "state": public_state(),
            "notices": get_notices(),
        },
    )


@router.post("/api/jarvis/chat")
def jarvis_chat(payload: JarvisRequest):
    return build_jarvis_answer(payload.message)


@router.get("/api/jarvis/demo")
def jarvis_demo():
    return build_jarvis_answer("오늘 학식과 혼잡도 알려줘")
