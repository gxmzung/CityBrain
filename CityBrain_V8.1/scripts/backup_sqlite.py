from __future__ import annotations
import shutil
from datetime import datetime
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DB = ROOT / "backend" / "data" / "citybrain_final.db"
BACKUP_DIR = ROOT / "backend" / "data" / "backups"

BACKUP_DIR.mkdir(parents=True, exist_ok=True)
if not DB.exists():
    raise SystemExit(f"DB not found: {DB}")
backup = BACKUP_DIR / f"citybrain_backup_{datetime.now().strftime('%Y%m%d_%H%M%S')}.db"
shutil.copy2(DB, backup)
print(f"Backup created: {backup}")
