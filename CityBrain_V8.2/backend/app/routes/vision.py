from fastapi import APIRouter
import httpx

router = APIRouter(prefix="/api/vision", tags=["vision"])

YOLO_CONGESTION_API = "http://127.0.0.1:8081/api/congestion/latest"


@router.get("/congestion")
async def get_vision_congestion():
    """
    Proxy endpoint for CityBrain Vision Congestion Demo.

    CityBrain backend calls the YOLO congestion module running on port 8081.
    If the vision module is unavailable, this endpoint returns a safe fallback.
    """
    try:
        async with httpx.AsyncClient(timeout=2.0) as client:
            response = await client.get(YOLO_CONGESTION_API)
            response.raise_for_status()
            data = response.json()

        return {
            "ok": True,
            "source": "yolo_vision_module",
            "person_count": data.get("person_count", 0),
            "congestion": data.get("congestion", "알 수 없음"),
            "updated_at": data.get("updated_at"),
            "method": data.get("method", "RTSP/Webcam + YOLO person detection"),
            "privacy_note": data.get(
                "privacy_note",
                "원본 저장 없이 사람 수 기반 혼잡도 통계값 산출 목적",
            ),
        }

    except Exception as exc:
        return {
            "ok": False,
            "source": "fallback",
            "person_count": None,
            "congestion": "수동 확인 필요",
            "updated_at": None,
            "error": str(exc),
            "message": "YOLO 혼잡도 모듈에 연결할 수 없어 수동 혼잡도 확인이 필요합니다.",
        }
