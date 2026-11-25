package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Radar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.ui.navigation.FiguresShelfRoute
import com.example.ui.navigation.RadarRoute
import com.example.ui.navigation.SaiyanModeRoute
import com.example.ui.navigation.SparkingBattleRoute
import com.example.ui.theme.DarkSpaceBg
import com.example.ui.theme.DarkSpaceSurface
import com.example.ui.theme.SaiyanGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

sealed class NavTab(
    val route: Any,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    data object Radar : NavTab(RadarRoute, "Radar Z", Icons.Filled.Radar, Icons.Outlined.Radar, "nav_radar")
    data object SaiyanMode : NavTab(SaiyanModeRoute, "Modo Sayajin", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter, "nav_saiyan_mode")
    data object Sparking : NavTab(SparkingBattleRoute, "Batalha", Icons.Filled.FlashOn, Icons.Outlined.FlashOn, "nav_sparking")
    data object Figures : NavTab(FiguresShelfRoute, "Prateleira", Icons.Filled.Inventory2, Icons.Outlined.Inventory2, "nav_figures")

    companion object {
        val items = listOf(Radar, SaiyanMode, Sparking, Figures)
    }
}

@Composable
fun ZBottomNavigation(
    currentDestination: NavDestination?,
    onNavigateToTab: (Any) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        windowInsets = WindowInsets.navigationBars,
        containerColor = DarkSpaceSurface,
        contentColor = TextPrimary
    ) {
        NavTab.items.forEach { tab ->
            val isSelected = currentDestination?.hierarchy?.any { it.hasRoute(tab.route::class) } == true
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigateToTab(tab.route) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.title,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 10.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DarkSpaceBg,
                    selectedTextColor = SaiyanGold,
                    indicatorColor = SaiyanGold,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                ),
                modifier = Modifier.testTag(tab.testTag)
            )
        }
    }
}
