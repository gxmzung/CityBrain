package com.citybrain.studentapp.ui.screens.live

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.citybrain.studentapp.ui.screens.home.HomeUiState
import com.citybrain.studentapp.ui.theme.AppBg
import com.citybrain.studentapp.ui.theme.BorderSoft
import com.citybrain.studentapp.ui.theme.BrandBlue
import com.citybrain.studentapp.ui.theme.BrandBlueSoft
import com.citybrain.studentapp.ui.theme.DangerRed
import com.citybrain.studentapp.ui.theme.SuccessGreen
import com.citybrain.studentapp.ui.theme.SurfaceMuted
import com.citybrain.studentapp.ui.theme.SurfaceWhite
import com.citybrain.studentapp.ui.theme.TextPrimary
import com.citybrain.studentapp.ui.theme.TextSecondary
import com.citybrain.studentapp.ui.theme.WarningOrange

@Composable
fun LiveScreen(uiState: HomeUiState) {
    val bundle = uiState.bundle
    if (bundle == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("실시간 정보를 불러오는 중입니다.") }
        return
    }
    val state = bundle.state
    val color = when (state.congestionLevel) { "원활" -> SuccessGreen; "혼잡" -> DangerRed; else -> WarningOrange }
    Column(Modifier.fillMaxSize().background(AppBg).padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("실시간 상태", color = BrandBlue, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
        Text("잔여 수량, 혼잡도, 대기시간을 자세히 확인합니다.", color = TextSecondary)

        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(SurfaceWhite), border = BorderStroke(1.dp, BorderSoft)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(state.menuName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailBox("남은 수량", "${state.remainingCount}개", Modifier.weight(1f))
                    DetailBox("판매량", "${state.soldCount}개", Modifier.weight(1f))
                }
                LinearProgressIndicator(progress = { state.remainingRatio }, modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(999.dp)), color = SuccessGreen, trackColor = SurfaceMuted)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailBox("혼잡도", state.congestionLevel, Modifier.weight(1f), color)
                    DetailBox("대기시간", "${state.waitMinutes}분", Modifier.weight(1f))
                    DetailBox("소진 예상", state.selloutEta, Modifier.weight(1f))
                }
            }
        }

        Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(BrandBlueSoft), border = BorderStroke(1.dp, BorderSoft)) {
            Column(Modifier.padding(16.dp)) {
                Text("이용 판단", color = BrandBlue, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(8.dp))
                Text(state.judgement, color = TextPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Text("현재 식당 인원 ${state.currentPeople}명 · ${state.updatedAt}", color = TextSecondary)
            }
        }

        Text("학생 반응", fontWeight = FontWeight.ExtraBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("좋아요", "괜찮아요", "보통이에요", "아쉬워요").forEach { label ->
                Box(Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(SurfaceWhite).padding(vertical = 14.dp), contentAlignment = Alignment.Center) {
                    Text(label, color = BrandBlue, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun DetailBox(label: String, value: String, modifier: Modifier, valueColor: Color = TextPrimary) {
    Column(modifier.clip(RoundedCornerShape(18.dp)).background(SurfaceMuted).padding(13.dp)) {
        Text(label, color = TextSecondary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        Text(value, color = valueColor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
    }
}
