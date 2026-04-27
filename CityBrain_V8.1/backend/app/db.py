import csv
import sqlite3
from collections import Counter
from datetime import datetime, timedelta
from pathlib import Path
from app.config import DATA_DIR, ADMIN_USERNAME, ADMIN_PASSWORD
from app.security import hash_password, verify_password

DB_PATH = DATA_DIR / "citybrain_final.db"
SURVEY_PATH = DATA_DIR / "survey_latest.csv"

def now_text() -> str:
    return datetime.now().strftime("%Y-%m-%d %H:%M:%S")

def connect():
    DATA_DIR.mkdir(parents=True, exist_ok=True)
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    return conn

def wait_from_congestion(level: str) -> int:
    return {"원활": 3, "보통": 7, "혼잡": 15}.get(level, 7)

def people_from_congestion(level: str) -> int:
    return {"원활": 18, "보통": 31, "혼잡": 54}.get(level, 31)

def init_db():
    DATA_DIR.mkdir(parents=True, exist_ok=True)
    conn = connect()
    cur = conn.cursor()

    cur.execute("""
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT UNIQUE NOT NULL,
            password_hash TEXT NOT NULL,
            role TEXT NOT NULL,
            is_active INTEGER NOT NULL DEFAULT 1
        )
    """)
    cur.execute("""
        CREATE TABLE IF NOT EXISTS state (
            id INTEGER PRIMARY KEY CHECK (id = 1),
            menu_name TEXT NOT NULL,
            total_count INTEGER NOT NULL,
            remaining_count INTEGER NOT NULL,
            sold_count INTEGER NOT NULL,
            congestion_level TEXT NOT NULL,
            wait_minutes INTEGER NOT NULL,
            sellout_eta TEXT NOT NULL,
            current_people INTEGER NOT NULL DEFAULT 31,
            updated_at TEXT NOT NULL
        )
    """)
    cur.execute("""
        CREATE TABLE IF NOT EXISTS audit_logs (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            created_at TEXT NOT NULL,
            actor TEXT NOT NULL,
            action TEXT NOT NULL,
            detail TEXT NOT NULL
        )
    """)
    cur.execute("""
        CREATE TABLE IF NOT EXISTS sales_history (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            created_at TEXT NOT NULL,
            menu_name TEXT NOT NULL,
            sold_delta INTEGER NOT NULL,
            remaining_count INTEGER NOT NULL,
            congestion_level TEXT NOT NULL,
            wait_minutes INTEGER NOT NULL,
            source TEXT NOT NULL DEFAULT 'manual'
        )
    """)
    cur.execute("""
        CREATE TABLE IF NOT EXISTS notices (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            created_at TEXT NOT NULL,
            title TEXT NOT NULL,
            body TEXT NOT NULL,
            is_active INTEGER NOT NULL DEFAULT 1
        )
    """)
    cur.execute("""
        CREATE TABLE IF NOT EXISTS survey_summary (
            id INTEGER PRIMARY KEY CHECK (id = 1),
            total_responses INTEGER NOT NULL,
            abandon_count INTEGER NOT NULL,
            info_help_count INTEGER NOT NULL,
            top_info TEXT NOT NULL,
            top_reason_1 TEXT NOT NULL,
            top_reason_2 TEXT NOT NULL,
            top_reason_3 TEXT NOT NULL,
            top_reason_4 TEXT NOT NULL
        )
    """)

    # old DB migration safety
    state_cols = {r["name"] for r in cur.execute("PRAGMA table_info(state)").fetchall()}
    if "current_people" not in state_cols:
        cur.execute("ALTER TABLE state ADD COLUMN current_people INTEGER NOT NULL DEFAULT 31")
    if "updated_at" not in state_cols:
        cur.execute("ALTER TABLE state ADD COLUMN updated_at TEXT NOT NULL DEFAULT '2026-04-27 12:00:00'")
    sales_cols = {r["name"] for r in cur.execute("PRAGMA table_info(sales_history)").fetchall()}
    if "wait_minutes" not in sales_cols:
        cur.execute("ALTER TABLE sales_history ADD COLUMN wait_minutes INTEGER NOT NULL DEFAULT 7")

    cur.execute("SELECT COUNT(*) c FROM users")
    if cur.fetchone()["c"] == 0:
        cur.execute("INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)", (ADMIN_USERNAME, hash_password(ADMIN_PASSWORD), "admin"))
        cur.execute("INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)", ("operator", hash_password("operator123"), "operator"))
        cur.execute("INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)", ("viewer", hash_password("viewer123"), "viewer"))

    cur.execute("SELECT COUNT(*) c FROM state")
    if cur.fetchone()["c"] == 0:
        cur.execute("""
            INSERT INTO state
            (id, menu_name, total_count, remaining_count, sold_count, congestion_level, wait_minutes, sellout_eta, current_people, updated_at)
            VALUES (1, '서가앤쿡 목살필라프', 100, 63, 37, '보통', 7, '12:45', 31, ?)
        """, (now_text(),))

    cur.execute("SELECT COUNT(*) c FROM survey_summary")
    if cur.fetchone()["c"] == 0:
        cur.execute("""
            INSERT INTO survey_summary
            (id, total_responses, abandon_count, info_help_count, top_info, top_reason_1, top_reason_2, top_reason_3, top_reason_4)
            VALUES (1, 96, 76, 85, '현재 혼잡도', '외부 식당이 더 나아서', '가격 대비 만족도가 낮아서', '메뉴가 다양하지 않아서', '대기시간이 길어서')
        """)

    cur.execute("SELECT COUNT(*) c FROM notices")
    if cur.fetchone()["c"] == 0:
        cur.execute(
            "INSERT INTO notices (created_at, title, body, is_active) VALUES (?, ?, ?, 1)",
            (now_text(), "시범운영 안내", "오늘의 일품식 잔여 수량·혼잡도·예상 대기시간을 실시간으로 제공합니다.")
        )
        cur.execute(
            "INSERT INTO notices (created_at, title, body, is_active) VALUES (?, ?, ?, 1)",
            (now_text(), "혼잡 시간대 참고", "12:10~12:35 사이에는 대기시간이 길어질 수 있습니다.")
        )

    conn.commit()
    conn.close()
    _ensure_v3_tables()
    _ensure_v4_tables()

