package com.citybrain.studentapp.ui.screens.notice

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.citybrain.studentapp.ui.screens.home.HomeUiState
import com.citybrain.studentapp.ui.theme.AppBg
import com.citybrain.studentapp.ui.theme.BorderSoft
import com.citybrain.studentapp.ui.theme.BrandBlue
import com.citybrain.studentapp.ui.theme.SurfaceWhite
import com.citybrain.studentapp.ui.theme.TextSecondary

@Composable
fun NoticeScreen(uiState: HomeUiState) {
    val notices = uiState.bundle?.notices.orEmpty()
    LazyColumn(Modifier.fillMaxSize().background(AppBg).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("운영 공지", color = BrandBlue, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Text("학생식당 운영 안내를 확인합니다.", color = TextSecondary)
        }
        if (notices.isEmpty()) {
            item { NoticeCard("등록된 공지가 없습니다.", "새로운 운영 안내가 생기면 이곳에 표시됩니다.") }
        } else {
            items(notices.size) { idx -> NoticeCard(notices[idx].title, notices[idx].body) }
        }
    }
}

@Composable
private fun NoticeCard(title: String, body: String) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(SurfaceWhite), border = BorderStroke(1.dp, BorderSoft)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(title, fontWeight = FontWeight.ExtraBold)
            Text(body, color = TextSecondary, modifier = Modifier.padding(top = 7.dp))
        }
    }
}
