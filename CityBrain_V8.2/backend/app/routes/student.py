from pathlib import Path
from fastapi import APIRouter, Request
from fastapi.responses import HTMLResponse
from fastapi.templating import Jinja2Templates
from app.db import public_state, get_survey_summary, get_notices
from app.menu_data import get_weekly_menu

router = APIRouter()
templates = Jinja2Templates(directory=str(Path(__file__).resolve().parent.parent / "templates"))

@router.get("/", response_class=HTMLResponse)
def student_home(request: Request):
    return templates.TemplateResponse(
        "student.html",
        {
            "request": request,
            "title": "CityBrain 학생식당",
            "state": public_state(),
            "survey": get_survey_summary(),
            "notices": get_notices(),
            "weekly_menu": get_weekly_menu(),
        },
    )

@router.get("/api/state")
def state_api():
    return public_state()

@router.get("/api/student/state")
def student_state_api():
    return public_state()

@router.get("/api/student/survey")
def student_survey_api():
    return get_survey_summary()

@router.get("/api/student/notices")
def student_notices_api():
    return get_notices()


@router.get("/api/student/weekly-menu")
def student_weekly_menu_api():
    return get_weekly_menu()

@router.get("/privacy", response_class=HTMLResponse)
def privacy_page(request: Request):
    return templates.TemplateResponse("privacy.html", {"request": request, "title": "개인정보 처리방침"})

@router.get("/status", response_class=HTMLResponse)
def status_page(request: Request):
    return templates.TemplateResponse("status.html", {"request": request, "title": "운영 상태"})