def verify_user(username: str):
    conn = connect()
    row = conn.execute("SELECT * FROM users WHERE username = ? AND is_active = 1", (username,)).fetchone()
    conn.close()
    return dict(row) if row else None

def list_users():
    conn = connect()
    rows = conn.execute("SELECT id, username, role, is_active FROM users ORDER BY id").fetchall()
    conn.close()
    return [dict(r) for r in rows]

def create_user(username: str, password: str, role: str):
    conn = connect()
    conn.execute("INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)", (username, hash_password(password), role))
    conn.commit()
    conn.close()

def toggle_user_active(user_id: int):
    conn = connect()
    row = conn.execute("SELECT is_active FROM users WHERE id = ?", (user_id,)).fetchone()
    if row:
        conn.execute("UPDATE users SET is_active = ? WHERE id = ?", (0 if row["is_active"] else 1, user_id))
        conn.commit()
    conn.close()

def change_password(username: str, current_password: str, new_password: str):
    conn = connect()
    row = conn.execute("SELECT * FROM users WHERE username = ?", (username,)).fetchone()
    if not row:
        conn.close()
        return False, "사용자를 찾을 수 없습니다."
    row = dict(row)
    if not verify_password(current_password, row["password_hash"]):
        conn.close()
        return False, "현재 비밀번호가 올바르지 않습니다."
    conn.execute("UPDATE users SET password_hash = ? WHERE username = ?", (hash_password(new_password), username))
    conn.commit()
    conn.close()
    return True, "비밀번호가 변경되었습니다."

def get_state():
    conn = connect()
    row = conn.execute("SELECT * FROM state WHERE id = 1").fetchone()
    conn.close()
    return dict(row)

