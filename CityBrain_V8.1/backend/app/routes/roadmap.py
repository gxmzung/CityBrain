from pydantic import BaseModel, Field
from fastapi import APIRouter
from app.db import (
    analytics_insights,
    campus_summary,
    create_facility_report,
    list_facility_reports,
    mobility_return_zones,
    platform_roadmap,
    public_state,
    risk_zones,
)

router = APIRouter(prefix="/api", tags=["platform-expansion"])


class FacilityReportIn(BaseModel):
    title: str = Field(..., min_length=2, max_length=80)
    description: str = Field(..., min_length=2, max_length=600)
    location: str = Field(..., min_length=2, max_length=120)
    photo_url: str | None = None


@router.get("/health")
def health_check():
    return {"ok": True, "service": "CityBrain", "state": public_state()}


@router.get("/campus/summary")
def campus_summary_api():
    """학생식당 MVP를 캠퍼스 운영 플랫폼으로 확장할 때 쓰는 통합 요약 API."""
    return campus_summary()


@router.get("/roadmap")
def roadmap_api():
    """발표/사업화 계획서에 넣을 수 있는 단계별 확장 로드맵."""
    return platform_roadmap()


@router.post("/facility/reports")
def create_facility_report_api(payload: FacilityReportIn):
    """사진+텍스트 민원을 접수하고 키워드 기반으로 1차 자동 분류한다.

    실제 AI 모델이 붙기 전까지는 MVP용 규칙 기반 분류로 운영 가능성을 보여준다.
    이후에는 같은 입출력 구조에 이미지/NLP 모델만 교체하면 된다.
    """
    return create_facility_report(payload.model_dump())


@router.get("/facility/reports")
def list_facility_reports_api(limit: int = 50):
    return list_facility_reports(limit=limit)


@router.get("/risk-zones")
def risk_zones_api():
    return risk_zones()


@router.get("/mobility/return-zones")
def mobility_return_zones_api():
    return mobility_return_zones()


@router.get("/analytics/insights")
def analytics_insights_api():
    return analytics_insights()
