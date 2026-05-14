package com.citybrain.studentapp.data.remote

import com.citybrain.studentapp.data.model.AnalyticsInsightsDto
import com.citybrain.studentapp.data.model.CampusSummaryDto
import com.citybrain.studentapp.data.model.FacilityReportDto
import com.citybrain.studentapp.data.model.FacilityReportRequest
import com.citybrain.studentapp.data.model.FacilityReportResponse
import com.citybrain.studentapp.data.model.MobilityReturnZoneDto
import com.citybrain.studentapp.data.model.NoticeDto
import com.citybrain.studentapp.data.model.RiskZoneDto
import com.citybrain.studentapp.data.model.RoadmapDto
import com.citybrain.studentapp.data.model.StudentLoginRequest
import com.citybrain.studentapp.data.model.StudentLoginResponse
import com.citybrain.studentapp.data.model.StudentProfileDto
import com.citybrain.studentapp.data.model.StateDto
import com.citybrain.studentapp.data.model.SurveyDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface CityBrainApi {
    @POST("api/student/auth/login")
    suspend fun loginStudent(@Body request: StudentLoginRequest): StudentLoginResponse

    @GET("api/student/auth/me")
    suspend fun getStudentMe(@Header("Authorization") authorization: String): StudentProfileDto

    @GET("api/student/state")
    suspend fun getState(): StateDto

    @GET("api/student/survey")
    suspend fun getSurvey(): SurveyDto

    @GET("api/student/notices")
    suspend fun getNotices(): List<NoticeDto>

    @GET("api/campus/summary")
    suspend fun getCampusSummary(): CampusSummaryDto

    @GET("api/facility/reports")
    suspend fun getFacilityReports(): List<FacilityReportDto>

    @POST("api/facility/reports")
    suspend fun submitFacilityReport(@Body request: FacilityReportRequest): FacilityReportResponse

    @GET("api/risk-zones")
    suspend fun getRiskZones(): List<RiskZoneDto>

    @GET("api/mobility/return-zones")
    suspend fun getMobilityReturnZones(): List<MobilityReturnZoneDto>

    @GET("api/analytics/insights")
    suspend fun getAnalyticsInsights(): AnalyticsInsightsDto

    @GET("api/roadmap")
    suspend fun getRoadmap(): RoadmapDto
}
