from fastapi import APIRouter
from pydantic import BaseModel
from app.db import apply_sale, add_log, public_state

router = APIRouter(prefix="/api/kiosk", tags=["kiosk"])

class KioskSale(BaseModel):
    menu_name: str = "돈까스덮밥"
    sold_delta: int
    congestion_level: str = "보통"
    wait_minutes: int | None = None

@router.post("/sales")
def kiosk_sales(payload: KioskSale):
    state = apply_sale(
        menu_name=payload.menu_name,
        sold_delta=payload.sold_delta,
        congestion_level=payload.congestion_level,
        wait_minutes=payload.wait_minutes,
        source="kiosk_api",
    )
    add_log("kiosk_api", "KIOSK_SALE", f"{payload.menu_name} / +{payload.sold_delta} / 잔여 {state['remaining_count']}")
    return {"ok": True, "state": state}

@router.get("/state")
def kiosk_state():
    return public_state()
