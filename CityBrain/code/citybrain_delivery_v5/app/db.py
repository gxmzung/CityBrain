import csv
import sqlite3
from collections import Counter
from datetime import datetime, timedelta
from pathlib import Path
from app.config import DATA_DIR, ADMIN_USERNAME, ADMIN_PASSWORD
from app.security import hash_password, verify_password

DB_PATH = DATA_DIR / "citybrain_v5.db"
SURVEY_PATH = DATA_DIR / "survey_latest.csv"

def connect():
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    return conn

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
            sellout_eta TEXT NOT NULL
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

    cur.execute("SELECT COUNT(*) c FROM users")
    if cur.fetchone()["c"] == 0:
        cur.execute("INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)", (ADMIN_USERNAME, hash_password(ADMIN_PASSWORD), "admin"))
        cur.execute("INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)", ("operator", hash_password("operator123"), "operator"))
        cur.execute("INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)", ("viewer", hash_password("viewer123"), "viewer"))

    cur.execute("SELECT COUNT(*) c FROM state")
    if cur.fetchone()["c"] == 0:
        cur.execute("""
            INSERT INTO state (id, menu_name, total_count, remaining_count, sold_count, congestion_level, wait_minutes, sellout_eta)
            VALUES (1, '에비동', 100, 63, 37, '보통', 7, '12:45')
        """)
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
            (datetime.now().strftime("%Y-%m-%d %H:%M:%S"), "시범운영 안내", "잔여 수량·혼잡도·예상 대기시간 정보를 시범 제공 중입니다.")
        )
    conn.commit()
    conn.close()

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
        new_val = 0 if row["is_active"] else 1
        conn.execute("UPDATE users SET is_active = ? WHERE id = ?", (new_val, user_id))
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

def update_state(payload: dict):
    conn = connect()
    conn.execute(
        "UPDATE state SET menu_name=?, total_count=?, remaining_count=?, sold_count=?, congestion_level=?, wait_minutes=?, sellout_eta=? WHERE id=1",
        (payload["menu_name"], payload["total_count"], payload["remaining_count"], payload["sold_count"], payload["congestion_level"], payload["wait_minutes"], payload["sellout_eta"])
    )
    conn.commit()
    conn.close()

def add_log(actor: str, action: str, detail: str):
    conn = connect()
    conn.execute("INSERT INTO audit_logs (created_at, actor, action, detail) VALUES (?, ?, ?, ?)",
                 (datetime.now().strftime("%Y-%m-%d %H:%M:%S"), actor, action, detail))
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

def add_sale_history(menu_name: str, sold_delta: int, remaining_count: int, congestion_level: str, source: str = "manual"):
    conn = connect()
    conn.execute(
        "INSERT INTO sales_history (created_at, menu_name, sold_delta, remaining_count, congestion_level, source) VALUES (?, ?, ?, ?, ?, ?)",
        (datetime.now().strftime("%Y-%m-%d %H:%M:%S"), menu_name, sold_delta, remaining_count, congestion_level, source)
    )
    conn.commit()
    conn.close()

def sales_by_menu():
    conn = connect()
    rows = conn.execute("SELECT menu_name, SUM(sold_delta) as total FROM sales_history GROUP BY menu_name ORDER BY total DESC").fetchall()
    conn.close()
    return [(r["menu_name"], r["total"] or 0) for r in rows] or [("에비동", 37)]

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
    return result or [("12시", 20), ("13시", 12), ("18시", 5)]

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
    rows = conn.execute("SELECT * FROM notices WHERE is_active = 1 ORDER BY id DESC").fetchall()
    conn.close()
    return [dict(r) for r in rows]

def add_notice(title: str, body: str):
    conn = connect()
    conn.execute("INSERT INTO notices (created_at, title, body, is_active) VALUES (?, ?, ?, 1)",
                 (datetime.now().strftime("%Y-%m-%d %H:%M:%S"), title, body))
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
                for p in [x.strip() for x in val.split(";") if x.strip()]:
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
    state = get_state()
    survey = get_survey_summary()
    return [
        {"item": "menu_name", "value": state["menu_name"]},
        {"item": "total_count", "value": state["total_count"]},
        {"item": "remaining_count", "value": state["remaining_count"]},
        {"item": "sold_count", "value": state["sold_count"]},
        {"item": "congestion_level", "value": state["congestion_level"]},
        {"item": "wait_minutes", "value": state["wait_minutes"]},
        {"item": "survey_total_responses", "value": survey["total_responses"]},
        {"item": "survey_abandon_count", "value": survey["abandon_count"]},
        {"item": "survey_info_help_count", "value": survey["info_help_count"]},
        {"item": "survey_top_info", "value": survey["top_info"]},
    ]

def weekly_report_rows():
    rows = []
    for day, total in weekly_sales():
        rows.append({"day": day, "sold_total": total})
    return rows