def public_state():
    state = get_state()
    state["sold_ratio"] = round((state["sold_count"] / max(state["total_count"], 1)) * 100, 1)
    state["remaining_ratio"] = round((state["remaining_count"] / max(state["total_count"], 1)) * 100, 1)
    state["judgement"] = "지금 이용 가능" if state["congestion_level"] != "혼잡" and state["remaining_count"] > 10 else "시간 조정 권장"
    return state

def update_state(payload: dict):
    level = payload.get("congestion_level", "보통")
    wait = int(payload.get("wait_minutes") or wait_from_congestion(level))
    people = int(payload.get("current_people") or people_from_congestion(level))
    total = max(0, int(payload["total_count"]))
    remaining = max(0, min(int(payload["remaining_count"]), total))
    sold = max(0, int(payload["sold_count"]))
    if sold + remaining != total:
        # 관리자 수동 입력 실수를 완화한다. 남은 수량 기준으로 판매량 보정.
        sold = max(0, total - remaining)
    conn = connect()
    conn.execute(
        """
        UPDATE state
        SET menu_name=?, total_count=?, remaining_count=?, sold_count=?,
            congestion_level=?, wait_minutes=?, sellout_eta=?, current_people=?, updated_at=?
        WHERE id=1
        """,
        (
            payload["menu_name"], total, remaining, sold,
            level, wait, payload["sellout_eta"], people, now_text()
        )
    )
    conn.commit()
    conn.close()

def add_log(actor: str, action: str, detail: str):
    conn = connect()
    conn.execute("INSERT INTO audit_logs (created_at, actor, action, detail) VALUES (?, ?, ?, ?)",
                 (now_text(), actor, action, detail))
    conn.commit()
    conn.close()

def get_logs(limit: int = 100):
    conn = connect()
    rows = conn.execute("SELECT * FROM audit_logs ORDER BY id DESC LIMIT ?", (limit,)).fetchall()
    conn.close()
    return [dict(r) for r in rows]

def get_all_logs():
    conn = connect()
    rows = conn.execute("SELECT * FROM audit_logs ORDER BY id DESC").fetchall()
    conn.close()
    return [dict(r) for r in rows]

def apply_sale(menu_name: str, sold_delta: int, congestion_level: str | None = None, wait_minutes: int | None = None, source: str = "manual"):
    state = get_state()
    sold_delta = max(0, int(sold_delta))
    level = congestion_level or state["congestion_level"]
    wait = int(wait_minutes) if wait_minutes is not None else wait_from_congestion(level)
    new_remaining = max(0, state["remaining_count"] - sold_delta)
    new_sold = max(0, state["total_count"] - new_remaining)
    new_state = {
        **state,
        "menu_name": menu_name or state["menu_name"],
        "remaining_count": new_remaining,
        "sold_count": new_sold,
        "congestion_level": level,
        "wait_minutes": wait,
        "current_people": people_from_congestion(level),
        "sellout_eta": state["sellout_eta"],
    }
    update_state(new_state)
    add_sale_history(new_state["menu_name"], sold_delta, new_remaining, level, wait, source)
    return public_state()

def reset_demo():
    payload = {
        "menu_name": "서가앤쿡 목살필라프",
        "total_count": 100,
        "remaining_count": 63,
        "sold_count": 37,
        "congestion_level": "보통",
        "wait_minutes": 7,
        "sellout_eta": "12:45",
        "current_people": 31
    }
    update_state(payload)
    add_log("system", "DEMO_RESET", "시연 기본값으로 초기화")
    return public_state()

def add_sale_history(menu_name: str, sold_delta: int, remaining_count: int, congestion_level: str, wait_minutes: int, source: str = "manual"):
    conn = connect()
    conn.execute(
        "INSERT INTO sales_history (created_at, menu_name, sold_delta, remaining_count, congestion_level, wait_minutes, source) VALUES (?, ?, ?, ?, ?, ?, ?)",
        (now_text(), menu_name, sold_delta, remaining_count, congestion_level, wait_minutes, source)
    )
    conn.commit()
    conn.close()

def sales_by_menu():
    conn = connect()
    rows = conn.execute("SELECT menu_name, SUM(sold_delta) as total FROM sales_history GROUP BY menu_name ORDER BY total DESC").fetchall()
    conn.close()
    return [(r["menu_name"], r["total"] or 0) for r in rows] or [("돈까스덮밥", 37), ("에비동", 22), ("헬스팩", 14)]

