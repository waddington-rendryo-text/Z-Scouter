package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.ui.components.NavTab
import com.example.ui.components.ZBottomNavigation
import com.example.ui.navigation.CharacterDetailRoute
import com.example.ui.navigation.FiguresShelfRoute
import com.example.ui.navigation.RadarRoute
import com.example.ui.navigation.SaiyanModeRoute
import com.example.ui.navigation.SparkingBattleRoute
import com.example.ui.screens.CharacterDetailScreen
import com.example.ui.screens.FiguresShelfScreen
import com.example.ui.screens.RadarScreen
import com.example.ui.screens.SaiyanModeScreen
import com.example.ui.screens.SparkingBattleScreen
import com.example.ui.theme.DarkSpaceBg
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ZScouterViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ZScouterApp()
            }
        }
    }
}

@Composable
fun ZScouterApp(
    viewModel: ZScouterViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = NavTab.items.any { tab ->
        currentDestination?.hierarchy?.any { it.hasRoute(tab.route::class) } == true
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = DarkSpaceBg,
        bottomBar = {
            if (showBottomBar) {
                ZBottomNavigation(
                    currentDestination = currentDestination,
                    onNavigateToTab = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = RadarRoute,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            composable<RadarRoute> {
                RadarScreen(
                    viewModel = viewModel,
                    onCharacterClick = { characterId ->
                        navController.navigate(CharacterDetailRoute(characterId))
                    }
                )
            }

            composable<CharacterDetailRoute> { backStackEntry ->
                val detailRoute = backStackEntry.toRoute<CharacterDetailRoute>()
                CharacterDetailScreen(
                    characterId = detailRoute.characterId,
                    viewModel = viewModel,
                    onBackClick = { navController.navigateUp() }
                )
            }

            composable<SaiyanModeRoute> {
                SaiyanModeScreen(viewModel = viewModel)
            }

            composable<SparkingBattleRoute> {
                SparkingBattleScreen(viewModel = viewModel)
            }

            composable<FiguresShelfRoute> {
                FiguresShelfScreen(viewModel = viewModel)
            }
        }
    }
}
