from pathlib import Path
from fastapi import APIRouter, Request
from fastapi.responses import HTMLResponse
from fastapi.templating import Jinja2Templates
from app.db import get_state, get_survey_summary, get_notices

router = APIRouter()
templates = Jinja2Templates(directory=str(Path(__file__).resolve().parent.parent / "templates"))

@router.get("/", response_class=HTMLResponse)
def student_home(request: Request):
    return templates.TemplateResponse(
        "student.html",
        {"request": request, "title": "학생 화면", "state": get_state(), "survey": get_survey_summary(), "notices": get_notices()}
    )