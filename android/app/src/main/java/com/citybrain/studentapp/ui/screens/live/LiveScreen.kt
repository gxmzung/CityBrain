package com.citybrain.studentapp.ui.screens.live

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.citybrain.studentapp.ui.screens.common.CloneHeader
import com.citybrain.studentapp.ui.screens.common.ClonePage
import com.citybrain.studentapp.ui.screens.common.MiniInfoBox
import com.citybrain.studentapp.ui.screens.common.WhiteCard
import com.citybrain.studentapp.ui.screens.common.normalizeCongestion
import com.citybrain.studentapp.ui.screens.common.statusColor
import com.citybrain.studentapp.ui.screens.home.HomeUiState
import com.citybrain.studentapp.ui.theme.AccentOrange
import com.citybrain.studentapp.ui.theme.BrandBlue
import com.citybrain.studentapp.ui.theme.BrandBlueSoft
import com.citybrain.studentapp.ui.theme.DangerRed
import com.citybrain.studentapp.ui.theme.SuccessGreen
import com.citybrain.studentapp.ui.theme.TextPrimary
import com.citybrain.studentapp.ui.theme.TextSecondary
import com.citybrain.studentapp.ui.theme.TextTertiary
import com.citybrain.studentapp.ui.theme.WarningOrange

@Composable
fun LiveScreen(uiState: HomeUiState, onRefresh: () -> Unit) {
    val state = uiState.bundle?.state ?: return
    var report by remember { mutableStateOf(normalizeCongestion(state.congestionLevel)) }

    ClonePage {
        item { CloneHeader("실시간 현황", "실시간 현황과 제보", icon = "↻", onIconClick = onRefresh) }
        item {
            Box(Modifier.fillMaxWidth().height(126.dp).clip(RoundedCornerShape(14.dp)).background(Brush.linearGradient(listOf(Color(0xFF0B57B7), Color(0xFF00409A)))).padding(13.dp)) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("현재 혼잡도", color = Color.White.copy(.82f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(normalizeCongestion(state.congestionLevel), color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Box(Modifier.clip(CircleShape).background(Color.White.copy(.15f)).padding(8.dp)) { Text("☺", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        SmallWhite("현재 대기시간", "${state.waitMinutes}분")
                        SmallWhite("예상 품절", state.selloutEta)
                    }
                }
            }
        }
        item {
            WhiteCard {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("남은 수량", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("${(state.remainingRatio * 100).toInt()}%", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Text("${state.remainingCount} / ${state.totalCount} 식", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                LinearProgressIndicator(progress = { state.remainingRatio.coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(999.dp)), color = BrandBlue, trackColor = BrandBlueSoft)
            }
        }
        item {
            WhiteCard {
                Text("시간대별 혼잡도 예측", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("09시" to "☺", "10시" to "☺", "11시" to "☺", "12시" to "☹", "13시" to "☹", "14시" to "☹").forEachIndexed { idx, it ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(it.first, color = if (idx == 3) TextPrimary else TextTertiary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(it.second, color = if (it.second == "☺") SuccessGreen else DangerRed, fontSize = 18.sp)
                        }
                    }
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MiniInfoBox("남은 수량", "${state.remainingCount}식", BrandBlue, Modifier.weight(1f), "실시간")
                MiniInfoBox("대기시간", "${state.waitMinutes}분", AccentOrange, Modifier.weight(1f), "예상")
                MiniInfoBox("현재 인원", "${state.currentPeople}명", SuccessGreen, Modifier.weight(1f), "추정")
            }
        }
        item {
            WhiteCard {
                Text("실시간 혼잡도 제보", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("원활" to SuccessGreen, "보통" to WarningOrange, "혼잡" to DangerRed).forEach { (label, color) ->
                        SelectButton(label, report == label, color, Modifier.weight(1f)) { report = label }
                    }
                }
                Button(onClick = onRefresh, modifier = Modifier.fillMaxWidth().height(38.dp), shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)) {
                    Text("새로고침", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable private fun SmallWhite(title: String, value: String) { Column { Text(title, color = Color.White.copy(.78f), fontSize = 10.sp); Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold) } }
@Composable private fun SelectButton(label: String, active: Boolean, color: Color, modifier: Modifier, onClick: () -> Unit) { Box(modifier.height(36.dp).clip(RoundedCornerShape(11.dp)).background(if (active) color.copy(.15f) else Color.White).clickable { onClick() }, contentAlignment = Alignment.Center) { Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold) } }
