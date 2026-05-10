package com.citybrain.studentapp.ui.screens.participate

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.citybrain.studentapp.ui.screens.common.CloneHeader
import com.citybrain.studentapp.ui.screens.common.ClonePage
import com.citybrain.studentapp.ui.screens.common.WhiteCard
import com.citybrain.studentapp.ui.screens.home.HomeUiState
import com.citybrain.studentapp.ui.theme.AccentOrange
import com.citybrain.studentapp.ui.theme.BorderSoft
import com.citybrain.studentapp.ui.theme.BrandBlue
import com.citybrain.studentapp.ui.theme.DangerRed
import com.citybrain.studentapp.ui.theme.SuccessGreen
import com.citybrain.studentapp.ui.theme.TextPrimary
import com.citybrain.studentapp.ui.theme.TextSecondary
import com.citybrain.studentapp.ui.theme.TextTertiary
import com.citybrain.studentapp.ui.theme.WarningOrange

@Composable
fun ParticipateScreen(uiState: HomeUiState) {
    var rating by remember { mutableStateOf(4) }
    val selectedMenus = remember { mutableStateListOf("한식", "일품") }
    var congestion by remember { mutableStateOf("원활") }
    var opinion by remember { mutableStateOf("") }
    ClonePage {
        item { CloneHeader("참여하기", "실시간 혼잡도 제보에 참여하세요.", icon = "♧") }
        item {
            WhiteCard {
                Text("학식 평가", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                Text("오늘 식당 이용은 어떠셨나요?", color = TextSecondary, fontSize = 11.sp)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) { (1..5).forEach { star -> Text(if (star <= rating) "★" else "☆", color = AccentOrange, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.clickable { rating = star }) } }
                Text("$rating.0 / 5.0", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        item {
            WhiteCard {
                Text("메뉴 선호 조사", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                Text("선호하는 메뉴 유형을 선택해주세요. 복수 선택 가능", color = TextSecondary, fontSize = 11.sp)
                listOf(listOf("한식", "일품", "분식"), listOf("면류", "덮밥", "기타")).forEach { row -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) { row.forEach { label -> ChoicePill(label, selectedMenus.contains(label), BrandBlue, Modifier.weight(1f)) { if (selectedMenus.contains(label)) selectedMenus.remove(label) else selectedMenus.add(label) } } } }
            }
        }
        item {
            WhiteCard {
                Text("혼잡도 제보", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                Text("현재 식당 상황을 알려주세요.", color = TextSecondary, fontSize = 11.sp)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChoicePill("원활", congestion == "원활", SuccessGreen, Modifier.weight(1f)) { congestion = "원활" }
                    ChoicePill("보통", congestion == "보통", WarningOrange, Modifier.weight(1f)) { congestion = "보통" }
                    ChoicePill("혼잡", congestion == "혼잡", DangerRed, Modifier.weight(1f)) { congestion = "혼잡" }
                }
            }
        }
        item {
            WhiteCard {
                Text("빠른 의견 보내기", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                OutlinedTextField(
                    value = opinion,
                    onValueChange = { if (it.length <= 100) opinion = it },
                    modifier = Modifier.fillMaxWidth().height(98.dp),
                    placeholder = { Text("의견을 입력해주세요. (최대 100자)", fontSize = 11.sp) },
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedIndicatorColor = BrandBlue, unfocusedIndicatorColor = BorderSoft)
                )
                Text("${opinion.length} / 100", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End, color = TextTertiary, fontSize = 10.sp)
                Button(onClick = {}, modifier = Modifier.fillMaxWidth().height(38.dp), shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)) { Text("의견 보내기", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold) }
            }
        }
        item {
            WhiteCard {
                Text("참여 통계", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                Text("96명 참여 · 평균 혼잡도: 원활", color = BrandBlue, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
private fun ChoicePill(label: String, active: Boolean, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Box(modifier.height(37.dp).clip(RoundedCornerShape(999.dp)).background(if (active) color else Color.White).border(BorderStroke(1.dp, if (active) color else BorderSoft), RoundedCornerShape(999.dp)).clickable { onClick() }, contentAlignment = Alignment.Center) {
        Text(label, color = if (active) Color.White else TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
    }
}
