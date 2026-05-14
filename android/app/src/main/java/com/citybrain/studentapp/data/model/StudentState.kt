package com.citybrain.studentapp.data.model

data class StudentState(
    val menuName: String,
    val totalCount: Int,
    val remainingCount: Int,
    val congestionLevel: String,
    val waitMinutes: Int,
    val selloutEta: String,
    val soldCount: Int,
    val currentPeople: Int,
    val updatedAt: String,
    val judgement: String
) {
    val remainingRatio: Float
        get() = if (totalCount <= 0) 0f else remainingCount.toFloat() / totalCount.toFloat()
}

data class SurveySummary(
    val totalResponses: Int,
    val abandonCount: Int,
    val infoHelpCount: Int,
    val topInfo: String,
    val topReasons: List<String>
)

data class NoticeItem(
    val title: String,
    val body: String,
    val createdAt: String
)

data class HomeBundle(
    val state: StudentState,
    val survey: SurveySummary,
    val notices: List<NoticeItem>
)
