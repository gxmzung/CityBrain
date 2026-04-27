package com.citybrain.studentapp.ui.screens.participate

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.citybrain.studentapp.ui.screens.common.LoadingBlock
import com.citybrain.studentapp.ui.screens.common.PrimaryButton
import com.citybrain.studentapp.ui.screens.common.SectionCard
import com.citybrain.studentapp.ui.screens.common.SelectChip
import com.citybrain.studentapp.ui.screens.common.TopOfficialHeader
import com.citybrain.studentapp.ui.screens.common.congestionColor
import com.citybrain.studentapp.ui.screens.home.HomeUiState
import com.citybrain.studentapp.ui.theme.AccentOrange
import com.citybrain.studentapp.ui.theme.AppBg
import com.citybrain.studentapp.ui.theme.BrandBlue
import com.citybrain.studentapp.ui.theme.TextPrimary
import com.citybrain.studentapp.ui.theme.TextSecondary

@Composable
fun ParticipateScreen(uiState: HomeUiState) {
    if (uiState.bundle == null) { Box(Modifier.fillMaxSize().background(AppBg)) { LoadingBlock() }; return }
    var rating by remember { mutableStateOf(4) }
    var congestion by remember { mutableStateOf("원활") }
    var opinion by remember { mutableStateOf("") }
    var selectedMenu by remember { mutableStateOf(setOf("한식", "일품")) }

    LazyColumn(modifier = Modifier.fillMaxSize().background(AppBg), contentPadding = PaddingValues(bottom = 20.dp)) {
        item { TopOfficialHeader("학생 참여", "평가·설문·제보로 더 나은 식당 운영을 만듭니다.", "👥") }
        item {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SectionCard("학식 평가", "오늘 식당 이용은 어떠셨나요?") {
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        (1..5).forEach { index ->
                            Text(
                                if (index <= rating) "★" else "☆",
                                color = AccentOrange,
                                style = androidx.compose.material3.MaterialTheme.typography.displaySmall,
                                modifier = Modifier.clickable { rating = index }.padding(2.dp)
                            )
                        }
                    }
                    Text("${rating}.0 / 5.0", color = TextPrimary, fontWeight = FontWeight.ExtraBold)
                }
                SectionCard("메뉴 선호 조사", "선호하는 메뉴 유형을 선택해주세요. 복수 선택 가능") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        listOf("한식", "일품", "분식").forEach { item ->
                            SelectChip(item, item in selectedMenu, Modifier.weight(1f)) {
                                selectedMenu = if (item in selectedMenu) selectedMenu - item else selectedMenu + item
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        listOf("면류", "덮밥", "기타").forEach { item ->
                            SelectChip(item, item in selectedMenu, Modifier.weight(1f), BrandBlue) {
                                selectedMenu = if (item in selectedMenu) selectedMenu - item else selectedMenu + item
                            }
                        }
                    }
                }
                SectionCard("혼잡도 제보", "현재 식당 상황을 알려주세요.") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        listOf("원활", "보통", "혼잡").forEach { item -> SelectChip(item, congestion == item, Modifier.weight(1f), congestionColor(item)) { congestion = item } }
                    }
                }
                SectionCard("빠른 의견 보내기") {
                    OutlinedTextField(value = opinion, onValueChange = { if (it.length <= 200) opinion = it }, modifier = Modifier.fillMaxWidth(), minLines = 4, placeholder = { Text("건의사항을 입력해주세요. 최대 200자", color = TextSecondary) })
                    Text("${opinion.length} / 200", color = TextSecondary)
                    PrimaryButton("의견 보내기") { opinion = "" }
                }
            }
        }
    }
}
