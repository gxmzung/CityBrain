package com.citybrain.studentapp.ui.screens.notice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.citybrain.studentapp.ui.screens.common.CloneHeader
import com.citybrain.studentapp.ui.screens.common.ClonePage
import com.citybrain.studentapp.ui.screens.common.FilterChip
import com.citybrain.studentapp.ui.screens.common.WhiteCard
import com.citybrain.studentapp.ui.screens.home.HomeUiState
import com.citybrain.studentapp.ui.theme.BrandBlue
import com.citybrain.studentapp.ui.theme.BrandBlueSoft
import com.citybrain.studentapp.ui.theme.DangerRed
import com.citybrain.studentapp.ui.theme.SuccessGreen
import com.citybrain.studentapp.ui.theme.TextPrimary
import com.citybrain.studentapp.ui.theme.TextSecondary
import com.citybrain.studentapp.ui.theme.TextTertiary

private data class NoticeUi(val type: String, val date: String, val title: String, val body: String)

@Composable
fun NoticeScreen(uiState: HomeUiState) {
    var filter by remember { mutableStateOf("전체") }
    val notices = listOf(
        NoticeUi("운영", "2026.05.08", "시범운영 안내", "학생식당 실시간 안내 서비스가 시범운영을 시작했습니다."),
        NoticeUi("운영", "2026.05.07", "혼잡 시간대 안내", "중식 12:00~12:30 사이 혼잡이 예상됩니다."),
        NoticeUi("식단", "2026.05.06", "식단 변경 안내", "운영 사정에 따라 일부 메뉴가 변경될 수 있습니다."),
        NoticeUi("기타", "2026.04.27", "결제 관련 안내", "카드 결제만 가능하며, 현금은 받지 않습니다."),
        NoticeUi("공지", "2026.04.27", "혼잡 시간대 참고", "12:10~12:35 사이에는 대기시간이 길어질 수 있습니다.")
    )
    ClonePage {
        item { CloneHeader("공지사항", "학생식당 공지사항을 확인하세요.", icon = "♧") }
        item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("전체", "운영", "식단", "기타").forEach { FilterChip(it, filter == it, Modifier.weight(1f)) { filter = it } } } }
        notices.filter { filter == "전체" || it.type == filter }.forEach { n -> item { NoticeCard(n) } }
    }
}

@Composable
private fun NoticeCard(n: NoticeUi) {
    WhiteCard {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(46.dp).clip(RoundedCornerShape(13.dp)).background(BrandBlueSoft), contentAlignment = Alignment.Center) { Text("📣", fontSize = 18.sp) }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.clip(RoundedCornerShape(999.dp)).background(if (n.type == "식단") SuccessGreen.copy(.12f) else if (n.type == "운영") BrandBlueSoft else DangerRed.copy(.10f)).padding(horizontal = 9.dp, vertical = 4.dp)) {
                        Text(n.type, color = if (n.type == "식단") SuccessGreen else if (n.type == "운영") BrandBlue else DangerRed, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Text(n.date, color = TextTertiary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Text(n.title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(n.body, color = TextSecondary, fontSize = 11.sp, lineHeight = 15.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Text("›", color = TextTertiary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
    }
}