def sales_by_hour():
    conn = connect()
    rows = conn.execute("""
        SELECT substr(created_at, 12, 2) as hh, SUM(sold_delta) as total
        FROM sales_history
        GROUP BY hh
        ORDER BY hh
    """).fetchall()
    conn.close()
    result = [(f'{r["hh"]}시', r["total"] or 0) for r in rows if r["hh"]]
    return result or [("11시", 9), ("12시", 20), ("13시", 8)]

def congestion_distribution():
    conn = connect()
    rows = conn.execute("""
        SELECT congestion_level, COUNT(*) as total
        FROM sales_history
        GROUP BY congestion_level
        ORDER BY total DESC
    """).fetchall()
    conn.close()
    result = [(r["congestion_level"], r["total"] or 0) for r in rows]
    return result or [("보통", 3), ("혼잡", 2), ("원활", 1)]

def weekly_sales():
    conn = connect()
    rows = conn.execute("""
        SELECT substr(created_at, 1, 10) as dd, SUM(sold_delta) as total
        FROM sales_history
        GROUP BY dd
        ORDER BY dd DESC
        LIMIT 7
    """).fetchall()
    conn.close()
    result = [(r["dd"], r["total"] or 0) for r in rows]
    return list(reversed(result)) or [("오늘", 37)]

def get_notices():
    conn = connect()
    rows = conn.execute("SELECT title, body, created_at FROM notices WHERE is_active = 1 ORDER BY id DESC").fetchall()
    conn.close()
    return [dict(r) for r in rows]

def add_notice(title: str, body: str):
    conn = connect()
    conn.execute("INSERT INTO notices (created_at, title, body, is_active) VALUES (?, ?, ?, 1)",
                 (now_text(), title, body))
    conn.commit()
    conn.close()

def get_survey_summary():
    conn = connect()
    row = conn.execute("SELECT * FROM survey_summary WHERE id = 1").fetchone()
    conn.close()
    return dict(row)

def compute_survey_summary_from_csv(path: Path):
    with open(path, "r", encoding="utf-8-sig", newline="") as f:
        reader = csv.DictReader(f)
        rows = list(reader)
    total = len(rows)
    abandon_count = 0
    info_help_count = 0
    info_counter = Counter()
    reason_counter = Counter()
    abandon_col = next((c for c in rows[0].keys() if "식사를 포기한 경험" in c), None) if rows else None
    info_col = next((c for c in rows[0].keys() if "가장 알고 싶은 정보" in c), None) if rows else None
    help_col = next((c for c in rows[0].keys() if "이용 판단에 도움이" in c), None) if rows else None
    reason_col = next((c for c in rows[0].keys() if "가장 큰 이유" in c), None) if rows else None
    for row in rows:
        if abandon_col and (row.get(abandon_col) or "").strip() in ("가끔 있다", "자주 있다"):
            abandon_count += 1
        if help_col and (row.get(help_col) or "").strip() in ("매우 그렇다", "그렇다"):
            info_help_count += 1
        if info_col:
            val = (row.get(info_col) or "").strip()
            if val:
                info_counter[val] += 1
        if reason_col:
            val = (row.get(reason_col) or "").strip()
            if val:
                for p in [x.strip() for x in val.replace(",", ";").split(";") if x.strip()]:
                    reason_counter[p] += 1
    top_info = info_counter.most_common(1)[0][0] if info_counter else "현재 혼잡도"
    top_reasons = [x[0] for x in reason_counter.most_common(4)]
    while len(top_reasons) < 4:
        top_reasons.append("-")
    return {
        "total_responses": total,
        "abandon_count": abandon_count,
        "info_help_count": info_help_count,
        "top_info": top_info,
        "top_reason_1": top_reasons[0],
        "top_reason_2": top_reasons[1],
        "top_reason_3": top_reasons[2],
        "top_reason_4": top_reasons[3],
    }

