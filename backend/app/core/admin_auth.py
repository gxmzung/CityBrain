import os
from typing import Optional

from fastapi import Header, HTTPException, Query, status


DEFAULT_ADMIN_KEY = "citybrain-local-admin"
# For real pilot use, set CITYBRAIN_ADMIN_KEY in the environment.


def get_admin_key() -> str:
    return os.getenv("CITYBRAIN_ADMIN_KEY", DEFAULT_ADMIN_KEY)


def require_admin_key(
    admin_key: Optional[str] = Query(default=None),
    x_admin_key: Optional[str] = Header(default=None, alias="X-Admin-Key"),
):
    expected = get_admin_key()
    provided = x_admin_key or admin_key

    if not provided or provided != expected:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="admin key required",
        )

    return True
