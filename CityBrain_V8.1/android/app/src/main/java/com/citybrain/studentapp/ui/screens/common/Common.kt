package com.citybrain.studentapp.ui.screens.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.citybrain.studentapp.data.model.StudentState
import com.citybrain.studentapp.ui.theme.AccentOrange
import com.citybrain.studentapp.ui.theme.AccentOrangeDark
import com.citybrain.studentapp.ui.theme.AccentOrangeSoft
import com.citybrain.studentapp.ui.theme.AppBg
import com.citybrain.studentapp.ui.theme.BorderSoft
import com.citybrain.studentapp.ui.theme.BrandBlue
import com.citybrain.studentapp.ui.theme.BrandBlueDark
import com.citybrain.studentapp.ui.theme.BrandBlueSoft
import com.citybrain.studentapp.ui.theme.DangerRed
import com.citybrain.studentapp.ui.theme.SuccessGreen
import com.citybrain.studentapp.ui.theme.SurfaceMuted
import com.citybrain.studentapp.ui.theme.SurfaceWhite
import com.citybrain.studentapp.ui.theme.TextPrimary
import com.citybrain.studentapp.ui.theme.TextSecondary
import com.citybrain.studentapp.ui.theme.TextTertiary
import com.citybrain.studentapp.ui.theme.WarningOrange

@Composable
fun TopOfficialHeader(
    title: String,
    subtitle: String,
    action: String = "🔔",
    onAction: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(BrandBlueDark, BrandBlue)))
            .padding(start = 22.dp, end = 22.dp, top = 30.dp, bottom = 22.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(Color.White.copy(alpha = 0.16f), RoundedCornerShape(16.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.24f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) { Text("배", color = Color.White, fontWeight = FontWeight.ExtraBold) }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("배재대학교", color = Color.White.copy(alpha = 0.86f), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Text(title, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.16f))
                    .clickable { onAction() },
                contentAlignment = Alignment.Center
            ) { Text(action) }
        }
        Spacer(Modifier.height(12.dp))
        Text(subtitle, color = Color.White.copy(alpha = 0.78f), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun MealPeriodTabs(
    selected: String = "중식",
    caption: String = "현재 시간 기준 자동 전환 · 관리자 수동 변경 가능",
    onSelect: (String) -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceMuted, RoundedCornerShape(999.dp))
                .padding(5.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("조식", "중식", "석식").forEach { label ->
                val active = label == selected
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (active) SurfaceWhite else Color.Transparent)
                        .clickable { onSelect(label) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, color = if (active) BrandBlue else TextSecondary, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
        Text(caption, color = TextTertiary, style = MaterialTheme.typography.labelSmall, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    }
}

@Composable
fun StatusHeroCard(state: StudentState) {
    val color = congestionColor(state.congestionLevel)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Brush.linearGradient(listOf(BrandBlueDark, BrandBlue)))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        SmallBadge("오늘의 일품식", dark = true)
                        Text(state.menuName, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text("중식 일품 · 학생식당", color = Color.White.copy(alpha = 0.76f), style = MaterialTheme.typography.bodyMedium)
                    }
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(AccentOrangeSoft),
                        contentAlignment = Alignment.Center
                    ) { Text("🍱", style = MaterialTheme.typography.displaySmall) }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("남은 수량", color = Color.White.copy(alpha = 0.76f), style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.width(8.dp))
                    Text("${state.remainingCount} / ${state.totalCount}식", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.weight(1f))
                    StatusBadge(state.congestionLevel, compact = true)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    HeroMiniMetric("예상 대기", "${state.waitMinutes}분", Modifier.weight(1f))
                    HeroMiniMetric("품절 예상", state.selloutEta, Modifier.weight(1f))
                    HeroMiniMetric("현재 인원", "약 ${state.currentPeople}명", Modifier.weight(1f))
                }
                LinearProgressIndicator(
                    progress = { state.remainingRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(9.dp)
                        .clip(RoundedCornerShape(999.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.18f)
                )
                Text(recommendationText(state), color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun HeroMiniMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(label, color = Color.White.copy(alpha = 0.72f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        Text(value, color = Color.White, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun MetricTile(label: String, value: String, modifier: Modifier = Modifier, color: Color = BrandBlue, sub: String? = null) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, BorderSoft)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(label, color = TextSecondary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text(value, color = color, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (sub != null) Text(sub, color = TextTertiary, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun QuickActionButton(symbol: String, title: String, subtitle: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, BorderSoft)
    ) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(symbol, style = MaterialTheme.typography.titleMedium)
            Text(title, color = BrandBlue, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
            Text(subtitle, color = TextTertiary, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun SectionHeader(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, color = TextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
        if (action != null) TextButton(onClick = { onAction?.invoke() }) { Text(action, color = BrandBlue, fontWeight = FontWeight.Bold) }
    }
}

@Composable
fun SectionCard(title: String, subtitle: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), border = BorderStroke(1.dp, BorderSoft)) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, color = TextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                if (subtitle != null) Text(subtitle, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            }
            content()
        }
    }
}

@Composable
fun StatusBadge(level: String, compact: Boolean = false) {
    val color = congestionColor(level)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color.copy(alpha = if (compact) 0.22f else 0.12f))
            .border(1.dp, color.copy(alpha = 0.24f), RoundedCornerShape(999.dp))
            .padding(horizontal = if (compact) 10.dp else 12.dp, vertical = if (compact) 6.dp else 7.dp)
    ) { Text(level, color = if (compact) Color.White else color, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold) }
}

@Composable
fun SmallBadge(text: String, dark: Boolean = false) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (dark) Color.White.copy(alpha = 0.16f) else BrandBlueSoft)
            .border(1.dp, if (dark) Color.White.copy(alpha = 0.22f) else BrandBlue.copy(alpha = 0.12f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) { Text(text, color = if (dark) Color.White else BrandBlue, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold) }
}

@Composable
fun SelectChip(text: String, selected: Boolean, modifier: Modifier = Modifier, tint: Color = BrandBlue, onClick: () -> Unit = {}) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) tint else SurfaceWhite)
            .border(1.dp, if (selected) tint else BorderSoft, RoundedCornerShape(999.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) { Text(text, color = if (selected) Color.White else TextSecondary, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.labelMedium) }
}