def import_survey_csv(file_bytes: bytes):
    DATA_DIR.mkdir(parents=True, exist_ok=True)
    SURVEY_PATH.write_bytes(file_bytes)
    summary = compute_survey_summary_from_csv(SURVEY_PATH)
    conn = connect()
    conn.execute("""
        UPDATE survey_summary
        SET total_responses=?, abandon_count=?, info_help_count=?, top_info=?, top_reason_1=?, top_reason_2=?, top_reason_3=?, top_reason_4=?
        WHERE id=1
    """, (
        summary["total_responses"], summary["abandon_count"], summary["info_help_count"], summary["top_info"],
        summary["top_reason_1"], summary["top_reason_2"], summary["top_reason_3"], summary["top_reason_4"]
    ))
    conn.commit()
    conn.close()
    return summary

def report_rows():
    state = public_state()
    return [
        ["항목", "값"],
        ["메뉴명", state["menu_name"]],
        ["총 수량", state["total_count"]],
        ["남은 수량", state["remaining_count"]],
        ["판매량", state["sold_count"]],
        ["혼잡도", state["congestion_level"]],
        ["대기시간", state["wait_minutes"]],
        ["소진 예상", state["sellout_eta"]],
        ["업데이트", state["updated_at"]],
    ]

def weekly_report_rows():
    rows = [["날짜", "판매량"]]
    rows.extend(weekly_sales())
    return rows

# -----------------------------------------------------------------------------
# V3 expansion layer: campus-wide services beyond the cafeteria MVP.
# These functions intentionally keep the same FastAPI/SQLite stack so the demo
# remains runnable today, while shaping the API contract for future AI/IoT work.
# -----------------------------------------------------------------------------

def _ensure_v3_tables():
    conn = connect()
    cur = conn.cursor()
    cur.execute("""
        CREATE TABLE IF NOT EXISTS facility_reports (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            created_at TEXT NOT NULL,
            title TEXT NOT NULL,
            description TEXT NOT NULL,
            location TEXT NOT NULL,
            photo_url TEXT,
            category TEXT NOT NULL,
            priority TEXT NOT NULL,
            route_to TEXT NOT NULL,
            status TEXT NOT NULL DEFAULT '접수'
        )
    """)
    conn.commit()
    conn.close()


def classify_facility_report(text: str):
    normalized = text.lower()
    rules = [
        ("안전", "긴급", "시설안전팀", ["위험", "추락", "파손", "미끄", "화재", "연기", "감전", "사고", "유리"]),
        ("전기", "높음", "전기설비팀", ["전등", "조명", "전기", "콘센트", "정전", "누전", "와이파이", "wifi"]),
        ("청소", "보통", "미화관리팀", ["쓰레기", "악취", "오염", "청소", "벌레", "음식물"]),
        ("시설", "보통", "시설관리팀", ["고장", "문", "책상", "의자", "화장실", "수도", "누수", "난방", "냉방"]),
        ("이동", "보통", "캠퍼스운영팀", ["자전거", "킥보드", "통행", "길", "계단", "경사", "주차"]),
    ]
    for category, priority, route_to, keywords in rules:
        if any(keyword in normalized for keyword in keywords):
            return category, priority, route_to
    return "기타", "낮음", "통합운영센터"


def create_facility_report(payload: dict):
    _ensure_v3_tables()
    text = f"{payload.get('title', '')} {payload.get('description', '')} {payload.get('location', '')}"
    category, priority, route_to = classify_facility_report(text)
    conn = connect()
    cur = conn.cursor()
    cur.execute(
        """
        INSERT INTO facility_reports
        (created_at, title, description, location, photo_url, category, priority, route_to, status)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, '접수')
        """,
        (
            now_text(),
            payload.get("title", "제목 없음"),
            payload.get("description", ""),
            payload.get("location", "위치 미상"),
            payload.get("photo_url"),
            category,
            priority,
            route_to,
        ),
    )
    report_id = cur.lastrowid
    conn.commit()
    conn.close()
    add_log("student_api", "FACILITY_REPORT", f"#{report_id} {category}/{priority} -> {route_to}")
    return {
        "ok": True,
        "report_id": report_id,
        "category": category,
        "priority": priority,
        "route_to": route_to,
        "status": "접수",
        "message": "문제가 접수되었고 담당 부서가 1차 분류되었습니다.",
    }


