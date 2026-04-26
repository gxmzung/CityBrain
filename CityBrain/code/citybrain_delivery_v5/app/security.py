import os, base64, hashlib, hmac
from itsdangerous import URLSafeSerializer
from app.config import SECRET_KEY

serializer = URLSafeSerializer(SECRET_KEY, salt="citybrain-auth")

def hash_password(password: str, salt: bytes | None = None) -> str:
    salt = salt or os.urandom(16)
    dk = hashlib.pbkdf2_hmac("sha256", password.encode("utf-8"), salt, 200_000)
    return base64.b64encode(salt + dk).decode("utf-8")

def verify_password(password: str, encoded: str) -> bool:
    raw = base64.b64decode(encoded.encode("utf-8"))
    salt, saved = raw[:16], raw[16:]
    test = hashlib.pbkdf2_hmac("sha256", password.encode("utf-8"), salt, 200_000)
    return hmac.compare_digest(saved, test)

def create_token(payload: dict) -> str:
    return serializer.dumps(payload)

def verify_token(token: str):
    try:
        return serializer.loads(token)
    except Exception:
        return None