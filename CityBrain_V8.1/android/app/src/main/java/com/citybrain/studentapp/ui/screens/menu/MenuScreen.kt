package com.citybrain.studentapp.ui.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.citybrain.studentapp.ui.screens.common.InfoRow
import com.citybrain.studentapp.ui.screens.common.LoadingBlock
import com.citybrain.studentapp.ui.screens.common.MealPeriodTabs
import com.citybrain.studentapp.ui.screens.common.PrimaryButton
import com.citybrain.studentapp.ui.screens.common.SectionCard
import com.citybrain.studentapp.ui.screens.common.SmallBadge
import com.citybrain.studentapp.ui.screens.common.TopOfficialHeader
import com.citybrain.studentapp.ui.screens.home.HomeUiState
import com.citybrain.studentapp.ui.theme.AccentOrange
import com.citybrain.studentapp.ui.theme.AppBg
import com.citybrain.studentapp.ui.theme.BrandBlue
import com.citybrain.studentapp.ui.theme.TextPrimary
import com.citybrain.studentapp.ui.theme.TextSecondary

@Composable
fun MenuScreen(uiState: HomeUiState) {
    val state = uiState.bundle?.state
    if (state == null) { Box(Modifier.fillMaxSize().background(AppBg)) { LoadingBlock() }; return }
    val weekly = listOf(
        "월 5.12  제육볶음 정식 / 돈까스 카레",
        "화 5.13  닭갈비 덮밥 / 김치찌개",
        "수 5.14  불고기 정식 / 비빔밥",
        "목 5.15  함박스테이크 / 잔치국수",
        "금 5.16  치킨마요 덮밥 / 부대찌개"
    )
    LazyColumn(modifier = Modifier.fillMaxSize().background(AppBg), contentPadding = PaddingValues(bottom = 20.dp)) {
        item { TopOfficialHeader("오늘 메뉴", "오늘 메뉴와 주간 식단을 한 번에 확인하세요.", "🍽️") }
        item {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                MealPeriodTabs(selected = "중식")
                SectionCard("오늘의 일품식") {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("🍱", style = androidx.compose.material3.MaterialTheme.typography.displaySmall)
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            SmallBadge("오늘의 대표 메뉴")
                            Text(state.menuName, color = TextPrimary, style = androidx.compose.material3.MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                            Text("가격 5,500원 · 쌀밥, 국, 김치 포함", color = TextSecondary)
                        }
                    }
                    InfoRow("운영 시간", "11:30 ~ 14:00")
                    InfoRow("이용 가격", "일반 5,500원 / 교직원 6,000원")
                    InfoRow("식당 위치", "배재관 1층 학생식당")
                }
                SectionCard("주간 메뉴 미리보기", "식단은 운영 사정에 따라 변경될 수 있습니다.") {
                    weekly.forEach { line -> InfoRow(line.substring(0, 6), line.substring(7), BrandBlue) }
                    PrimaryButton("전체 식단표 보기") { }
                }
            }
        }
    }
}
