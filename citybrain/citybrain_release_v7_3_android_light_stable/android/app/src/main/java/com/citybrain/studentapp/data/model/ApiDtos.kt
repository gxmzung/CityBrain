package com.citybrain.studentapp.data.model

data class StateDto(
    val menu_name: String,
    val total_count: Int? = null,
    val remaining_count: Int,
    val sold_count: Int,
    val congestion_level: String,
    val wait_minutes: Int,
    val sellout_eta: String,
    val current_people: Int? = null,
    val updated_at: String? = null,
    val sold_ratio: Double? = null,
    val remaining_ratio: Double? = null,
    val judgement: String? = null
)

data class SurveyDto(
    val total_responses: Int,
    val abandon_count: Int,
    val info_help_count: Int,
    val top_info: String,
    val top_reason_1: String,
    val top_reason_2: String,
    val top_reason_3: String,
    val top_reason_4: String
)

data class NoticeDto(
    val title: String,
    val body: String,
    val created_at: String
)

fun StateDto.toDomain(): StudentState = StudentState(
    menuName = menu_name,
    totalCount = total_count ?: (remaining_count + sold_count),
    remainingCount = remaining_count,
    congestionLevel = congestion_level,
    waitMinutes = wait_minutes,
    selloutEta = sellout_eta,
    soldCount = sold_count,
    currentPeople = current_people ?: 31,
    updatedAt = updated_at ?: "방금 전",
    judgement = judgement ?: if (congestion_level == "혼잡") "시간 조정 권장" else "지금 이용 가능"
)

fun SurveyDto.toDomain(): SurveySummary = SurveySummary(
    totalResponses = total_responses,
    abandonCount = abandon_count,
    infoHelpCount = info_help_count,
    topInfo = top_info,
    topReasons = listOf(top_reason_1, top_reason_2, top_reason_3, top_reason_4)
)

fun NoticeDto.toDomain(): NoticeItem = NoticeItem(
    title = title,
    body = body,
    createdAt = created_at
)

data class CampusSummaryDto(
    val cafeteria: StateDto,
    val facility_reports_count: Int,
    val recent_facility_reports: List<FacilityReportDto>,
    val risk_zone_count: Int,
    val mobility_zone_count: Int,
    val service_status: Map<String, String>
)

data class FacilityReportDto(
    val id: Int? = null,
    val created_at: String? = null,
    val title: String,
    val description: String,
    val location: String,
    val photo_url: String? = null,
    val category: String? = null,
    val priority: String? = null,
    val route_to: String? = null,
    val status: String? = null
)

data class FacilityReportRequest(
    val title: String,
    val description: String,
    val location: String,
    val photo_url: String? = null
)

data class FacilityReportResponse(
    val ok: Boolean,
    val report_id: Int,
    val category: String,
    val priority: String,
    val route_to: String,
    val status: String,
    val message: String
)

data class RiskZoneDto(
    val id: String,
    val name: String,
    val level: String,
    val reason: String,
    val recommendation: String,
    val lat: Double,
    val lng: Double
)

data class MobilityReturnZoneDto(
    val id: String,
    val name: String,
    val allowed: Boolean,
    val capacity: Int,
    val current_count: Int,
    val policy: String
)

data class AnalyticsInsightsDto(
    val generated_at: String,
    val current_state: StateDto,
    val insights: List<String>,
    val data_products: List<String>
)

data class RoadmapDto(
    val vision: String,
    val phases: List<RoadmapPhaseDto>
)

data class RoadmapPhaseDto(
    val phase: Int,
    val name: String,
    val duration: String,
    val scope: List<String>,
    val deliverable: String
)

data class StudentLoginRequest(
    val student_no: String,
    val password: String
)

data class StudentProfileDto(
    val id: Int,
    val student_no: String,
    val name: String,
    val department: String,
    val is_active: Int? = null,
    val created_at: String? = null
)

data class StudentLoginResponse(
    val access_token: String,
    val token_type: String,
    val student: StudentProfileDto
)

object DemoAccount {
    const val STUDENT_NO = "20260001"
    const val PASSWORD = "demo1234"
}
