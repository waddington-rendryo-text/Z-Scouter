package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CharacterDto
import com.example.ui.components.CharacterCard
import com.example.ui.theme.CapsuleCyan
import com.example.ui.theme.DarkSpaceBg
import com.example.ui.theme.DarkSpaceBorder
import com.example.ui.theme.DarkSpaceCard
import com.example.ui.theme.KiFlameOrange
import com.example.ui.theme.SaiyanGold
import com.example.ui.theme.ScouterGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.UiState
import com.example.ui.viewmodel.ZScouterViewModel

@Composable
fun RadarScreen(
    viewModel: ZScouterViewModel,
    modifier: Modifier = Modifier
) {
    val charactersState by viewModel.charactersState.collectAsState()
    val filteredList by viewModel.filteredCharacters.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedRace by viewModel.selectedRaceFilter.collectAsState()
    val figures by viewModel.figures.collectAsState()

    val savedCharacterIds = figures.map { it.characterId }.toSet()

    val raceFilters = listOf("Todos", "Saiyajin", "Humano", "Namekuseijin", "Androide", "Majin", "Deuses / Anjos")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkSpaceBg)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Hero Banner of Vegeta for Designer Luke
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, SaiyanGold.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.vegeta_banner),
                        contentDescription = "Vegeta Super Saiyajin Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Text Overlay Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSpaceBg.copy(alpha = 0.85f))
                            .border(1.dp, CapsuleCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Column {
                            Text(
                                text = "RADAR Z • UNIVERSO 7",
                                style = MaterialTheme.typography.labelMedium,
                                color = CapsuleCyan,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "ENCICLOPÉDIA DE GUERREIROS",
                                style = MaterialTheme.typography.titleMedium,
                                color = SaiyanGold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Search Bar
            item(span = { GridItemSpan(2) }) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_field"),
                    placeholder = {
                        Text(
                            text = "Buscar guerreiro, ki ou técnica...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = SaiyanGold
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Limpar",
                                    tint = TextSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SaiyanGold,
                        unfocusedBorderColor = DarkSpaceBorder,
                        focusedContainerColor = DarkSpaceCard,
                        unfocusedContainerColor = DarkSpaceCard,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            // Race Filter Chips
            item(span = { GridItemSpan(2) }) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(raceFilters) { race ->
                        val isSelected = selectedRace == race
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectedRaceFilter.value = race },
                            label = {
                                Text(
                                    text = race,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontSize = 11.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SaiyanGold,
                                selectedLabelColor = DarkSpaceBg,
                                containerColor = DarkSpaceCard,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) SaiyanGold else DarkSpaceBorder
                            )
                        )
                    }
                }
            }

            // Status message / count
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LUTADORES DETECTADOS (${filteredList.size})",
                        style = MaterialTheme.typography.labelMedium,
                        color = ScouterGreen,
                        fontSize = 11.sp
                    )

                    Text(
                        text = "API DRAGON BALL ONLINE",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                        fontSize = 9.sp
                    )
                }
            }

            // Loading / Error / Empty States
            when (val state = charactersState) {
                is UiState.Loading -> {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = SaiyanGold)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Sintonizando Scouter com a API...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(DarkSpaceCard)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Falha no Sinal do Scouter",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = KiFlameOrange
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = state.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.loadCharacters() },
                                    colors = ButtonDefaults.buttonColors(containerColor = SaiyanGold)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, tint = DarkSpaceBg)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Reconectar", color = DarkSpaceBg)
                                }
                            }
                        }
                    }
                }
                is UiState.Success -> {
                    if (filteredList.isEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Nenhum guerreiro encontrado com esses filtros.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                        }
                    } else {
                        items(filteredList, key = { it.id }) { character ->
                            CharacterCard(
                                character = character,
                                isFigureSaved = savedCharacterIds.contains(character.id),
                                onCardClick = { viewModel.selectCharacterForDetail(character) },
                                onToggleFigure = { viewModel.toggleFigureCollection(character) }
                            )
                        }
                    }
                }
            }
        }
    }
}
