from fastapi import APIRouter
from pydantic import BaseModel
from app.db import get_state, update_state, add_sale_history, add_log

router = APIRouter(prefix="/api/kiosk", tags=["kiosk"])

class KioskSale(BaseModel):
    menu_name: str
    sold_delta: int
    congestion_level: str = "보통"

@router.post("/sales")
def kiosk_sales(payload: KioskSale):
    state = get_state()
    sold_delta = max(0, payload.sold_delta)
    new_sold = state["sold_count"] + sold_delta
    new_remaining = max(0, state["remaining_count"] - sold_delta)
    state.update({
        "menu_name": payload.menu_name,
        "sold_count": new_sold,
        "remaining_count": new_remaining,
        "congestion_level": payload.congestion_level,
    })
    update_state(state)
    add_sale_history(payload.menu_name, sold_delta, new_remaining, payload.congestion_level, source="kiosk_mock")
    add_log("kiosk_mock", "KIOSK_SALE", f"{payload.menu_name} / +{sold_delta} / 잔여 {new_remaining}")
    return {"ok": True, "state": state}