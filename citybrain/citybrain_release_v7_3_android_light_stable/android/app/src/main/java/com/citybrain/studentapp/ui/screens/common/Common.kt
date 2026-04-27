package com.citybrain.studentapp.ui.screens.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
fun StudentHeroHeader(
    title: String = "Paichai University Cafeteria",
    greeting: String = "Hello, Paichai Student!",
    dateText: String = "May 12, 2025 (Mon)"
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color(0xFFF6A341), Color(0xFFEF942E))))
            .padding(start = 18.dp, end = 18.dp, top = 26.dp, bottom = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(greeting, color = Color.White.copy(alpha = 0.92f), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text(
                    title,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🔔", fontSize = MaterialTheme.typography.titleMedium.fontSize)
            }
        }
        Spacer(Modifier.height(16.dp))
        DatePill(dateText = dateText)
    }
}

@Composable
fun DatePill(dateText: String, blue: Boolean = false) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (blue) Color.White.copy(alpha = 0.14f) else SurfaceWhite)
            .border(1.dp, if (blue) Color.White.copy(alpha = 0.22f) else Color.Transparent, RoundedCornerShape(999.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "📅  $dateText  ⌄",
            color = if (blue) Color.White else TextPrimary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun MealSegmentedTabs() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceMuted, RoundedCornerShape(999.dp))
            .padding(5.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        SegmentButton("Breakfast", false, Modifier.weight(1f))
        SegmentButton("Lunch", true, Modifier.weight(1f))
        SegmentButton("Dinner", false, Modifier.weight(1f))
    }
}

@Composable
private fun SegmentButton(text: String, selected: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) SurfaceWhite else Color.Transparent)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) AccentOrangeDark else TextSecondary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun StudentFilterRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconChip()
        FilterChip("All", true)
        FilterChip("Under 6,000₩", false)
        FilterChip("Near me", false)
        FilterChip("High rating", false)
    }
}

