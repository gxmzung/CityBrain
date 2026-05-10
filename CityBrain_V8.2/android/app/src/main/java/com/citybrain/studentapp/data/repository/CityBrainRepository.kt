package com.citybrain.studentapp.data.repository

import com.citybrain.studentapp.BuildConfig
import com.citybrain.studentapp.data.model.AnalyticsInsightsDto
import com.citybrain.studentapp.data.model.CampusSummaryDto
import com.citybrain.studentapp.data.model.FacilityReportRequest
import com.citybrain.studentapp.data.model.FacilityReportResponse
import com.citybrain.studentapp.data.model.HomeBundle
import com.citybrain.studentapp.data.model.MobilityReturnZoneDto
import com.citybrain.studentapp.data.model.NoticeDto
import com.citybrain.studentapp.data.model.RiskZoneDto
import com.citybrain.studentapp.data.model.RoadmapDto
import com.citybrain.studentapp.data.model.StateDto
import com.citybrain.studentapp.data.model.StudentLoginRequest
import com.citybrain.studentapp.data.model.StudentLoginResponse
import com.citybrain.studentapp.data.model.StudentProfileDto
import com.citybrain.studentapp.data.model.SurveyDto
import com.citybrain.studentapp.data.model.toDomain
import com.citybrain.studentapp.data.remote.CityBrainApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class CityBrainRepository(
    private val api: CityBrainApi
) {
    suspend fun loginStudent(studentNo: String, password: String): StudentLoginResponse =
        api.loginStudent(StudentLoginRequest(student_no = studentNo, password = password))

    suspend fun getStudentMe(token: String): StudentProfileDto =
        api.getStudentMe("Bearer " + token)

    suspend fun getHomeBundle(): HomeBundle {
        return runCatching {
            val state = api.getState().toDomain()
            val survey = api.getSurvey().toDomain()
            val notices = api.getNotices().map { it.toDomain() }

            HomeBundle(
                state = state,
                survey = survey,
                notices = notices
            )
        }.getOrElse {
            // 발표 중 네트워크가 끊겨도 앱 화면이 빈 화면으로 무너지지 않도록 예비값을 둔다.
            HomeBundle(
                state = StateDto(
                    menu_name = "서가앤쿡 목살필라프",
                    total_count = 100,
                    remaining_count = 63,
                    sold_count = 37,
                    congestion_level = "보통",
                    wait_minutes = 7,
                    sellout_eta = "12:45",
                    current_people = 31,
                    updated_at = "오프라인 예비값",
                    judgement = "지금 이용 가능"
                ).toDomain(),
                survey = SurveyDto(
                    total_responses = 96,
                    abandon_count = 76,
                    info_help_count = 85,
                    top_info = "현재 혼잡도",
                    top_reason_1 = "외부 식당이 더 나아서",
                    top_reason_2 = "가격 대비 만족도가 낮아서",
                    top_reason_3 = "메뉴가 다양하지 않아서",
                    top_reason_4 = "대기시간이 길어서"
                ).toDomain(),
                notices = listOf(
                    NoticeDto(
                        title = "시범운영 안내",
                        body = "잔여 수량 · 혼잡도 · 예상 대기시간 정보를 시범 제공 중입니다.",
                        created_at = "2026-04-27"
                    ).toDomain(),
                    NoticeDto(
                        title = "혼잡 시간대 참고",
                        body = "12:10 ~ 12:35 구간이 가장 붐빌 수 있습니다.",
                        created_at = "2026-04-27"
                    ).toDomain()
                )
            )
        }
    }

    suspend fun getCampusSummary(): CampusSummaryDto = api.getCampusSummary()

    suspend fun submitFacilityReport(
        title: String,
        description: String,
        location: String,
        photoUrl: String? = null
    ): FacilityReportResponse {
        return api.submitFacilityReport(
            FacilityReportRequest(
                title = title,
                description = description,
                location = location,
                photo_url = photoUrl
            )
        )
    }

    suspend fun getRiskZones(): List<RiskZoneDto> = api.getRiskZones()

    suspend fun getMobilityReturnZones(): List<MobilityReturnZoneDto> = api.getMobilityReturnZones()

    suspend fun getAnalyticsInsights(): AnalyticsInsightsDto = api.getAnalyticsInsights()

    suspend fun getRoadmap(): RoadmapDto = api.getRoadmap()

    companion object {
        fun create(): CityBrainRepository {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return CityBrainRepository(
                api = retrofit.create(CityBrainApi::class.java)
            )
        }
    }
}
