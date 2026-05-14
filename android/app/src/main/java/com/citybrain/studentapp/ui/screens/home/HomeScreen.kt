package com.citybrain.studentapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.citybrain.studentapp.data.model.StudentState
import com.citybrain.studentapp.ui.screens.common.ActionBox
import com.citybrain.studentapp.ui.screens.common.BlueBadge
import com.citybrain.studentapp.ui.screens.common.CloneHeader
import com.citybrain.studentapp.ui.screens.common.ClonePage
import com.citybrain.studentapp.ui.screens.common.FoodPlate
import com.citybrain.studentapp.ui.screens.common.MealSegment
import com.citybrain.studentapp.ui.screens.common.MiniInfoBox
import com.citybrain.studentapp.ui.screens.common.displayMenuName
import com.citybrain.studentapp.ui.screens.common.normalizeCongestion
import com.citybrain.studentapp.ui.screens.common.statusColor
import com.citybrain.studentapp.ui.theme.AccentOrange
import com.citybrain.studentapp.ui.theme.BrandBlue
import com.citybrain.studentapp.ui.theme.BrandBlueDark
import com.citybrain.studentapp.ui.theme.SuccessGreen
import com.citybrain.studentapp.ui.theme.TextPrimary
import com.citybrain.studentapp.ui.theme.TextSecondary
import com.citybrain.studentapp.ui.theme.TextTertiary

@Composable
fun HomeScreen(uiState: HomeUiState, onRefresh: () -> Unit, openTab: (String) -> Unit) {
    val bundle = uiState.bundle
    if (bundle == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(if (uiState.isLoading) "학생식당 정보를 불러오는 중..." else "서버 데이터를 불러오지 못했습니다.", color = TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        return
    }
    val state = bundle.state
    var selectedMeal by remember { mutableStateOf(currentMealPeriod()) }

    ClonePage {
        item { CloneHeader("학생식당 실시간 안내", "오늘의 식사 상태를 확인하고\n헛걸음을 줄이세요.", icon = "♧", onIconClick = onRefresh) }
        item { MealSegment(selectedMeal) { selectedMeal = it } }
        item { HomeHero(state = state, selectedMeal = selectedMeal) }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MiniInfoBox("남은 수량", "${state.remainingCount}식", BrandBlue, Modifier.weight(1f), "${(state.remainingRatio * 100).toInt()}%")
                MiniInfoBox("혼잡도", normalizeCongestion(state.congestionLevel), statusColor(state.congestionLevel), Modifier.weight(1f), "현재 상태")
                MiniInfoBox("예상 대기", "${state.waitMinutes}분", AccentOrange, Modifier.weight(1f), "품절 ${state.selloutEta}")
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionBox("▤", "메뉴 보기", "오늘/주간 메뉴", AccentOrange, Modifier.weight(1f)) { openTab("menu") }
                ActionBox("▣", "공지 확인", "운영 공지 보기", BrandBlue, Modifier.weight(1f)) { openTab("notice") }
                ActionBox("●", "혼잡도 제보", "실시간 제보", SuccessGreen, Modifier.weight(1f)) { openTab("participate") }
            }
        }
        item { HomeSummary() }
    }
}

@Composable
private fun HomeHero(state: StudentState, selectedMeal: String) {
    val level = normalizeCongestion(state.congestionLevel)
    Card(
        modifier = Modifier.fillMaxWidth().height(223.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF0B57B7), Color(0xFF00409A)))).padding(11.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(Modifier.clip(RoundedCornerShape(999.dp)).background(Color.White.copy(alpha=.16f)).border(1.dp, Color.White.copy(alpha=.26f), RoundedCornerShape(999.dp)).padding(horizontal = 10.dp, vertical = 5.dp)) {
                Text("오늘의 일품식", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
            }
            Text(displayMenuName(state.menuName), color = Color.White, fontSize = 18.sp, lineHeight = 21.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("$selectedMeal 일품 · 학생식당", color = Color.White.copy(alpha=.86f), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            Row(Modifier.fillMaxWidth().weight(1f), verticalAlignment = Alignment.CenterVertically) {
                FoodPlate(Modifier.size(74.dp))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("남은 수량", color = Color.White.copy(alpha=.80f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("${state.remainingCount}", color = Color.White, fontSize = 25.sp, lineHeight = 27.sp, fontWeight = FontWeight.ExtraBold)
                        Text(" / ${state.totalCount}식", color = Color.White.copy(alpha=.92f), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 3.dp))
                    }
                    Box(Modifier.clip(RoundedCornerShape(999.dp)).background(statusColor(level)).padding(horizontal = 12.dp, vertical = 5.dp)) { Text(level, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold) }
                    Text("예상 대기시간 ${state.waitMinutes}분", color = Color.White.copy(alpha=.90f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("품절 예상 ${state.selloutEta}", color = Color.White.copy(alpha=.90f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            LinearProgressIndicator(progress = { state.remainingRatio.coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(999.dp)), color = Color.White, trackColor = Color.White.copy(alpha=.22f))
            Text(recommendationText(state), color = Color.White, fontSize = 10.sp, lineHeight = 13.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun HomeSummary() {
    com.citybrain.studentapp.ui.screens.common.WhiteCard {
        Text("오늘 이용 요약", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryMini("평균 평점", "4.2", AccentOrange, Modifier.weight(1f))
            SummaryMini("참여 인원", "96명", BrandBlue, Modifier.weight(1f))
            SummaryMini("선호 메뉴", "한식 · 일품", SuccessGreen, Modifier.weight(1f))
        }
    }
}

@Composable
private fun SummaryMini(title: String, value: String, color: Color, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(10.dp)).background(Color(0xFFF5F7FB)).padding(horizontal = 7.dp, vertical = 6.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(title, color = TextTertiary, fontSize = 8.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(value, color = color, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

private fun currentMealPeriod(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when (hour) { in 0..10 -> "조식"; in 11..15 -> "중식"; else -> "석식" }
}

private fun recommendationText(state: StudentState): String {
    val level = normalizeCongestion(state.congestionLevel)
    return when {
        state.remainingCount <= 0 -> "오늘 일품식이 소진됐어요. 다른 메뉴를 확인하세요."
        state.remainingCount <= 10 -> "곧 소진될 수 있어요. 이동 전에 확인하세요."
        level == "혼잡" -> "지금은 대기 시간이 길어요. 시간을 조정해보세요."
        level == "원활" -> "지금 방문하시면 여유롭게 식사하실 수 있어요!"
        else -> "지금 가도 괜찮아요. 대기시간을 확인하고 이동하세요."
    }
}
