import csv, io
from pathlib import Path
from fastapi import APIRouter, Form, Request, UploadFile, File
from fastapi.responses import HTMLResponse, RedirectResponse, StreamingResponse
from fastapi.templating import Jinja2Templates
from app.config import COOKIE_NAME
from app.security import create_token, verify_token, verify_password
from app.db import (
    verify_user, list_users, create_user, change_password, toggle_user_active,
    get_state, update_state, add_log, get_logs, get_all_logs, get_survey_summary,
    import_survey_csv, add_sale_history, sales_by_menu, sales_by_hour, congestion_distribution, weekly_sales,
    get_notices, add_notice, report_rows, weekly_report_rows
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
            "state": get_state(),
            "logs": get_logs(),
            "survey": get_survey_summary(),
            "notices": get_notices(),
            "users": list_users(),
            "weekly_total": sum(v for _, v in weekly),
            "menu_chart": bar_chart_svg(sales_by_menu()[:6], title="메뉴별 판매량"),
            "hour_chart": bar_chart_svg(sales_by_hour()[:8], title="시간대별 판매량"),
            "congestion_chart": bar_chart_svg(congestion_distribution()[:6], title="혼잡도 분포"),
            "weekly_chart": bar_chart_svg(weekly[:7], title="최근 7일 판매량"),
        }
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
):
    if not require_auth(request): return RedirectResponse(url="/admin/login", status_code=303)
    if not can_edit(request): return RedirectResponse(url="/admin/dashboard", status_code=303)
    actor = current_username(request)
    prev = get_state()
    delta = max(0, sold_count - prev["sold_count"])
    payload = {"menu_name": menu_name, "total_count": total_count, "remaining_count": remaining_count, "sold_count": sold_count, "congestion_level": congestion_level, "wait_minutes": wait_minutes, "sellout_eta": sellout_eta}
    update_state(payload)
    if delta > 0:
        add_sale_history(menu_name, delta, remaining_count, congestion_level, source="manual")
    add_log(actor, "UPDATE_STATE", f"{menu_name}, 총 {total_count}, 잔여 {remaining_count}, 판매 {sold_count}, 혼잡 {congestion_level}, 대기 {wait_minutes}분")
    return RedirectResponse(url="/admin/dashboard", status_code=303)

@router.post("/survey/upload")
async def upload_survey(request: Request, survey_file: UploadFile = File(...)):
    if not require_auth(request): return RedirectResponse(url="/admin/login", status_code=303)
    if not can_edit(request): return RedirectResponse(url="/admin/dashboard", status_code=303)
    actor = current_username(request)
    content = await survey_file.read()
    summary = import_survey_csv(content)
    add_log(actor, "IMPORT_SURVEY", f"응답자 {summary['total_responses']}명, 포기 {summary['abandon_count']}명, 도움 {summary['info_help_count']}명")
    return RedirectResponse(url="/admin/dashboard", status_code=303)

@router.post("/notices/create")
def create_notice_action(request: Request, title: str = Form(...), body: str = Form(...)):
    if not require_auth(request): return RedirectResponse(url="/admin/login", status_code=303)
    if not can_edit(request): return RedirectResponse(url="/admin/dashboard", status_code=303)
    actor = current_username(request)
    add_notice(title, body)
    add_log(actor, "CREATE_NOTICE", title)
    return RedirectResponse(url="/admin/dashboard", status_code=303)

@router.post("/users/create")
def create_user_action(request: Request, username: str = Form(...), password: str = Form(...), role: str = Form(...)):
    if not require_auth(request): return RedirectResponse(url="/admin/login", status_code=303)
    if current_role(request) != "admin": return RedirectResponse(url="/admin/dashboard", status_code=303)
    actor = current_username(request)
    create_user(username, password, role)
    add_log(actor, "CREATE_USER", f"{username} / {role}")
    return RedirectResponse(url="/admin/dashboard", status_code=303)

@router.post("/users/toggle/{user_id}")
def toggle_user_action(request: Request, user_id: int):
    if not require_auth(request): return RedirectResponse(url="/admin/login", status_code=303)
    if current_role(request) != "admin": return RedirectResponse(url="/admin/dashboard", status_code=303)
    actor = current_username(request)
    toggle_user_active(user_id)
    add_log(actor, "TOGGLE_USER", f"user_id={user_id}")
    return RedirectResponse(url="/admin/dashboard", status_code=303)

@router.post("/password/change")
def change_password_action(
    request: Request,
    current_password: str = Form(...),
    new_password: str = Form(...),
    confirm_password: str = Form(...)
):
    if not require_auth(request): return RedirectResponse(url="/admin/login", status_code=303)
    if new_password != confirm_password:
        add_log(current_username(request), "PASSWORD_CHANGE_FAIL", "새 비밀번호 확인 불일치")
        return RedirectResponse(url="/admin/dashboard", status_code=303)
    ok, msg = change_password(current_username(request), current_password, new_password)
    add_log(current_username(request), "PASSWORD_CHANGE", msg)
    return RedirectResponse(url="/admin/dashboard", status_code=303)

@router.get("/export/logs")
def export_logs(request: Request):
    if not require_auth(request): return RedirectResponse(url="/admin/login", status_code=303)
    rows = get_all_logs()
    stream = io.StringIO()
    writer = csv.DictWriter(stream, fieldnames=["id", "created_at", "actor", "action", "detail"])
    writer.writeheader()
    writer.writerows(rows)
    mem = io.BytesIO(stream.getvalue().encode("utf-8-sig"))
    headers = {"Content-Disposition": "attachment; filename=citybrain_audit_logs.csv"}
    return StreamingResponse(mem, media_type="text/csv", headers=headers)

@router.get("/export/report")
def export_report(request: Request):
    if not require_auth(request): return RedirectResponse(url="/admin/login", status_code=303)
    rows = report_rows()
    stream = io.StringIO()
    writer = csv.DictWriter(stream, fieldnames=["item", "value"])
    writer.writeheader()
    writer.writerows(rows)
    mem = io.BytesIO(stream.getvalue().encode("utf-8-sig"))
    headers = {"Content-Disposition": "attachment; filename=citybrain_operation_report.csv"}
    return StreamingResponse(mem, media_type="text/csv", headers=headers)

@router.get("/export/weekly-report")
def export_weekly_report(request: Request):
    if not require_auth(request): return RedirectResponse(url="/admin/login", status_code=303)
    rows = weekly_report_rows()
    stream = io.StringIO()
    writer = csv.DictWriter(stream, fieldnames=["day", "sold_total"])
    writer.writeheader()
    writer.writerows(rows)
    mem = io.BytesIO(stream.getvalue().encode("utf-8-sig"))
    headers = {"Content-Disposition": "attachment; filename=citybrain_weekly_report.csv"}
    return StreamingResponse(mem, media_type="text/csv", headers=headers)