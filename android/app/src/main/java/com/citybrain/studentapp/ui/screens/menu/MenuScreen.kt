package com.citybrain.studentapp.ui.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.citybrain.studentapp.ui.screens.common.BlueBadge
import com.citybrain.studentapp.ui.screens.common.CloneHeader
import com.citybrain.studentapp.ui.screens.common.ClonePage
import com.citybrain.studentapp.ui.screens.common.FilterChip
import com.citybrain.studentapp.ui.screens.common.FoodPlate
import com.citybrain.studentapp.ui.screens.common.MealSegment
import com.citybrain.studentapp.ui.screens.common.WhiteCard
import com.citybrain.studentapp.ui.screens.common.displayMenuName
import com.citybrain.studentapp.ui.screens.home.HomeUiState
import com.citybrain.studentapp.ui.theme.BrandBlue
import com.citybrain.studentapp.ui.theme.BrandBlueSoft
import com.citybrain.studentapp.ui.theme.SurfaceWhite
import com.citybrain.studentapp.ui.theme.TextPrimary
import com.citybrain.studentapp.ui.theme.TextSecondary
import com.citybrain.studentapp.ui.theme.TextTertiary

@Composable
fun MenuScreen(uiState: HomeUiState) {
    val state = uiState.bundle?.state
    var selectedMeal by remember { mutableStateOf("중식") }
    var selectedTab by remember { mutableStateOf("오늘 메뉴") }

    ClonePage {
        item { CloneHeader("오늘의 메뉴", "오늘과 주간 메뉴를 확인하세요.", icon = "♧") }
        item { MealSegment(selectedMeal) { selectedMeal = it } }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip("오늘 메뉴", selectedTab == "오늘 메뉴", Modifier.weight(1f)) { selectedTab = "오늘 메뉴" }
                FilterChip("주간 메뉴", selectedTab == "주간 메뉴", Modifier.weight(1f)) { selectedTab = "주간 메뉴" }
            }
        }
        item {
            WhiteCard {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FoodPlate(Modifier.size(78.dp), emojiFallback = true)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        BlueBadge("오늘의 일품식")
                        Text(displayMenuName(state?.menuName ?: "제육볶음 정식"), color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("5,500원", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                        Text("쌀밥, 된장국, 김치 포함", color = TextSecondary, fontSize = 10.sp, maxLines = 1)
                    }
                }
                InfoRow("운영 시간", "11:30 ~ 14:00")
                InfoRow("이용 가격", "일반 5,500원   교직원 6,000원")
                InfoRow("식당 위치", "배재관 1층 학생식당")
            }
        }
        item {
            WhiteCard {
                Text("주간 메뉴 미리보기", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                Text("식단은 운영 사정에 따라 변경될 수 있습니다.", color = TextSecondary, fontSize = 10.sp)
                listOf(
                    "월 5.12" to "제육볶음 정식 / 돈까스 카레",
                    "화 5.13" to "닭갈비 덮밥 / 김치찌개",
                    "수 5.14" to "불고기 정식 / 비빔밥",
                    "목 5.15" to "함박스테이크 / 잔치국수",
                    "금 5.16" to "치킨마요 덮밥 / 부대찌개"
                ).forEach { (day, menu) -> WeekRow(day, menu) }
            }
        }
        item {
            Box(Modifier.fillMaxWidth().height(42.dp).clip(RoundedCornerShape(11.dp)).background(SurfaceWhite).clickable { }, contentAlignment = Alignment.Center) {
                Text("전체 식단표 보기", color = BrandBlue, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable private fun InfoRow(label: String, value: String) { Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(11.dp)).background(Color(0xFFF5F7FB)).padding(horizontal = 12.dp, vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text(label, color = TextSecondary, fontSize = 11.sp); Text(value, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold) } }
@Composable private fun WeekRow(day: String, menu: String) { Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color(0xFFF5F7FB)).padding(horizontal = 11.dp, vertical = 9.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text(day, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.width(8.dp)); Text(menu, color = BrandBlue, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis) } }
