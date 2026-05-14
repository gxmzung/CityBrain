package com.citybrain.studentapp.ui.screens.my

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.citybrain.studentapp.ui.theme.AppBg
import com.citybrain.studentapp.ui.theme.BorderSoft
import com.citybrain.studentapp.ui.theme.BrandBlue
import com.citybrain.studentapp.ui.theme.SurfaceWhite
import com.citybrain.studentapp.ui.theme.TextSecondary

@Composable
fun MyScreen() {
    Column(Modifier.fillMaxSize().background(AppBg).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("MY", color = BrandBlue, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
        Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(SurfaceWhite), border = BorderStroke(1.dp, BorderSoft)) {
            Column(Modifier.fillMaxWidth().padding(18.dp)) {
                Text("시범운영 준비 중", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Text("정식 운영 시 학생 인증을 통해 선호 메뉴, 알림 설정, 이용 기록을 제공할 예정입니다.", color = TextSecondary, modifier = Modifier.padding(top = 8.dp))
            }
        }
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(SurfaceWhite), border = BorderStroke(1.dp, BorderSoft)) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text("개인정보 처리방침", fontWeight = FontWeight.ExtraBold)
                Text("현재 MVP는 개인 식별 정보를 필수 수집하지 않습니다.", color = TextSecondary, modifier = Modifier.padding(top = 6.dp))
            }
        }
    }
}
