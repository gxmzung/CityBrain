package com.citybrain.studentapp.ui.screens.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.citybrain.studentapp.ui.theme.AccentOrange
import com.citybrain.studentapp.ui.theme.AppBg
import com.citybrain.studentapp.ui.theme.BorderSoft
import com.citybrain.studentapp.ui.theme.BrandBlue
import com.citybrain.studentapp.ui.theme.BrandBlueDark
import com.citybrain.studentapp.ui.theme.BrandBlueSoft
import com.citybrain.studentapp.ui.theme.DangerRed
import com.citybrain.studentapp.ui.theme.SuccessGreen
import com.citybrain.studentapp.ui.theme.SurfaceWhite
import com.citybrain.studentapp.ui.theme.TextPrimary
import com.citybrain.studentapp.ui.theme.TextSecondary
import com.citybrain.studentapp.ui.theme.TextTertiary
import com.citybrain.studentapp.ui.theme.WarningOrange

@Composable
fun ClonePage(
    bottomPadding: Dp = 78.dp,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit
) {
    Box(Modifier.fillMaxSize().background(AppBg), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier.widthIn(max = 380.dp).fillMaxSize(),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 12.dp, bottom = bottomPadding),
            verticalArrangement = Arrangement.spacedBy(9.dp),
            content = content
        )
    }
}

@Composable
fun CloneHeader(title: String, subtitle: String, icon: String = "♧", onIconClick: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().padding(top = 2.dp), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.SpaceBetween) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text("배재대학교", color = BrandBlue, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.2).sp)
            Text(title, color = BrandBlueDark, fontSize = 16.sp, lineHeight = 19.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp)
            Text(subtitle, color = TextSecondary, fontSize = 10.sp, lineHeight = 13.sp, fontWeight = FontWeight.Medium)
        }
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).clickable(enabled = onIconClick != null) { onIconClick?.invoke() },
            contentAlignment = Alignment.Center
        ) { Text(icon, color = TextSecondary, fontSize = 17.sp, fontWeight = FontWeight.Bold) }
    }
}

@Composable
fun MealSegment(selected: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(999.dp)).background(Color(0xFFF0F3F8)).padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("조식", "중식", "석식").forEach { label ->
                val active = selected == label
                Box(
                    modifier = Modifier.weight(1f).height(30.dp).clip(RoundedCornerShape(999.dp)).background(if (active) SurfaceWhite else Color.Transparent).clickable { onSelect(label) },
                    contentAlignment = Alignment.Center
                ) { Text(label, color = if (active) BrandBlue else TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold) }
            }
        }
        Text("현재 시간 기준 자동 전환  ⓘ", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = TextTertiary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun WhiteCard(modifier: Modifier = Modifier, content: @Composable Column.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, BorderSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(9.dp), content = content) }
}

@Composable
fun BlueBadge(text: String) {
    Box(Modifier.clip(RoundedCornerShape(999.dp)).background(BrandBlueSoft).border(1.dp, Color(0xFFBFD8FF), RoundedCornerShape(999.dp)).padding(horizontal = 9.dp, vertical = 4.dp)) {
        Text(text, color = BrandBlue, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun FilterChip(label: String, active: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier.height(32.dp).clip(RoundedCornerShape(999.dp)).background(if (active) BrandBlue else SurfaceWhite)
            .border(1.dp, if (active) BrandBlue else BorderSoft, RoundedCornerShape(999.dp)).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) { Text(label, color = if (active) Color.White else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1) }
}

@Composable
fun MiniInfoBox(title: String, value: String, color: Color, modifier: Modifier = Modifier, sub: String? = null) {
    Card(modifier = modifier.height(78.dp), shape = RoundedCornerShape(13.dp), colors = CardDefaults.cardColors(SurfaceWhite), border = BorderStroke(1.dp, BorderSoft), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 9.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Text(title, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(value, color = color, fontSize = 18.sp, lineHeight = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.4).sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(sub ?: "", color = TextTertiary, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun ActionBox(icon: String, title: String, sub: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(modifier = modifier.height(68.dp).clickable { onClick() }, shape = RoundedCornerShape(13.dp), colors = CardDefaults.cardColors(SurfaceWhite), border = BorderStroke(1.dp, color.copy(alpha=.26f))) {
        Column(Modifier.fillMaxSize().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(icon, color = color, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(3.dp))
            Text(title, color = color, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, textAlign = TextAlign.Center)
            Text(sub, color = TextTertiary, fontSize = 8.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun FoodPlate(modifier: Modifier = Modifier, emojiFallback: Boolean = false) {
    if (emojiFallback) {
        Box(modifier.clip(RoundedCornerShape(14.dp)).background(Color(0xFFFFF3DD)), contentAlignment = Alignment.Center) { Text("🍱", fontSize = 38.sp) }
        return
    }
    Box(modifier.clip(CircleShape).background(Color(0xFFFFF4DE)).border(2.dp, Color.White.copy(alpha=.65f), CircleShape), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize().padding(8.dp)) {
            val c = Offset(size.width/2f, size.height/2f)
            drawCircle(Color.White, size.minDimension*.44f, c)
            drawCircle(Color(0xFFF0B35E), size.minDimension*.18f, Offset(size.width*.36f, size.height*.58f))
            drawCircle(Color(0xFF9A4F1B), size.minDimension*.20f, Offset(size.width*.58f, size.height*.45f))
            drawCircle(Color(0xFFDB6B2A), size.minDimension*.11f, Offset(size.width*.67f, size.height*.62f))
            drawCircle(Color(0xFF2E7D32), size.minDimension*.06f, Offset(size.width*.50f, size.height*.66f))
            drawCircle(Color(0xFFFFD166), size.minDimension*.07f, Offset(size.width*.42f, size.height*.38f))
            drawCircle(Color(0xFF111827).copy(alpha=.08f), size.minDimension*.45f, c, style = Stroke(width = 2.dp.toPx()))
        }
    }
}

fun normalizeCongestion(level: String): String = when (level) {
    "원활" -> "원활"; "혼잡" -> "혼잡"; "보통" -> "보통"; "Normal" -> "원활"; "Busy" -> "혼잡"; else -> "보통"
}

fun statusColor(level: String): Color = when (normalizeCongestion(level)) {
    "원활" -> SuccessGreen; "혼잡" -> DangerRed; else -> WarningOrange
}

fun displayMenuName(raw: String): String = when {
    raw.isBlank() -> "제육볶음 정식"
    raw.contains("서가앤쿡") -> "제육볶음 정식"
    raw.length > 10 -> raw.take(10)
    else -> raw
}
