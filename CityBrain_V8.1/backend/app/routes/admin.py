import csv
import io
from pathlib import Path
from fastapi import APIRouter, Form, Request, UploadFile, File
from fastapi.responses import HTMLResponse, RedirectResponse, StreamingResponse
from fastapi.templating import Jinja2Templates
from app.config import COOKIE_NAME
from app.security import create_token, verify_token, verify_password
from app.db import (
    verify_user, list_users, create_user, change_password, toggle_user_active,
    public_state, update_state, add_log, get_logs, get_all_logs, get_survey_summary,
    import_survey_csv, apply_sale, sales_by_menu, sales_by_hour, congestion_distribution, weekly_sales,
    get_notices, add_notice, report_rows, weekly_report_rows, reset_demo, wait_from_congestion, people_from_congestion
)
from app.charts import bar_chart_svg

router = APIRouter(prefix="/admin")
templates = Jinja2Templates(directory=str(Path(__file__).resolve().parent.parent / "templates"))

def current_user_payload(request: Request):
    token = request.cookies.get(COOKIE_NAME)
    return verify_token(token) if token else None

def current_username(request: Request):
    payload = current_user_payload(request)
    return payload.get("username") if payload else None

def current_role(request: Request):
    payload = current_user_payload(request)
    return payload.get("role") if payload else None

def require_auth(request: Request):
    return current_user_payload(request) is not None

def can_edit(request: Request):
    return current_role(request) in ("admin", "operator")

@router.get("/login", response_class=HTMLResponse)
def login_page(request: Request):
    return templates.TemplateResponse("login.html", {"request": request, "title": "관리자 로그인", "error": None})

@router.post("/login")
def login_submit(request: Request, username: str = Form(...), password: str = Form(...)):
    user = verify_user(username)
    if user and verify_password(password, user["password_hash"]):
        response = RedirectResponse(url="/admin/dashboard", status_code=303)
        response.set_cookie(COOKIE_NAME, create_token({"username": username, "role": user["role"]}), httponly=True, samesite="lax")
        add_log(username, "LOGIN", f"{user['role']} 권한 로그인")
        return response
    return templates.TemplateResponse("login.html", {"request": request, "title": "관리자 로그인", "error": "로그인 정보가 올바르지 않습니다."})

@router.get("/logout")
def logout():
    response = RedirectResponse(url="/admin/login", status_code=303)
    response.delete_cookie(COOKIE_NAME)
    return response

@router.get("/dashboard", response_class=HTMLResponse)
def dashboard(request: Request):
    if not require_auth(request):
        return RedirectResponse(url="/admin/login", status_code=303)
    weekly = weekly_sales()
    return templates.TemplateResponse(
        "admin.html",
        {
            "request": request,
            "title": "관리자 대시보드",
            "actor": current_username(request),
            "role": current_role(request),
            "can_edit": can_edit(request),
            "state": public_state(),
            "logs": get_logs(),
            "survey": get_survey_summary(),
            "notices": get_notices(),
            "users": list_users(),
            "weekly_total": sum(v for _, v in weekly),
            "menu_chart": bar_chart_svg(sales_by_menu()[:6], title="메뉴별 판매량"),
            "hour_chart": bar_chart_svg(sales_by_hour()[:8], title="시간대별 판매량"),
            "congestion_chart": bar_chart_svg(congestion_distribution()[:6], title="혼잡도 분포"),
            "weekly_chart": bar_chart_svg(weekly[:7], title="최근 7일 판매량"),
        },
    )

@router.post("/update")
def admin_update(
    request: Request,
    menu_name: str = Form(...),
    total_count: int = Form(...),
    remaining_count: int = Form(...),
    sold_count: int = Form(...),
    congestion_level: str = Form(...),
    wait_minutes: int = Form(...),
    sellout_eta: str = Form(...),
    current_people: int = Form(31),
):
    if not require_auth(request):
        return RedirectResponse(url="/admin/login", status_code=303)
    if not can_edit(request):
        return RedirectResponse(url="/admin/dashboard", status_code=303)
    payload = {
        "menu_name": menu_name,
        "total_count": total_count,
        "remaining_count": remaining_count,
        "sold_count": sold_count,
        "congestion_level": congestion_level,
        "wait_minutes": wait_minutes,
        "sellout_eta": sellout_eta,
        "current_people": current_people,
    }
    update_state(payload)
    add_log(current_username(request), "STATE_UPDATE", f"{menu_name} / 잔여 {remaining_count} / {congestion_level} / {wait_minutes}분")
    return RedirectResponse(url="/admin/dashboard", status_code=303)

