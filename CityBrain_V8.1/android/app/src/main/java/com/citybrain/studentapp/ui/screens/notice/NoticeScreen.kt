package com.citybrain.studentapp.ui.screens.notice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.citybrain.studentapp.ui.screens.common.LoadingBlock
import com.citybrain.studentapp.ui.screens.common.NoticeCard
import com.citybrain.studentapp.ui.screens.common.SelectChip
import com.citybrain.studentapp.ui.screens.common.TopOfficialHeader
import com.citybrain.studentapp.ui.screens.home.HomeUiState
import com.citybrain.studentapp.ui.theme.AppBg
import com.citybrain.studentapp.ui.theme.BrandBlue

@Composable
fun NoticeScreen(uiState: HomeUiState) {
    val notices = uiState.bundle?.notices
    if (notices == null) { Box(Modifier.fillMaxSize().background(AppBg)) { LoadingBlock() }; return }
    var selected by remember { mutableStateOf("전체") }
    LazyColumn(modifier = Modifier.fillMaxSize().background(AppBg), contentPadding = PaddingValues(bottom = 20.dp)) {
        item { TopOfficialHeader("운영 공지", "시범운영, 혼잡 시간대, 식단 변경 안내를 확인하세요.", "📢") }
        item {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    listOf("전체", "운영", "식단", "기타").forEach { label -> SelectChip(label, selected == label, tint = BrandBlue) { selected = label } }
                }
                NoticeCard("시범운영 안내", "학생식당 실시간 안내 서비스가 시범운영을 시작했습니다.", "2026.05.08", "운영")
                NoticeCard("혼잡 시간대 안내", "중식 12:00~12:30 사이 혼잡이 예상됩니다. 이용 시간 분산에 협조 부탁드립니다.", "2026.05.07", "운영")
                NoticeCard("식단 변경 안내", "운영 사정에 따라 일부 메뉴가 변경될 수 있습니다.", "2026.05.06", "식단")
                notices.take(2).forEach { NoticeCard(it.title, it.body, it.createdAt.take(10), "공지") }
            }
        }
    }
}
