from app.db import (
    analytics_insights,
    create_facility_report,
    init_db,
    mobility_return_zones,
    platform_roadmap,
    risk_zones,
)


def test_expansion_contract():
    init_db()
    report = create_facility_report({
        "title": "전등 고장",
        "description": "중앙도서관 5층 조명이 꺼져 있습니다.",
        "location": "중앙도서관 5층",
    })
    assert report["ok"] is True
    assert report["category"] in {"전기", "시설", "안전", "청소", "이동", "기타"}
    assert len(risk_zones()) >= 1
    assert len(mobility_return_zones()) >= 1
    assert platform_roadmap()["phases"][0]["phase"] == 1
    assert len(analytics_insights()["insights"]) >= 1
