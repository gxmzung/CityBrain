package com.citybrain.studentapp.ui.screens.home

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.citybrain.studentapp.data.model.StudentState
import com.citybrain.studentapp.ui.theme.AccentOrange
import com.citybrain.studentapp.ui.theme.AccentOrangeSoft
import com.citybrain.studentapp.ui.theme.AppBg
import com.citybrain.studentapp.ui.theme.BorderSoft
import com.citybrain.studentapp.ui.theme.BrandBlue
import com.citybrain.studentapp.ui.theme.BrandBlueDark
import com.citybrain.studentapp.ui.theme.BrandBlueSoft
import com.citybrain.studentapp.ui.theme.DangerRed
import com.citybrain.studentapp.ui.theme.SuccessGreen
import com.citybrain.studentapp.ui.theme.SurfaceMuted
import com.citybrain.studentapp.ui.theme.SurfaceWhite
import com.citybrain.studentapp.ui.theme.TextPrimary
import com.citybrain.studentapp.ui.theme.TextSecondary
import com.citybrain.studentapp.ui.theme.WarningOrange

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onRefresh: () -> Unit
) {
    val bundle = uiState.bundle
    if (uiState.isLoading && bundle == null) {
        LoadingScreen()
        return
    }
    if (bundle == null) {
        ErrorScreen(onRefresh)
        return
    }

    val state = bundle.state
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(AppBg),
        contentPadding = PaddingValues(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { KoreanHeader() }
        item {
            Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MealTabs()
                if (bundle.notices.isNotEmpty()) {
                    NoticePill(bundle.notices.first().title)
                }
                TodayStatusCard(state)
                ActionButtons()
                SectionTitle("주변 선택지", "전체보기")
                PlaceCard("학생회관 식당", "4층 · 오늘 일품식 운영", "보통", "🏫")
                PlaceCard("공학관 간이식당", "공학관 1층 · 4,500원~", "원활", "🍜")
                PlaceCard("글로벌관 푸드존", "간편식 · 카페/베이커리", "여유", "☕")
                SurveyCard(
                    total = bundle.survey.totalResponses,
                    abandon = bundle.survey.abandonCount,
                    topInfo = bundle.survey.topInfo
                )
            }
        }
    }
}

@Composable
private fun KoreanHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(BrandBlue, BrandBlueDark)))
            .padding(start = 20.dp, end = 20.dp, top = 34.dp, bottom = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(16.dp)).background(Color.White),
                contentAlignment = Alignment.Center
            ) { Text("PCU", color = BrandBlue, fontWeight = FontWeight.ExtraBold) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("배재대학교", color = Color.White.copy(alpha = .86f), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Text("학생식당 실시간 안내", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            }
            Text("🔔", color = Color.White, style = MaterialTheme.typography.titleLarge)
        }
        Spacer(Modifier.height(14.dp))
        Text("오늘의 식사 상태를 확인하고 헛걸음을 줄이세요.", color = Color.White.copy(alpha = .82f))
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(999.dp)).background(Color.White.copy(alpha = .14f)).padding(vertical = 11.dp),
            contentAlignment = Alignment.Center
        ) { Text("📅 2026.04.27 ~ 2026.05.01", color = Color.White, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun MealTabs() {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(999.dp)).background(SurfaceMuted).padding(5.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        listOf("조식", "중식", "석식").forEach { label ->
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(999.dp)).background(if (label == "중식") SurfaceWhite else Color.Transparent).padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) { Text(label, color = if (label == "중식") BrandBlue else TextSecondary, fontWeight = FontWeight.ExtraBold) }
        }
    }
}

