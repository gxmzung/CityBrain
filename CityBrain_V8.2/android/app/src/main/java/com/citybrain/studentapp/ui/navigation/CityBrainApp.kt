package com.citybrain.studentapp.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.citybrain.studentapp.data.repository.CityBrainRepository
import com.citybrain.studentapp.ui.screens.home.HomeScreen
import com.citybrain.studentapp.ui.screens.home.HomeViewModel
import com.citybrain.studentapp.ui.screens.home.HomeViewModelFactory
import com.citybrain.studentapp.ui.screens.live.LiveScreen
import com.citybrain.studentapp.ui.screens.menu.MenuScreen
import com.citybrain.studentapp.ui.screens.notice.NoticeScreen
import com.citybrain.studentapp.ui.screens.participate.ParticipateScreen
import com.citybrain.studentapp.ui.theme.AppBg
import com.citybrain.studentapp.ui.theme.BrandBlue
import com.citybrain.studentapp.ui.theme.BrandBlueSoft
import com.citybrain.studentapp.ui.theme.TextTertiary

private data class Dest(val route: String, val label: String, val symbol: String)

@Composable
fun CityBrainApp(repository: CityBrainRepository) {
    val navController = rememberNavController()
    val destinations = listOf(
        Dest("home", "홈", "⌂"),
        Dest("live", "실시간", "◷"),
        Dest("menu", "메뉴", "▥"),
        Dest("notice", "공지", "◼"),
        Dest("participate", "참여", "♣")
    )
    val vm: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = AppBg,
        bottomBar = {
            val backStack by navController.currentBackStackEntryAsState()
            val currentRoute = backStack?.destination?.route ?: "home"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(Color.White)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                destinations.forEach { dest ->
                    val selected = currentRoute == dest.route
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                navController.navigate(dest.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                }
                            }
                            .padding(vertical = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 36.dp, height = 25.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(if (selected) BrandBlueSoft else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dest.symbol,
                                color = if (selected) BrandBlue else TextTertiary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Text(
                            text = dest.label,
                            color = if (selected) BrandBlue else TextTertiary,
                            fontSize = 9.sp,
                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = "home", modifier = Modifier.padding(innerPadding)) {
            composable("home") { HomeScreen(uiState = uiState, onRefresh = { vm.refresh() }, openTab = { navController.navigate(it) }) }
            composable("live") { LiveScreen(uiState = uiState, onRefresh = { vm.refresh() }) }
            composable("menu") { MenuScreen(uiState = uiState) }
            composable("notice") { NoticeScreen(uiState = uiState) }
            composable("participate") { ParticipateScreen(uiState = uiState) }
        }
    }
}
