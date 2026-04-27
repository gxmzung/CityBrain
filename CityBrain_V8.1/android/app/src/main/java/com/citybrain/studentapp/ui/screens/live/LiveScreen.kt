package com.citybrain.studentapp.ui.screens.live

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.citybrain.studentapp.ui.screens.common.InfoRow
import com.citybrain.studentapp.ui.screens.common.LoadingBlock
import com.citybrain.studentapp.ui.screens.common.MetricTile
import com.citybrain.studentapp.ui.screens.common.PrimaryButton
import com.citybrain.studentapp.ui.screens.common.SectionCard
import com.citybrain.studentapp.ui.screens.common.SelectChip
import com.citybrain.studentapp.ui.screens.common.StatusBadge
import com.citybrain.studentapp.ui.screens.common.TopOfficialHeader
import com.citybrain.studentapp.ui.screens.common.congestionColor
import com.citybrain.studentapp.ui.theme.AppBg
import com.citybrain.studentapp.ui.theme.BorderSoft
import com.citybrain.studentapp.ui.theme.BrandBlue
import com.citybrain.studentapp.ui.theme.SuccessGreen
import com.citybrain.studentapp.ui.theme.SurfaceMuted
import com.citybrain.studentapp.ui.theme.TextPrimary
import com.citybrain.studentapp.ui.theme.TextSecondary
import com.citybrain.studentapp.ui.screens.home.HomeUiState

@Composable
fun LiveScreen(uiState: HomeUiState, onRefresh: () -> Unit) {
    val state = uiState.bundle?.state
    if (state == null) { Box(Modifier.fillMaxSize().background(AppBg)) { LoadingBlock() }; return }
    var report by remember { mutableStateOf(state.congestionLevel) }
    LazyColumn(modifier = Modifier.fillMaxSize().background(AppBg), contentPadding = PaddingValues(bottom = 20.dp)) {
        item { TopOfficialHeader("실시간 현황", "남은 수량·혼잡도·대기시간을 실시간으로 확인하세요.", "↻", onRefresh) }
        item {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SectionCard("남은 수량", "마지막 업데이트 ${state.updatedAt}") {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${state.remainingCount} / ${state.totalCount}식", color = TextPrimary, fontWeight = FontWeight.ExtraBold)
                        Text("${(state.remainingRatio * 100).toInt()}%", color = TextSecondary)
                    }
                    LinearProgressIndicator(
                        progress = { state.remainingRatio },
                        modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(999.dp)),
                        color = BrandBlue,
                        trackColor = SurfaceMuted
                    )
                }
                SectionCard("현재 혼잡도") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        MetricTile("혼잡도", state.congestionLevel, Modifier.weight(1f), congestionColor(state.congestionLevel))
                        MetricTile("예상 대기시간", "${state.waitMinutes}분", Modifier.weight(1f), BrandBlue)
                    }
                    InfoRow("품절 예상 시간", state.selloutEta)
                    InfoRow("현재 식당 인원", "약 ${state.currentPeople}명")
                }
                SectionCard("실시간 혼잡도 제보", "여러분의 제보가 정확도를 높여요.") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        SelectChip("원활해요", report == "원활", Modifier.weight(1f), SuccessGreen) { report = "원활" }
                        SelectChip("보통이에요", report == "보통", Modifier.weight(1f)) { report = "보통" }
                        SelectChip("혼잡해요", report == "혼잡", Modifier.weight(1f), congestionColor("혼잡")) { report = "혼잡" }
                    }
                    InfoRow("선택한 제보", report, congestionColor(report))
                }
                PrimaryButton("새로고침") { onRefresh() }
            }
        }
    }
}
