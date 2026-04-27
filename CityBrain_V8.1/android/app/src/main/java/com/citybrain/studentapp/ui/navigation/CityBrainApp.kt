package com.citybrain.studentapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        Dest("menu", "메뉴", "▤"),
        Dest("notice", "공지", "◼"),
        Dest("participate", "참여", "♣")
    )
    val vm: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val backStack by navController.currentBackStackEntryAsState()
                val currentRoute = backStack?.destination?.route ?: "home"
                destinations.forEach { dest ->
                    NavigationBarItem(
                        selected = currentRoute == dest.route,
                        onClick = {
                            navController.navigate(dest.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                            }
                        },
                        icon = { Text(dest.symbol) },
                        label = { Text(dest.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandBlue,
                            selectedTextColor = BrandBlue,
                            indicatorColor = BrandBlueSoft,
                            unselectedIconColor = TextTertiary,
                            unselectedTextColor = TextTertiary
                        )
                    )
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
