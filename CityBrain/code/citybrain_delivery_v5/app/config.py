import os
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent.parent
DATA_DIR = BASE_DIR / "data"

ADMIN_USERNAME = os.getenv("ADMIN_USERNAME", "admin")
ADMIN_PASSWORD = os.getenv("ADMIN_PASSWORD", "change-me-now")
SECRET_KEY = os.getenv("SECRET_KEY", "dev-secret-key")
COOKIE_NAME = os.getenv("COOKIE_NAME", "citybrain_admin")