def list_facility_reports(limit: int = 50):
    _ensure_v3_tables()
    conn = connect()
    rows = conn.execute(
        "SELECT * FROM facility_reports ORDER BY id DESC LIMIT ?",
        (max(1, min(int(limit), 200)),),
    ).fetchall()
    conn.close()
    return [dict(r) for r in rows]


def risk_zones():
    return [
        {
            "id": "RISK-001",
            "name": "국제교류관 뒤편 경사로",
            "level": "주의",
            "reason": "야간 조도 낮음 · 보행량 감소 · 경사 구간",
            "recommendation": "야간에는 중앙 통행로 우회 권장",
            "lat": 36.3212,
            "lng": 127.3698,
        },
        {
            "id": "RISK-002",
            "name": "기숙사 후문 연결 계단",
            "level": "관찰",
            "reason": "비 오는 날 미끄럼 가능성 · 킥보드 방치 신고 가능 구간",
            "recommendation": "우천 시 우회 경로 안내 및 시설 점검 필요",
            "lat": 36.3207,
            "lng": 127.3711,
        },
    ]


def mobility_return_zones():
    return [
        {
            "id": "MOB-001",
            "name": "정문 공유모빌리티 정리 구역",
            "allowed": True,
            "capacity": 18,
            "current_count": 11,
            "policy": "반납 가능 · 보행 통로 침범 금지",
        },
        {
            "id": "MOB-002",
            "name": "학생식당 앞 통로",
            "allowed": False,
            "capacity": 0,
            "current_count": 4,
            "policy": "반납 금지 · 혼잡 시간 통행 방해 위험",
        },
    ]


def analytics_insights():
    state = public_state()
    suggestions = []
    if state["remaining_count"] <= 10:
        suggestions.append("소진 임박: 학생 화면에 대체 메뉴 또는 이동 권장 안내를 노출합니다.")
    if state["congestion_level"] == "혼잡":
        suggestions.append("혼잡 상태: 10~15분 뒤 방문 권장 또는 다른 식당 안내를 우선 표시합니다.")
    if state["sold_ratio"] >= 70:
        suggestions.append("판매율 높음: 다음 운영일 예상 수요를 상향 검토합니다.")
    if not suggestions:
        suggestions.append("현재 운영 상태는 안정적입니다. 판매 흐름과 혼잡 로그를 계속 축적합니다.")
    return {
        "generated_at": now_text(),
        "current_state": state,
        "insights": suggestions,
        "data_products": [
            "시간대별 판매량",
            "메뉴별 선호도",
            "혼잡도-대기시간 상관",
            "소진 예상 시간",
            "시설 신고 유형별 빈도",
        ],
    }


def platform_roadmap():
    return {
        "vision": "학생식당 MVP에서 시작해 시설·안전·이동·공간 데이터를 통합하는 스마트캠퍼스 운영 플랫폼으로 확장",
        "phases": [
            {
                "phase": 1,
                "name": "학생식당 실증 MVP",
                "duration": "2026.04~2026.05",
                "scope": ["잔여 수량", "혼잡도", "예상 대기시간", "관리자 반영", "키오스크 API 모의 연동"],
                "deliverable": "본선 시연 가능 웹/Android/관리자 MVP",
            },
            {
                "phase": 2,
                "name": "시범운영",
                "duration": "1~2개월",
                "scope": ["운영업체 협의", "수동 입력 운영", "실제 이용 로그", "학생 피드백 수집"],
                "deliverable": "운영 리포트와 개선 제안서",
            },
            {
                "phase": 3,
                "name": "데이터 자동화",
                "duration": "3~6개월",
                "scope": ["키오스크 판매 로그 연동", "익명 인원 카운터", "알림 자동화", "운영 지표 대시보드"],
                "deliverable": "학교 운영부서용 데이터 대시보드",
            },
            {
                "phase": 4,
                "name": "스마트캠퍼스 확장",
                "duration": "6~12개월",
                "scope": ["시설 민원 자동 분류", "위험 구역 지도", "빈 강의실/학습공간", "모빌리티 반납 질서"],
                "deliverable": "캠퍼스 통합 운영 플랫폼",
            },
            {
                "phase": 5,
                "name": "스마트시티 전환",
                "duration": "12개월 이후",
                "scope": ["지자체 실증", "공공시설 민원", "생활안전", "교통/보행 데이터", "정책 의사결정 지원"],
                "deliverable": "지역 확장형 SaaS/공공 플랫폼 모델",
            },
        ],
    }


