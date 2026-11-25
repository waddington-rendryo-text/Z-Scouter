package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.components.CharacterDetailDialog
import com.example.ui.components.ZBottomNavigation
import com.example.ui.screens.FiguresShelfScreen
import com.example.ui.screens.RadarScreen
import com.example.ui.screens.SaiyanModeScreen
import com.example.ui.screens.SparkingBattleScreen
import com.example.ui.theme.DarkSpaceBg
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SaiyanGold
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
    var currentTab by rememberSaveable { mutableIntStateOf(0) }

    // Handle back button to return to Radar tab if currently on other tabs
    BackHandler(enabled = currentTab != 0) {
        currentTab = 0
    }

    val selectedDetail by viewModel.selectedCharacterDetail.collectAsState()
    val isLoadingDetail by viewModel.isLoadingDetail.collectAsState()
    val figures by viewModel.figures.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = DarkSpaceBg,
        bottomBar = {
            ZBottomNavigation(
                currentTabIndex = currentTab,
                onTabSelected = { currentTab = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = currentTab,
                label = "tab_crossfade"
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> RadarScreen(viewModel = viewModel)
                    1 -> SaiyanModeScreen(viewModel = viewModel)
                    2 -> SparkingBattleScreen(viewModel = viewModel)
                    3 -> FiguresShelfScreen(viewModel = viewModel)
                }
            }

            // Loading overlay for detail fetch
            if (isLoadingDetail) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x800A0E17)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SaiyanGold)
                }
            }

            // Character Detail Dialog
            selectedDetail?.let { detail ->
                val isInCollection = figures.any { it.characterId == detail.id }
                CharacterDetailDialog(
                    character = detail,
                    isInCollection = isInCollection,
                    onDismiss = { viewModel.closeCharacterDetail() },
                    onToggleCollection = {
                        val basicDto = com.example.data.model.CharacterDto(
                            id = detail.id,
                            name = detail.name,
                            ki = detail.ki,
                            maxKi = detail.maxKi,
                            race = detail.race,
                            gender = detail.gender,
                            description = detail.description,
                            image = detail.image,
                            affiliation = detail.affiliation
                        )
                        viewModel.toggleFigureCollection(basicDto)
                    }
                )
            }
        }
    }
}
