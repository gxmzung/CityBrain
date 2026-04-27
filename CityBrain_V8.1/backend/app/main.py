from pathlib import Path
from fastapi import FastAPI
from fastapi.staticfiles import StaticFiles
from app.db import init_db
from app.routes import student, admin, kiosk, roadmap, auth, ops

app = FastAPI(title="CityBrain Final MVP", version="1.0.0")
app.mount("/static", StaticFiles(directory=str(Path(__file__).parent / "static")), name="static")

@app.on_event("startup")
def startup():
    init_db()

app.include_router(student.router)
app.include_router(admin.router)
app.include_router(kiosk.router)
app.include_router(roadmap.router)
app.include_router(auth.router)
app.include_router(ops.router)
