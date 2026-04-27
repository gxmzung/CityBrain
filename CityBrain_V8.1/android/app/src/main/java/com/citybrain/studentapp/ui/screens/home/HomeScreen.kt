package com.citybrain.studentapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.citybrain.studentapp.ui.screens.common.LoadingBlock
import com.citybrain.studentapp.ui.screens.common.MealPeriodTabs
import com.citybrain.studentapp.ui.screens.common.MetricTile
import com.citybrain.studentapp.ui.screens.common.QuickActionButton
import com.citybrain.studentapp.ui.screens.common.SectionCard
import com.citybrain.studentapp.ui.screens.common.SectionHeader
import com.citybrain.studentapp.ui.screens.common.StatusHeroCard
import com.citybrain.studentapp.ui.screens.common.TopOfficialHeader
import com.citybrain.studentapp.ui.screens.common.congestionColor
import com.citybrain.studentapp.ui.theme.AccentOrange
import com.citybrain.studentapp.ui.theme.AppBg
import com.citybrain.studentapp.ui.theme.BrandBlue
import com.citybrain.studentapp.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onRefresh: () -> Unit,
    openTab: (String) -> Unit
) {
    val bundle = uiState.bundle
    if (uiState.isLoading && bundle == null) {
        Box(Modifier.fillMaxSize().background(AppBg), contentAlignment = Alignment.Center) { LoadingBlock() }
        return
    }
    if (bundle == null) {
        ErrorScreen(onRefresh)
        return
    }
    val state = bundle.state

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(AppBg),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        item {
            TopOfficialHeader(
                title = "학생식당 실시간 안내",
                subtitle = "오늘의 식사 상태를 확인하고 헛걸음을 줄이세요.",
                action = "🔔"
            )
        }
        item {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                MealPeriodTabs(selected = "중식")
                StatusHeroCard(state)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    MetricTile("남은 수량", "${state.remainingCount}식", Modifier.weight(1f), BrandBlue, "${(state.remainingRatio * 100).toInt()}%")
                    MetricTile("혼잡도", state.congestionLevel, Modifier.weight(1f), congestionColor(state.congestionLevel), "현재 상태")
                    MetricTile("예상 대기", "${state.waitMinutes}분", Modifier.weight(1f), AccentOrange, "품절 ${state.selloutEta}")
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    QuickActionButton("🍽️", "메뉴 보기", "오늘/주간 메뉴", Modifier.weight(1f)) { openTab("menu") }
                    QuickActionButton("📢", "공지 확인", "운영 공지 보기", Modifier.weight(1f)) { openTab("notice") }
                    QuickActionButton("🙋", "혼잡도 제보", "실시간 제보", Modifier.weight(1f)) { openTab("participate") }
                }

                SectionHeader("학생 참여 요약", "참여하기") { openTab("participate") }
                SectionCard(title = "평가·선호·제보 기반 운영 개선", subtitle = "학생 의견은 식단과 혼잡도 예측 품질 개선에 활용됩니다.") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        MetricTile("평균 평점", "4.2", Modifier.weight(1f), AccentOrange)
                        MetricTile("참여 인원", "${bundle.survey.totalResponses}명", Modifier.weight(1f), BrandBlue)
                        MetricTile("관심 정보", bundle.survey.topInfo, Modifier.weight(1f), BrandBlue)
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorScreen(onRefresh: () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(AppBg).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("서버 데이터를 불러오지 못했습니다.", color = TextSecondary)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onRefresh, colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)) {
            Text("다시 시도", color = Color.White, fontWeight = FontWeight.ExtraBold)
        }
    }
}
