package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.example.domain.model.Character
import com.example.ui.theme.CapsuleCyan
import com.example.ui.theme.DarkSpaceBg
import com.example.ui.theme.DarkSpaceBorder
import com.example.ui.theme.DarkSpaceCard
import com.example.ui.theme.DarkSpaceSurface
import com.example.ui.theme.KiFlameOrange
import com.example.ui.theme.SaiyanGold
import com.example.ui.theme.ScouterGreen
import com.example.ui.theme.ScouterRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.UiState
import com.example.ui.viewmodel.ZScouterViewModel

@Composable
fun SparkingBattleScreen(
    viewModel: ZScouterViewModel,
    modifier: Modifier = Modifier
) {
    val charactersState by viewModel.charactersState.collectAsStateWithLifecycle()
    val fighter1 by viewModel.battleFighter1.collectAsStateWithLifecycle()
    val fighter2 by viewModel.battleFighter2.collectAsStateWithLifecycle()
    val isSimulating by viewModel.isSimulatingBattle.collectAsStateWithLifecycle()
    val battleResult by viewModel.battleResult.collectAsStateWithLifecycle()

    var selectingForFighterSlot by remember { mutableStateOf<Int?>(null) }

    val allCharacters = when (val state = charactersState) {
        is UiState.Success -> state.data
        else -> emptyList()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkSpaceBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SportsKabaddi,
                        contentDescription = null,
                        tint = SaiyanGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SIMULADOR SPARKING ZERO",
                        style = MaterialTheme.typography.titleLarge,
                        color = SaiyanGold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Debate de Poder & Análise Scouter para conversas épicas de anime",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Versus Faceoff Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF1E1430),
                                DarkSpaceCard,
                                Color(0xFF142436)
                            )
                        )
                    )
                    .border(1.5.dp, DarkSpaceBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Fighter 1 Slot
                    FighterSlotView(
                        character = fighter1,
                        label = "LUTADOR 1",
                        borderColor = KiFlameOrange,
                        onClick = { selectingForFighterSlot = 1 },
                        modifier = Modifier.weight(1f)
                    )

                    // VS Emblem
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(SaiyanGold, KiFlameOrange)
                                )
                            )
                            .border(2.dp, DarkSpaceBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "VS",
                            style = MaterialTheme.typography.labelLarge,
                            color = DarkSpaceBg,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Fighter 2 Slot
                    FighterSlotView(
                        character = fighter2,
                        label = "LUTADOR 2",
                        borderColor = CapsuleCyan,
                        onClick = { selectingForFighterSlot = 2 },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Fighter Picker Drawer / Row (if user clicked to choose)
        if (selectingForFighterSlot != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSpaceSurface)
                        .border(1.dp, SaiyanGold, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ESCOLHA O LUTADOR ${selectingForFighterSlot}",
                                style = MaterialTheme.typography.titleMedium,
                                color = SaiyanGold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Clique para selecionar",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(allCharacters) { char ->
                                Box(
                                    modifier = Modifier
                                        .width(90.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(DarkSpaceCard)
                                        .border(1.dp, DarkSpaceBorder, RoundedCornerShape(10.dp))
                                        .clickable {
                                            if (selectingForFighterSlot == 1) {
                                                viewModel.battleFighter1.value = char
                                            } else {
                                                viewModel.battleFighter2.value = char
                                            }
                                            selectingForFighterSlot = null
                                        }
                                        .padding(6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        SubcomposeAsyncImage(
                                            model = char.image,
                                            contentDescription = char.name,
                                            modifier = Modifier.size(60.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = char.name,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = TextPrimary,
                                            fontSize = 9.sp,
                                            maxLines = 1,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Action Button: Simulate Battle
        item {
            Button(
                onClick = { viewModel.runBattleSimulation() },
                enabled = fighter1 != null && fighter2 != null && !isSimulating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("simulate_battle_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SaiyanGold,
                    disabledContainerColor = DarkSpaceCard
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isSimulating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = DarkSpaceBg,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "CALCULANDO COM SCOUTER...",
                        style = MaterialTheme.typography.labelLarge,
                        color = DarkSpaceBg
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = DarkSpaceBg
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ANALISAR COM SCOUTER",
                        style = MaterialTheme.typography.labelLarge,
                        color = DarkSpaceBg
                    )
                }
            }
        }

        // Battle Result Card
        battleResult?.let { result ->
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(DarkSpaceSurface)
                            .border(1.5.dp, ScouterGreen, RoundedCornerShape(20.dp))
                            .padding(18.dp)
                    ) {
                        Column {
                            // Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "VEREDITO DO SCOUTER",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = ScouterGreen,
                                    fontSize = 11.sp
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SaiyanGold.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "VANTAGEM: +${result.differencePct}%",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = SaiyanGold,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Winner Card
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(DarkSpaceCard)
                                    .padding(12.dp)
                            ) {
                                SubcomposeAsyncImage(
                                    model = result.winner.image,
                                    contentDescription = result.winner.name,
                                    modifier = Modifier.size(70.dp),
                                    contentScale = ContentScale.Fit
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "VENCEDOR PROVÁVEL",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = ScouterGreen,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = result.winner.name,
                                        style = MaterialTheme.typography.displayMedium,
                                        color = SaiyanGold,
                                        fontSize = 20.sp
                                    )
                                    Text(
                                        text = "Raça: ${result.winner.race ?: "Desconhecida"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Tactical analysis
                            Text(
                                text = "ANÁLISE DE COMBATE:",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = result.analysisVerdict,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Luke's Bar Debate Quote
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CapsuleCyan.copy(alpha = 0.1f))
                                    .border(1.dp, CapsuleCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "ARGUMENTO DE BAR DO LUKE:",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = CapsuleCyan,
                                        fontSize = 9.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "\"${result.barDebateQuote}\"",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextPrimary,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FighterSlotView(
    character: Character?,
    label: String,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSpaceCard)
            .border(1.dp, borderColor.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = borderColor,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (character != null) {
                SubcomposeAsyncImage(
                    model = character.image,
                    contentDescription = character.name,
                    modifier = Modifier.size(90.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    maxLines = 1
                )
                Text(
                    text = "Ki: ${character.ki ?: "?"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SaiyanGold,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            } else {
                Box(
                    modifier = Modifier.size(90.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Toque para escolher",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Trocar",
                style = MaterialTheme.typography.labelSmall,
                color = CapsuleCyan,
                fontSize = 9.sp
            )
        }
    }
}