@Composable
private fun IconChip() {
    Box(
        modifier = Modifier
            .size(42.dp)
            .background(SurfaceWhite, RoundedCornerShape(999.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(999.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("☷", color = TextSecondary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun FilterChip(text: String, selected: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) AccentOrangeSoft else SurfaceWhite)
            .border(1.dp, if (selected) AccentOrange.copy(alpha = 0.55f) else BorderSoft, RoundedCornerShape(999.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            color = if (selected) AccentOrangeDark else TextSecondary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun NoticeStrip(title: String, body: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF0F3F8), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(
            text = "$title · $body",
            color = Color(0xFF3D4857),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SmallBadge(text: String) {
    Box(
        modifier = Modifier
            .background(AccentOrangeSoft, RoundedCornerShape(999.dp))
            .border(1.dp, AccentOrange.copy(alpha = 0.35f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = AccentOrangeDark,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun TodaysSpecialCard(
    menuName: String,
    totalCount: Int,
    remainingCount: Int,
    congestionLevel: String,
    waitMinutes: Int,
    selloutEta: String,
    currentPeople: Int,
    judgement: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, Color(0xFFF2E3D1)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallBadge("TODAY'S SPECIAL")
                Text(
                    text = menuName,
                    color = TextPrimary,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text("실시간 운영 정보가 반영되는 오늘의 일품식", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
            }

            FoodHeroVisual()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BigMetric("$remainingCount left", "of $totalCount meals", Modifier.weight(1f))
                VerticalDivider()
                BigMetric(congestionLevel, "Congestion", Modifier.weight(1f), color = congestionColor(congestionLevel))
                VerticalDivider()
                BigMetric("$waitMinutes min", "Est. wait time", Modifier.weight(1f))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                MiniInfo("Sold out ETA", selloutEta, Modifier.weight(1f))
                MiniInfo("Current people", "약 ${currentPeople}명", Modifier.weight(1f))
                MiniInfo("Status", judgement, Modifier.weight(1f))
            }

            LinearProgressIndicator(
                progress = { if (totalCount <= 0) 0f else remainingCount.toFloat() / totalCount.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(999.dp)),
                color = AccentOrange,
                trackColor = Color(0xFFEEF2F7)
            )
        }
    }
}

@Composable
private fun FoodHeroVisual() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(144.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFFDE9D1), Color(0xFFFFF9F0))))
            .border(1.dp, Color(0xFFFAEAD9), RoundedCornerShape(22.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(118.dp)
                .background(Brush.linearGradient(listOf(Color(0xFFFFB24D), Color(0xFFF28D35))), CircleShape)
                .border(10.dp, Color.White.copy(alpha = 0.25f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🍛", style = MaterialTheme.typography.displaySmall)
        }
    }
}

@Composable
fun DetailFoodHero(menuName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFFF6A341), Color(0xFFFFF2E2))))
            .padding(18.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(118.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.78f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🍛  🥗  🥣", style = MaterialTheme.typography.displayMedium)
            }
            Text(menuName, color = TextPrimary, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun BigMetric(value: String, label: String, modifier: Modifier = Modifier, color: Color = TextPrimary) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
        Text(label, color = TextSecondary, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
    }
}

@Composable
private fun VerticalDivider() {
    Box(modifier = Modifier.width(1.dp).height(44.dp).background(BorderSoft))
}

@Composable
fun MiniInfo(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFFFAFBFD), RoundedCornerShape(16.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(label, color = TextTertiary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
        Text(value, color = TextPrimary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun CampusCafeteriaCard(
    emoji: String,
    name: String,
    location: String,
    rating: String,
    likes: String,
    price: String,
    tags: List<String>,
    badge: String? = null,
    tint: Color = AccentOrange
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, BorderSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(tint.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) { Text(emoji, style = MaterialTheme.typography.headlineMedium) }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(name, color = TextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (badge != null) CompactTag(badge, tint)
                }
                Text(location, color = TextSecondary, style = MaterialTheme.typography.labelMedium)
                Text("⭐ $rating   ♡ $likes", color = TextSecondary, style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    tags.forEach { CompactTag(it, Color(0xFF7A8495), soft = true) }
                }
            }

            Text(price, color = TextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun CompactTag(text: String, tint: Color, soft: Boolean = false) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (soft) SurfaceMuted else tint.copy(alpha = 0.14f))
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Text(text, color = if (soft) TextSecondary else tint, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun DetailStatusGrid(
    remainingCount: Int,
    totalCount: Int,
    selloutEta: String,
    congestionLevel: String,
    waitMinutes: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, BorderSoft)
    ) {
        Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            BigMetric("$remainingCount", "of $totalCount meals", Modifier.weight(1f), AccentOrangeDark)
            VerticalDivider()
            BigMetric(selloutEta, "Expected sold out", Modifier.weight(1f), TextPrimary)
            VerticalDivider()
            BigMetric(congestionLevel, "Congestion", Modifier.weight(1f), congestionColor(congestionLevel))
            VerticalDivider()
            BigMetric("$waitMinutes", "min wait", Modifier.weight(1f), BrandBlue)
        }
    }
}

@Composable
fun PreferencePollCard() {
    SectionCard(title = "How does this menu look to you?") {
        Text("Your opinion helps improve tomorrow's menu.", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            ReactionChip("Love it! 😍", "128", AccentOrange, Modifier.weight(1f))
            ReactionChip("Good 👍", "42", BrandBlue, Modifier.weight(1f))
            ReactionChip("So-so 😐", "15", WarningOrange, Modifier.weight(1f))
            ReactionChip("Not for me 👎", "8", DangerRed, Modifier.weight(1f))
        }
    }
}

@Composable
private fun ReactionChip(label: String, count: String, tint: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(tint.copy(alpha = 0.10f), RoundedCornerShape(14.dp))
            .border(1.dp, tint.copy(alpha = 0.18f), RoundedCornerShape(14.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label, color = tint, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(count, color = TextPrimary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun LiveSummaryCard(
    remainingCount: Int,
    congestionLevel: String,
    waitMinutes: Int,
    currentPeople: Int,
    updatedAt: String,
    isLivePolling: Boolean
) {
    SectionCard(title = "Live Data Summary") {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(if (isLivePolling) "Syncing..." else "LIVE", color = AccentOrangeDark, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold)
            Text("Updated $updatedAt", color = TextTertiary, style = MaterialTheme.typography.labelSmall)
        }
        InfoRow("Remaining quantity", "$remainingCount meals")
        InfoRow("Current congestion", congestionLevel, congestionColor(congestionLevel))
        InfoRow("Average wait time", "$waitMinutes min")
        InfoRow("Current people", "약 ${currentPeople}명")
    }
}

@Composable
fun SectionHeader(title: String, action: String? = null) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, color = TextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
        if (action != null) Text(action, color = AccentOrangeDark, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun AppTopBar(title: String, subtitle: String? = null, blue: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = if (blue) BrandBlue else SurfaceWhite),
        border = BorderStroke(1.dp, if (blue) BrandBlue else BorderSoft)
    ) {
        Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(if (blue) Color.White.copy(alpha = 0.18f) else BrandBlueSoft, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) { Text("CB", color = if (blue) Color.White else BrandBlue, fontWeight = FontWeight.ExtraBold) }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, color = if (blue) Color.White else TextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                if (subtitle != null) Text(subtitle, color = if (blue) Color.White.copy(alpha = 0.78f) else TextTertiary, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun ScreenHeader(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}

@Composable
fun HeroStatusCard(menuName: String, remainingCount: Int, congestionLevel: String, waitMinutes: Int, selloutEta: String) {
    TodaysSpecialCard(
        menuName = menuName,
        totalCount = 100,
        remainingCount = remainingCount,
        congestionLevel = congestionLevel,
        waitMinutes = waitMinutes,
        selloutEta = selloutEta,
        currentPeople = 31,
        judgement = if (congestionLevel == "혼잡") "시간 조정 권장" else "지금 이용 가능"
    )
}

@Composable
fun Pill(text: String, bg: Color, fg: Color) {
    Box(modifier = Modifier.background(bg, RoundedCornerShape(999.dp)).padding(horizontal = 12.dp, vertical = 7.dp)) {
        Text(text, color = fg, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
    }
}

enum class MetricType { Menu, Congestion, Wait, Notice }

@Composable
fun MetricCard(title: String, value: String, type: MetricType, modifier: Modifier = Modifier) {
    val accent = when (type) {
        MetricType.Menu -> BrandBlue
        MetricType.Congestion -> WarningOrange
        MetricType.Wait -> AccentOrange
        MetricType.Notice -> SuccessGreen
    }
    val symbol = when (type) {
        MetricType.Menu -> "식"
        MetricType.Congestion -> "혼"
        MetricType.Wait -> "시"
        MetricType.Notice -> "공"
    }
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), border = BorderStroke(1.dp, BorderSoft)) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.size(34.dp).background(accent.copy(alpha = 0.12f), CircleShape), contentAlignment = Alignment.Center) {
                Text(symbol, color = accent, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold)
            }
            Text(title, color = TextSecondary, style = MaterialTheme.typography.labelLarge)
            Text(value, color = TextPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), border = BorderStroke(1.dp, BorderSoft)) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, color = TextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, valueColor: Color = TextPrimary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceMuted, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        Text(value, color = valueColor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StatusBadge(level: String) {
    val color = congestionColor(level)
    Box(modifier = Modifier.background(color.copy(alpha = 0.12f), RoundedCornerShape(999.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
        Text(level, color = color, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun NoticeCard(title: String, body: String, date: String) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), border = BorderStroke(1.dp, BorderSoft)) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(date, color = TextTertiary, style = MaterialTheme.typography.labelMedium)
            Text(title, color = TextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            HorizontalDivider(color = BorderSoft)
            Text(body, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun PrimaryActionButton(text: String, modifier: Modifier = Modifier) {
    Button(
        onClick = {},
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
        shape = RoundedCornerShape(16.dp)
    ) { Text(text, color = Color.White, fontWeight = FontWeight.ExtraBold) }
}

@Composable
fun LoadingBlock() {
    Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = AccentOrange)
    }
}

fun congestionColor(level: String): Color = when (level) {
    "원활" -> SuccessGreen
    "혼잡" -> DangerRed
    "보통" -> WarningOrange
    "Normal" -> SuccessGreen
    "Busy" -> WarningOrange
    else -> BrandBlue
}
