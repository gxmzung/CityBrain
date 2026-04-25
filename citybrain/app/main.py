from __future__ import annotations
import sqlite3
from pathlib import Path
from contextlib import contextmanager
from datetime import datetime
from typing import Generator
from fastapi import FastAPI, Form, Request
from fastapi.responses import HTMLResponse, RedirectResponse, JSONResponse
from fastapi.staticfiles import StaticFiles
from fastapi.templating import Jinja2Templates

BASE_DIR = Path(__file__).resolve().parent.parent
DB_PATH = BASE_DIR / 'citybrain.db'
app = FastAPI(title='CityBrain Cafeteria Demo')
app.mount('/static', StaticFiles(directory=str(BASE_DIR/'app'/'static')), name='static')
templates = Jinja2Templates(directory=str(BASE_DIR/'app'/'templates'))

def now(): return datetime.now().strftime('%Y-%m-%d %H:%M:%S')
@contextmanager
def db() -> Generator[sqlite3.Connection, None, None]:
    conn=sqlite3.connect(DB_PATH); conn.row_factory=sqlite3.Row
    try:
        yield conn; conn.commit()
    finally: conn.close()

def init_db():
    with db() as conn:
        conn.execute("""CREATE TABLE IF NOT EXISTS state (
            id INTEGER PRIMARY KEY CHECK (id=1), menu TEXT, total INTEGER, remaining INTEGER,
            congestion TEXT, wait_min INTEGER, crowd_count INTEGER, sellout_time TEXT,
            likes INTEGER, survey_total INTEGER, updated_at TEXT)""")
        conn.execute("""CREATE TABLE IF NOT EXISTS events (
            id INTEGER PRIMARY KEY AUTOINCREMENT, created_at TEXT, event TEXT, detail TEXT)""")
        if conn.execute('SELECT COUNT(*) c FROM state').fetchone()['c']==0:
            conn.execute("""INSERT INTO state VALUES (1, '오늘의 일품식 · 에비덮밥', 100, 63, '보통', 7, 24, '12:45', 86, 124, ?)""",(now(),))
            conn.execute('INSERT INTO events (created_at,event,detail) VALUES (?,?,?)',(now(),'초기 설정','일품식 100개, 잔여 63개, 혼잡도 보통'))

def get_state():
    with db() as conn:
        s=dict(conn.execute('SELECT * FROM state WHERE id=1').fetchone())
        events=[dict(r) for r in conn.execute('SELECT * FROM events ORDER BY id DESC LIMIT 8').fetchall()]
    ratio = int(s['remaining']/max(s['total'],1)*100)
    s['ratio']=ratio
    s['sold']=s['total']-s['remaining']
    s['events']=events
    return s

@app.on_event('startup')
def startup(): init_db()

@app.get('/', response_class=HTMLResponse)
def home(request: Request): return templates.TemplateResponse('home.html', {'request':request, 's':get_state()})

@app.get('/student', response_class=HTMLResponse)
def student(request: Request): return templates.TemplateResponse('student.html', {'request':request, 's':get_state()})

@app.get('/admin', response_class=HTMLResponse)
def admin(request: Request): return templates.TemplateResponse('admin.html', {'request':request, 's':get_state()})

@app.get('/logs', response_class=HTMLResponse)
def logs(request: Request): return templates.TemplateResponse('logs.html', {'request':request, 's':get_state()})

@app.post('/admin/sale')
def sale(qty: int = Form(1)):
    with db() as conn:
        s=conn.execute('SELECT remaining FROM state WHERE id=1').fetchone()
        new=max(0, s['remaining']-qty)
        conn.execute('UPDATE state SET remaining=?, updated_at=? WHERE id=1',(new,now()))
        conn.execute('INSERT INTO events (created_at,event,detail) VALUES (?,?,?)',(now(),'판매 반영',f'{qty}건 판매 반영, 잔여 {new}개'))
    return RedirectResponse('/admin', status_code=303)

@app.post('/admin/crowd')
def crowd(congestion: str = Form(...)):
    wait = {'원활':3,'보통':7,'혼잡':15,'매우 혼잡':22}.get(congestion,7)
    count = {'원활':12,'보통':24,'혼잡':46,'매우 혼잡':68}.get(congestion,24)
    with db() as conn:
        conn.execute('UPDATE state SET congestion=?, wait_min=?, crowd_count=?, updated_at=? WHERE id=1',(congestion,wait,count,now()))
        conn.execute('INSERT INTO events (created_at,event,detail) VALUES (?,?,?)',(now(),'혼잡도 변경',f'{congestion}, 예상 대기시간 {wait}분'))
    return RedirectResponse('/admin', status_code=303)

@app.post('/admin/reset')
def reset():
    with db() as conn:
        conn.execute("UPDATE state SET total=100, remaining=63, congestion='보통', wait_min=7, crowd_count=24, sellout_time='12:45', updated_at=? WHERE id=1",(now(),))
        conn.execute('INSERT INTO events (created_at,event,detail) VALUES (?,?,?)',(now(),'시연 초기화','63개 남음 / 보통 / 7분으로 초기화'))
    return RedirectResponse('/admin', status_code=303)

@app.post('/survey')
def survey():
    with db() as conn:
        s=conn.execute('SELECT likes,survey_total FROM state WHERE id=1').fetchone()
        conn.execute('UPDATE state SET likes=?, survey_total=?, updated_at=? WHERE id=1',(s['likes']+1, s['survey_total']+1, now()))
        conn.execute('INSERT INTO events (created_at,event,detail) VALUES (?,?,?)',(now(),'선호도 응답','오늘의 일품식 선호도 응답 1건 추가'))
    return RedirectResponse('/student', status_code=303)

@app.get('/api/state')
def api(): return JSONResponse(get_state())