@router.post("/quick-sale")
def quick_sale(
    request: Request,
    sold_delta: int = Form(...),
    congestion_level: str = Form(...),
    wait_minutes: int | None = Form(None),
):
    if not require_auth(request):
        return RedirectResponse(url="/admin/login", status_code=303)
    if not can_edit(request):
        return RedirectResponse(url="/admin/dashboard", status_code=303)
    state = public_state()
    wait = wait_minutes if wait_minutes is not None else wait_from_congestion(congestion_level)
    updated = apply_sale(state["menu_name"], sold_delta, congestion_level, wait, source="admin_quick")
    add_log(current_username(request), "QUICK_SALE", f"-{sold_delta}개 반영 / 잔여 {updated['remaining_count']} / {congestion_level}")
    return RedirectResponse(url="/admin/dashboard", status_code=303)

@router.post("/quick-congestion")
def quick_congestion(
    request: Request,
    congestion_level: str = Form(...),
):
    if not require_auth(request):
        return RedirectResponse(url="/admin/login", status_code=303)
    if not can_edit(request):
        return RedirectResponse(url="/admin/dashboard", status_code=303)
    state = public_state()
    state.update({
        "congestion_level": congestion_level,
        "wait_minutes": wait_from_congestion(congestion_level),
        "current_people": people_from_congestion(congestion_level),
    })
    update_state(state)
    add_log(current_username(request), "CONGESTION_UPDATE", f"{congestion_level} / {state['wait_minutes']}분")
    return RedirectResponse(url="/admin/dashboard", status_code=303)

@router.post("/reset-demo")
def reset_demo_route(request: Request):
    if not require_auth(request):
        return RedirectResponse(url="/admin/login", status_code=303)
    if can_edit(request):
        reset_demo()
        add_log(current_username(request), "RESET_DEMO", "시연 기본값 복원")
    return RedirectResponse(url="/admin/dashboard", status_code=303)

@router.post("/survey/upload")
async def survey_upload(request: Request, survey_file: UploadFile = File(...)):
    if not require_auth(request):
        return RedirectResponse(url="/admin/login", status_code=303)
    if not can_edit(request):
        return RedirectResponse(url="/admin/dashboard", status_code=303)
    summary = import_survey_csv(await survey_file.read())
    add_log(current_username(request), "SURVEY_UPLOAD", f"응답 {summary['total_responses']}건 집계")
    return RedirectResponse(url="/admin/dashboard", status_code=303)

@router.post("/notices/create")
def notice_create(request: Request, title: str = Form(...), body: str = Form(...)):
    if not require_auth(request):
        return RedirectResponse(url="/admin/login", status_code=303)
    if can_edit(request):
        add_notice(title, body)
        add_log(current_username(request), "NOTICE_CREATE", title)
    return RedirectResponse(url="/admin/dashboard", status_code=303)

@router.post("/users/create")
def users_create(request: Request, username: str = Form(...), password: str = Form(...), role: str = Form(...)):
    if not require_auth(request):
        return RedirectResponse(url="/admin/login", status_code=303)
    if current_role(request) == "admin":
        create_user(username, password, role)
        add_log(current_username(request), "USER_CREATE", f"{username}/{role}")
    return RedirectResponse(url="/admin/dashboard", status_code=303)

@router.post("/users/toggle/{user_id}")
def users_toggle(request: Request, user_id: int):
    if not require_auth(request):
        return RedirectResponse(url="/admin/login", status_code=303)
    if current_role(request) == "admin":
        toggle_user_active(user_id)
        add_log(current_username(request), "USER_TOGGLE", f"user_id={user_id}")
    return RedirectResponse(url="/admin/dashboard", status_code=303)

@router.post("/password/change")
def password_change(request: Request, current_password: str = Form(...), new_password: str = Form(...), confirm_password: str = Form(...)):
    if not require_auth(request):
        return RedirectResponse(url="/admin/login", status_code=303)
    if new_password == confirm_password:
        ok, msg = change_password(current_username(request), current_password, new_password)
        add_log(current_username(request), "PASSWORD_CHANGE", msg)
    return RedirectResponse(url="/admin/dashboard", status_code=303)

def csv_response(filename: str, rows):
    stream = io.StringIO()
    writer = csv.writer(stream)
    writer.writerows(rows)
    stream.seek(0)
    return StreamingResponse(
        iter([stream.getvalue()]),
        media_type="text/csv; charset=utf-8-sig",
        headers={"Content-Disposition": f"attachment; filename={filename}"},
    )

@router.get("/export/logs")
def export_logs(request: Request):
    if not require_auth(request):
        return RedirectResponse(url="/admin/login", status_code=303)
    rows = [["시간", "사용자", "행동", "상세"]]
    rows.extend([[r["created_at"], r["actor"], r["action"], r["detail"]] for r in get_all_logs()])
    return csv_response("citybrain_logs.csv", rows)

@router.get("/export/report")
def export_report(request: Request):
    if not require_auth(request):
        return RedirectResponse(url="/admin/login", status_code=303)
    return csv_response("citybrain_operation_report.csv", report_rows())

@router.get("/export/weekly-report")
def export_weekly_report(request: Request):
    if not require_auth(request):
        return RedirectResponse(url="/admin/login", status_code=303)
    return csv_response("citybrain_weekly_report.csv", weekly_report_rows())