def campus_summary():
    state = public_state()
    reports = list_facility_reports(limit=5)
    return {
        "cafeteria": state,
        "facility_reports_count": len(list_facility_reports(limit=200)),
        "recent_facility_reports": reports,
        "risk_zone_count": len(risk_zones()),
        "mobility_zone_count": len(mobility_return_zones()),
        "service_status": {
            "cafeteria_mvp": "운영 가능",
            "facility_report": "MVP API 준비",
            "risk_map": "샘플 데이터 준비",
            "mobility_policy": "샘플 데이터 준비",
            "ai_classification": "규칙 기반 MVP, 향후 ML 모델 교체 예정",
        },
    }

# -----------------------------------------------------------------------------
# V4 school-pilot layer: student auth, operational status, backup metadata.
# This is still MVP-grade, but it gives the project a migration path toward
# university SSO/OAuth without changing the Android client contract.
# -----------------------------------------------------------------------------

def _ensure_v4_tables():
    conn = connect()
    cur = conn.cursor()
    cur.execute("""
        CREATE TABLE IF NOT EXISTS student_users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            student_no TEXT UNIQUE NOT NULL,
            name TEXT NOT NULL,
            department TEXT NOT NULL,
            password_hash TEXT NOT NULL,
            is_active INTEGER NOT NULL DEFAULT 1,
            created_at TEXT NOT NULL
        )
    """)
    cur.execute("SELECT COUNT(*) c FROM student_users")
    if cur.fetchone()["c"] == 0:
        cur.execute(
            "INSERT INTO student_users (student_no, name, department, password_hash, is_active, created_at) VALUES (?, ?, ?, ?, 1, ?)",
            ("20260001", "시연학생", "컴퓨터공학과", hash_password("demo1234"), now_text())
        )
    conn.commit()
    conn.close()


def verify_student_user(student_no: str, password: str):
    _ensure_v4_tables()
    conn = connect()
    row = conn.execute(
        "SELECT id, student_no, name, department, password_hash FROM student_users WHERE student_no = ? AND is_active = 1",
        (student_no,)
    ).fetchone()
    conn.close()
    if not row:
        return None
    data = dict(row)
    if not verify_password(password, data.pop("password_hash")):
        return None
    return data


def get_student_profile(student_no: str):
    _ensure_v4_tables()
    conn = connect()
    row = conn.execute(
        "SELECT id, student_no, name, department, is_active, created_at FROM student_users WHERE student_no = ? AND is_active = 1",
        (student_no,)
    ).fetchone()
    conn.close()
    return dict(row) if row else None


def ops_status():
    DATA_DIR.mkdir(parents=True, exist_ok=True)
    backups = sorted((DATA_DIR / "backups").glob("citybrain_backup_*.db")) if (DATA_DIR / "backups").exists() else []
    return {
        "ok": True,
        "generated_at": now_text(),
        "database": {
            "type": "SQLite",
            "path": str(DB_PATH),
            "exists": DB_PATH.exists(),
            "size_bytes": DB_PATH.stat().st_size if DB_PATH.exists() else 0,
            "latest_backup": backups[-1].name if backups else None,
        },
        "security": {
            "admin_cookie": "httponly / samesite=lax",
            "student_auth": "signed bearer token MVP",
            "production_required": ["HTTPS", "school SSO/OIDC", "role based access control", "audit retention policy"],
        },
        "service": {
            "cafeteria": "ready",
            "admin_dashboard": "ready",
            "android_api": "ready",
            "kiosk_api": "mock-ready",
            "facility_report_api": "mvp-ready",
        }
    }
