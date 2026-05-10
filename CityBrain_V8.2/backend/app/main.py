"""
CityBrain Backend Entry Point

Creates the FastAPI application, mounts static assets,
initializes the database on startup, and registers feature routers.

Maintainer:
- 이영준

Notes:
- Detailed study notes are kept in docs/code-reading-log.md.
- Implementation history should be checked through Git commit logs.
"""

from pathlib import Path

from fastapi import FastAPI
from fastapi.staticfiles import StaticFiles

from app.db import init_db
from app.routes import student, admin, kiosk, roadmap, auth, ops, jarvis


app = FastAPI(
    title="CityBrain Final MVP",
    version="1.0.0"
)


# Serve CSS, JavaScript, and image assets from app/static.
app.mount(
    "/static",
    StaticFiles(directory=str(Path(__file__).parent / "static")),
    name="static"
)


@app.on_event("startup")
def startup():
    """Initialize database tables and default state when the server starts."""
    init_db()


# Register feature routers.
app.include_router(student.router)   # Student-facing cafeteria status
app.include_router(admin.router)     # Admin dashboard and manual updates
app.include_router(kiosk.router)     # Kiosk / sales-flow prototype
app.include_router(roadmap.router)   # Roadmap and expansion pages
app.include_router(auth.router)      # Login and authentication
app.include_router(ops.router)       # Operations data and status
app.include_router(jarvis.router)    # Jarvis assistant prototype