#!/usr/bin/env bash
set -euo pipefail

MODE="${1:-phone}"
PORT="${PORT:-8000}"
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
ANDROID_PROPS="$ROOT_DIR/android/gradle.properties"

get_ip() {
  ipconfig getifaddr en0 2>/dev/null || ipconfig getifaddr en1 2>/dev/null || echo "127.0.0.1"
}

if [[ "$MODE" == "emulator" ]]; then
  BASE_URL="http://10.0.2.2:${PORT}/"
  PHONE_URL="http://127.0.0.1:${PORT}/"
else
  IP="$(get_ip)"
  BASE_URL="http://${IP}:${PORT}/"
  PHONE_URL="$BASE_URL"
fi

python3 - <<PY
from pathlib import Path
p = Path('$ANDROID_PROPS')
text = p.read_text() if p.exists() else ''
lines = [x for x in text.splitlines() if not x.startswith('CITYBRAIN_BASE_URL=')]
lines.append('CITYBRAIN_BASE_URL=$BASE_URL')
p.write_text('\n'.join(lines) + '\n')
PY

cd "$ROOT_DIR/backend"
if [[ ! -d .venv ]]; then
  python3 -m venv .venv
fi
source .venv/bin/activate
python -m pip install --upgrade pip
python -m pip install -r requirements.txt

echo ""
echo "CityBrain server"
echo "Student Web: http://127.0.0.1:${PORT}/"
echo "Admin Web  : http://127.0.0.1:${PORT}/admin/login"
echo "Phone Web  : ${PHONE_URL}"
echo "Android BASE_URL: ${BASE_URL}"
echo ""
python -m uvicorn app.main:app --host 0.0.0.0 --port "$PORT" --reload