@Composable
private fun NoticePill(title: String) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(SurfaceWhite), border = BorderStroke(1.dp, BorderSoft)) {
        Row(Modifier.fillMaxWidth().padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("공지", color = AccentOrange, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.width(10.dp))
            Text(title, color = TextPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TodayStatusCard(state: StudentState) {
    val color = when (state.congestionLevel) { "원활" -> SuccessGreen; "혼잡" -> DangerRed; else -> WarningOrange }
    Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(SurfaceWhite), elevation = CardDefaults.cardElevation(3.dp), border = BorderStroke(1.dp, BorderSoft)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text("오늘의 일품식", color = BrandBlue, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold)
                    Text(state.menuName, color = TextPrimary, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                }
                Box(Modifier.size(62.dp).clip(RoundedCornerShape(20.dp)).background(AccentOrangeSoft), contentAlignment = Alignment.Center) { Text("🍱", style = MaterialTheme.typography.headlineMedium) }
            }
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(BrandBlueSoft).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("이용 판단", color = TextSecondary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Text(state.judgement, color = TextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                }
                StatusBadge(state.congestionLevel, color)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricTile("남은 수량", "${state.remainingCount}개", Modifier.weight(1f))
                MetricTile("예상 대기", "${state.waitMinutes}분", Modifier.weight(1f))
                MetricTile("소진 예상", state.selloutEta, Modifier.weight(1f))
            }
            LinearProgressIndicator(
                progress = { state.remainingRatio },
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(999.dp)),
                color = SuccessGreen,
                trackColor = SurfaceMuted
            )
            Text("현재 식당 인원 ${state.currentPeople}명 · ${state.updatedAt} 업데이트", color = TextSecondary, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun StatusBadge(text: String, color: Color) {
    Box(Modifier.clip(RoundedCornerShape(999.dp)).background(color.copy(alpha = .12f)).padding(horizontal = 12.dp, vertical = 8.dp)) {
        Text(text, color = color, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun MetricTile(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier.clip(RoundedCornerShape(18.dp)).background(SurfaceMuted).padding(12.dp)) {
        Text(label, color = TextSecondary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        Text(value, color = TextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun ActionButtons() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ActionButton("🍽️", "메뉴 보기", Modifier.weight(1f))
        ActionButton("📢", "공지 확인", Modifier.weight(1f))
        ActionButton("🙋", "혼잡도 제보", Modifier.weight(1f))
    }
}

@Composable
private fun ActionButton(icon: String, text: String, modifier: Modifier) {
    OutlinedButton(onClick = {}, modifier = modifier.height(70.dp), shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, BorderSoft)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(icon); Text(text, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.labelMedium) }
    }
}

@Composable
private fun SectionTitle(title: String, action: String? = null) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
        if (action != null) Text(action, color = BrandBlue, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun PlaceCard(name: String, desc: String, status: String, icon: String) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(SurfaceWhite), border = BorderStroke(1.dp, BorderSoft)) {
        Row(Modifier.fillMaxWidth().padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).clip(RoundedCornerShape(16.dp)).background(BrandBlueSoft), contentAlignment = Alignment.Center) { Text(icon) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) { Text(name, fontWeight = FontWeight.ExtraBold); Text(desc, color = TextSecondary, style = MaterialTheme.typography.labelMedium) }
            Text(status, color = BrandBlue, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun SurveyCard(total: Int, abandon: Int, topInfo: String) {
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(SurfaceWhite), border = BorderStroke(1.dp, BorderSoft)) {
        Column(Modifier.padding(16.dp)) {
            Text("학생 조사 기반", color = BrandBlue, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(8.dp))
            Text("응답자 ${total}명 · 식사 포기 경험 ${abandon}명", fontWeight = FontWeight.Bold)
            Text("가장 알고 싶은 정보: $topInfo", color = TextSecondary)
        }
    }
}

@Composable
private fun LoadingScreen() { Box(Modifier.fillMaxSize().background(AppBg), contentAlignment = Alignment.Center) { Text("식당 정보를 불러오는 중입니다…", color = TextSecondary) } }

@Composable
private fun ErrorScreen(onRefresh: () -> Unit) {
    Column(Modifier.fillMaxSize().background(AppBg).padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("서버 데이터를 불러오지 못했습니다.", color = TextSecondary)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onRefresh, colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)) { Text("다시 시도") }
    }
}