@Composable
fun InfoRow(label: String, value: String, valueColor: Color = TextPrimary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceMuted)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        Text(value, color = valueColor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
    }
}

@Composable
fun NoticeCard(title: String, body: String, date: String, category: String = "운영") {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), border = BorderStroke(1.dp, BorderSoft)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(BrandBlueSoft), contentAlignment = Alignment.Center) { Text("📢") }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallBadge(category)
                    Text(date, color = TextTertiary, style = MaterialTheme.typography.labelSmall)
                }
                Text(title, color = TextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold)
                Text(body, color = TextSecondary, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Text("›", color = TextTertiary, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
fun LoadingBlock() {
    Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) { Text("불러오는 중...", color = TextSecondary) }
}

@Composable
fun PrimaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Button(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)) {
        Text(text, color = Color.White, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun SecondaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    OutlinedButton(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, BrandBlue.copy(alpha = 0.38f))) {
        Text(text, color = BrandBlue, fontWeight = FontWeight.ExtraBold)
    }
}

fun congestionColor(level: String): Color = when (level) {
    "원활" -> SuccessGreen
    "혼잡" -> DangerRed
    "보통" -> WarningOrange
    else -> BrandBlue
}

fun recommendationText(state: StudentState): String = when {
    state.remainingCount <= 0 -> "오늘 일품식이 소진됐어요. 메뉴 탭에서 다른 식단을 확인하세요."
    state.remainingCount <= 10 -> "곧 소진될 수 있어요. 이동 전 다시 확인하세요."
    state.congestionLevel == "혼잡" -> "지금은 대기 시간이 길어요. 시간 조정을 추천합니다."
    state.congestionLevel == "원활" -> "지금 방문하시면 여유롭게 식사하실 수 있어요!"
    else -> "지금 가도 괜찮아요. 대기시간을 확인하고 이동하세요."
}

fun activeMealByClock(): String = "중식"
