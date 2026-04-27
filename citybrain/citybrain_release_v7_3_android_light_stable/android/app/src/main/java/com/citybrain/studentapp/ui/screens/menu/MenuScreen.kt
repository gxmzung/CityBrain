package com.citybrain.studentapp.ui.screens.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.citybrain.studentapp.ui.screens.home.HomeUiState
import com.citybrain.studentapp.ui.theme.AppBg
import com.citybrain.studentapp.ui.theme.BorderSoft
import com.citybrain.studentapp.ui.theme.BrandBlue
import com.citybrain.studentapp.ui.theme.BrandBlueSoft
import com.citybrain.studentapp.ui.theme.SurfaceWhite
import com.citybrain.studentapp.ui.theme.TextSecondary

private val weekMenus = listOf(
    "월" to "서가앤쿡 목살필라프 · 웨지감자튀김",
    "화" to "지리성st 고추짜장 · 짬뽕군만두",
    "수" to "직화 얼큰해장파스타 · 크로아상",
    "목" to "새우튀김냉우동 · 후리가케밥",
    "금" to "메뉴 준비중입니다."
)

@Composable
fun MenuScreen(uiState: HomeUiState) {
    val state = uiState.bundle?.state
    LazyColumn(Modifier.fillMaxSize().background(AppBg).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("금주 식단", color = BrandBlue, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Text("2026.04.27 ~ 2026.05.01 · 학생식당", color = TextSecondary)
        }
        if (state != null) {
            item { MenuCard("오늘의 일품식", state.menuName, "남은 수량 ${state.remainingCount}개 · 예상 대기 ${state.waitMinutes}분") }
        }
        items(weekMenus.size) { idx ->
            val (day, menu) = weekMenus[idx]
            MenuCard("${day}요일 중식 일품", menu, if (idx == 4) "준비중" else "11:00~13:30")
        }
        item { MenuCard("대체 선택지", "공학관 간이식당 · 글로벌관 푸드존 · 편의점 간편식", "시범운영 단계에서는 학생식당 중심으로 제공합니다.") }
    }
}

@Composable
private fun MenuCard(title: String, menu: String, meta: String) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(SurfaceWhite), border = BorderStroke(1.dp, BorderSoft)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(title, color = BrandBlue, fontWeight = FontWeight.ExtraBold)
            Text(menu, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 8.dp))
            Text(meta, color = TextSecondary, modifier = Modifier.padding(top = 5.dp))
        }
    }
}
