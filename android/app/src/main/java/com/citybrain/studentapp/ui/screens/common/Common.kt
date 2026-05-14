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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.citybrain.studentapp.data.model.NoticeItem
import com.citybrain.studentapp.data.model.StudentState
import com.citybrain.studentapp.ui.theme.AccentOrange
import com.citybrain.studentapp.ui.theme.AccentOrangeDark
import com.citybrain.studentapp.ui.theme.AccentOrangeSoft
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
import com.citybrain.studentapp.ui.theme.TextTertiary
import com.citybrain.studentapp.ui.theme.WarningOrange

@Composable
fun PagePadding(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppBg)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        content = content
    )
}

@Composable
fun CompactHeader(title: String, subtitle: String, action: String? = null, onAction: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text("배재대학교", color = BrandBlue, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
            Text(title, color = TextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            Text(subtitle, color = TextSecondary, style = MaterialTheme.typography.labelSmall, lineHeight = MaterialTheme.typography.labelSmall.lineHeight)
        }
        if (action != null) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .clickable { onAction() },
                contentAlignment = Alignment.Center
            ) { Text(action, color = TextSecondary) }
        }
    }
}

@Composable
fun MealTabs(selected: String, onSelect: (String) -> Unit = {}) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(999.dp))
                .background(SurfaceMuted)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("조식", "중식", "석식").forEach { label ->
                val active = label == selected
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (active) SurfaceWhite else Color.Transparent)
                        .clickable { onSelect(label) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, color = if (active) BrandBlue else TextSecondary, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
        Text(
            "현재 시간 기준 자동 전환  ⓘ",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = TextTertiary,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun HomeHeroCard(state: StudentState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(BrandBlue, Color(0xFF004098))))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SmallBlueOutlineBadge("오늘의 일품식")
                Text(state.menuName, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("쌀밥, 된장국, 김치 포함", color = Color.White.copy(alpha = .86f), style = MaterialTheme.typography.labelMedium)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FoodPhoto(modifier = Modifier.size(92.dp))
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("남은 수량", color = Color.White.copy(alpha = .8f), style = MaterialTheme.typography.labelSmall)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("${state.remainingCount}", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                            Text(" / ${state.totalCount} 식", color = Color.White, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        }
                        StatusPill(state.congestionLevel, compact = true)
                        Text("예상 대기시간 ${state.waitMinutes}분", color = Color.White.copy(alpha = .88f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Text("품절 예상 ${state.selloutEta}", color = Color.White.copy(alpha = .88f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }
                Text(recommendationText(state), color = Color.White, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FoodPhoto(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFFFFF5E4)),
        contentAlignment = Alignment.Center
    ) {
        Text("🍱", style = MaterialTheme.typography.headlineLarge)
    }
}

@Composable
fun SmallBlueOutlineBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .border(1.dp, Color.White.copy(alpha = .35f), RoundedCornerShape(999.dp))
            .background(Color.White.copy(alpha = .13f))
            .padding(horizontal = 11.dp, vertical = 6.dp)
    ) { Text(text, color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold) }
}

@Composable
fun MetricBox(title: String, value: String, sub: String? = null, color: Color = BrandBlue, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, BorderSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(11.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(title, color = TextSecondary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text(value, color = color, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            if (sub != null) Text(sub, color = TextTertiary, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun QuickActionTile(icon: String, title: String, subtitle: String, modifier: Modifier = Modifier, color: Color = BrandBlue, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(13.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, color.copy(alpha = .22f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(icon)
            Text(title, color = color, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
            Text(subtitle, color = TextTertiary, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun AppCard(title: String? = null, subtitle: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, BorderSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (title != null) Text(title, color = TextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            if (subtitle != null) Text(subtitle, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            content()
        }
    }
}

@Composable
fun StatusPill(level: String, compact: Boolean = false) {
    val color = congestionColor(level)
    val label = if (level == "원활") "원활" else if (level == "혼잡") "혼잡" else "보통"
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color)
            .padding(horizontal = if (compact) 12.dp else 14.dp, vertical = if (compact) 5.dp else 7.dp),
        contentAlignment = Alignment.Center
    ) { Text(label, color = Color.White, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold) }
}

@Composable
fun ChoiceChip(text: String, selected: Boolean, color: Color = BrandBlue, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) color else SurfaceWhite)
            .border(1.dp, if (selected) color else BorderSoft, RoundedCornerShape(999.dp))
            .clickable { onClick() }
            .padding(horizontal = 15.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) { Text(text, color = if (selected) Color.White else TextSecondary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold) }
}

@Composable
fun FilterChips(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) { options.forEach { ChoiceChip(it, selected == it, BrandBlue) { onSelect(it) } } }
}

@Composable
fun NoticeListCard(item: NoticeItem, category: String = "운영") {
    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(BrandBlueSoft),
                contentAlignment = Alignment.Center
            ) { Text("📢") }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    LightBadge(category)
                    Text(item.createdAt.take(10), color = TextTertiary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
                Text(item.title, color = TextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold)
                Text(item.body, color = TextSecondary, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Text("›", color = TextTertiary, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun LightBadge(text: String, color: Color = BrandBlue) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color.copy(alpha = .12f))
            .border(1.dp, color.copy(alpha = .20f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) { Text(text, color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold) }
}

@Composable
fun MenuInfoRow(icon: String, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(icon, color = TextSecondary)
        Text(label, color = TextSecondary, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        Text(value, color = TextPrimary, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PrimaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
    ) { Text(text, color = Color.White, fontWeight = FontWeight.ExtraBold) }
}

@Composable
fun OutlineButtonLike(text: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) {
        Text(text, color = BrandBlue, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun OpinionBox(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { if (it.length <= 200) onValueChange(it) },
        modifier = Modifier.fillMaxWidth().height(96.dp),
        placeholder = { Text("의견을 입력해주세요. (최대 200자)", color = TextTertiary) },
        shape = RoundedCornerShape(10.dp),
        supportingText = { Text("${value.length} / 200") }
    )
}

fun congestionColor(level: String): Color = when (level) {
    "원활" -> SuccessGreen
    "혼잡" -> DangerRed
    "보통" -> WarningOrange
    "Normal" -> SuccessGreen
    "Busy" -> DangerRed
    else -> WarningOrange
}

fun recommendationText(state: StudentState): String = when {
    state.remainingCount <= 0 -> "오늘 일품식이 소진됐어요. 다른 메뉴를 확인하세요."
    state.remainingCount <= 10 -> "곧 소진될 수 있어요. 이동 전에 확인하세요."
    state.congestionLevel == "혼잡" -> "지금은 대기 시간이 길어요. 시간을 조정해보세요."
    state.congestionLevel == "원활" -> "지금 방문하시면 여유롭게 식사하실 수 있어요!"
    else -> "지금 가도 괜찮아요. 대기시간을 확인하고 이동하세요."
}
