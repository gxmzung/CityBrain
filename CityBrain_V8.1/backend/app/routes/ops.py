from fastapi import APIRouter
from app.db import ops_status

router = APIRouter(prefix="/api/ops", tags=["operations"])

@router.get("/status")
def status():
    return ops_status()
