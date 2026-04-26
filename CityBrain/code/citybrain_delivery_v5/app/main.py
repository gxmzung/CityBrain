from pathlib import Path
from fastapi import FastAPI
from fastapi.staticfiles import StaticFiles
from app.db import init_db
from app.routes import student, admin, kiosk

app = FastAPI(title="CityBrain Delivery V5")
app.mount("/static", StaticFiles(directory=str(Path(__file__).parent / "static")), name="static")

@app.on_event("startup")
def startup():
    init_db()

app.include_router(student.router)
app.include_router(admin.router)
app.include_router(kiosk.router)