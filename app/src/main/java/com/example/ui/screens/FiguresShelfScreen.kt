package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.data.local.FigureEntity
import com.example.ui.theme.CapsuleCyan
import com.example.ui.theme.DarkSpaceBg
import com.example.ui.theme.DarkSpaceBorder
import com.example.ui.theme.DarkSpaceCard
import com.example.ui.theme.DarkSpaceSurface
import com.example.ui.theme.KiFlameOrange
import com.example.ui.theme.SaiyanGold
import com.example.ui.theme.ScouterGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.UiState
import com.example.ui.viewmodel.ZScouterViewModel

@Composable
fun FiguresShelfScreen(
    viewModel: ZScouterViewModel,
    modifier: Modifier = Modifier
) {
    val figures by viewModel.figures.collectAsState()
    val planetsState by viewModel.planetsState.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Figures, 1: Planetas
    var filterStatus by remember { mutableStateOf("Todos") }
    var editingFigure by remember { mutableStateOf<FigureEntity?>(null) }

    val statusFilters = listOf("Todos", "Exposto no Estúdio", "Na Caixa Original", "Wishlist (Desejo)")

    val filteredFigures = figures.filter {
        filterStatus == "Todos" || it.status.equals(filterStatus, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkSpaceBg)
    ) {
        // Tab Row: Prateleira SH Figuarts vs Planetas da Lore
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSpaceSurface,
            contentColor = SaiyanGold,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = SaiyanGold,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("FIGURES SH (${figures.size})", style = MaterialTheme.typography.labelMedium)
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PLANETAS LORE", style = MaterialTheme.typography.labelMedium)
                    }
                }
            )
        }

        if (selectedTab == 0) {
            // Figures Shelf Content
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Shelf Summary Metrics
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkSpaceCard)
                            .border(1.dp, DarkSpaceBorder, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "ESTANTE DE ACTION FIGURES DO LUKE",
                                style = MaterialTheme.typography.labelMedium,
                                color = SaiyanGold,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ShelfMetricPill(
                                    label = "TOTAL",
                                    value = "${figures.size}",
                                    color = SaiyanGold,
                                    modifier = Modifier.weight(1f)
                                )
                                ShelfMetricPill(
                                    label = "EXPOSTOS",
                                    value = "${figures.count { it.status.contains("Exposto", ignoreCase = true) }}",
                                    color = ScouterGreen,
                                    modifier = Modifier.weight(1f)
                                )
                                ShelfMetricPill(
                                    label = "WISHLIST",
                                    value = "${figures.count { it.status.contains("Wishlist", ignoreCase = true) }}",
                                    color = CapsuleCyan,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Filter Row
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(statusFilters) { status ->
                            val isSelected = filterStatus == status
                            FilterChip(
                                selected = isSelected,
                                onClick = { filterStatus = status },
                                label = {
                                    Text(
                                        text = status,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontSize = 11.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SaiyanGold,
                                    selectedLabelColor = DarkSpaceBg,
                                    containerColor = DarkSpaceCard,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }
                }

                if (filteredFigures.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Inventory2,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Nenhuma miniatura cadastrada neste filtro.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Abra a aba 'Radar Z' e toque no ícone de marcador para salvar!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SaiyanGold
                                )
                            }
                        }
                    }
                } else {
                    items(filteredFigures, key = { it.id }) { figure ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(DarkSpaceCard)
                                .border(1.dp, DarkSpaceBorder, RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SubcomposeAsyncImage(
                                    model = figure.image,
                                    contentDescription = figure.name,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(DarkSpaceSurface)
                                        .padding(6.dp),
                                    contentScale = ContentScale.Fit
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = figure.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary,
                                        fontSize = 15.sp
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                when {
                                                    figure.status.contains("Exposto", ignoreCase = true) -> ScouterGreen.copy(alpha = 0.2f)
                                                    figure.status.contains("Caixa", ignoreCase = true) -> CapsuleCyan.copy(alpha = 0.2f)
                                                    else -> KiFlameOrange.copy(alpha = 0.2f)
                                                }
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = figure.status,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = when {
                                                figure.status.contains("Exposto", ignoreCase = true) -> ScouterGreen
                                                figure.status.contains("Caixa", ignoreCase = true) -> CapsuleCyan
                                                else -> KiFlameOrange
                                            },
                                            fontSize = 9.sp
                                        )
                                    }

                                    if (figure.customNote.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = figure.customNote,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            fontSize = 11.sp,
                                            maxLines = 2
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row {
                                        repeat(figure.rating) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = SaiyanGold,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    TextButton(onClick = { editingFigure = figure }) {
                                        Text(
                                            text = "Editar",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CapsuleCyan
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteFigure(figure) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remover",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Planets Lore Explorer
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (val pState = planetsState) {
                    is UiState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = SaiyanGold)
                            }
                        }
                    }
                    is UiState.Error -> {
                        item {
                            Text(
                                text = "Erro ao carregar planetas da lore.",
                                color = KiFlameOrange,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    is UiState.Success -> {
                        items(pState.data, key = { it.id }) { planet ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(DarkSpaceCard)
                                    .border(1.dp, DarkSpaceBorder, RoundedCornerShape(16.dp))
                                    .padding(14.dp)
                            ) {
                                Row(verticalAlignment = Alignment.Top) {
                                    SubcomposeAsyncImage(
                                        model = planet.image,
                                        contentDescription = planet.name,
                                        modifier = Modifier
                                            .size(90.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(DarkSpaceSurface),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = planet.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = TextPrimary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = if (planet.isDestroyed == true) "STATUS: DESTRUÍDO" else "STATUS: EM ÓRBITA / HABITADO",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (planet.isDestroyed == true) KiFlameOrange else ScouterGreen,
                                            fontSize = 9.sp
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = planet.description ?: "Sem dados astronômicos registrados.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            fontSize = 11.sp,
                                            lineHeight = 16.sp
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

    // Edit Figure Dialog
    editingFigure?.let { figure ->
        var editStatus by remember { mutableStateOf(figure.status) }
        var editNote by remember { mutableStateOf(figure.customNote) }

        AlertDialog(
            onDismissRequest = { editingFigure = null },
            title = {
                Text(
                    text = "Editar ${figure.name}",
                    style = MaterialTheme.typography.titleMedium,
                    color = SaiyanGold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Status da Miniatura:",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    listOf("Exposto no Estúdio", "Na Caixa Original", "Wishlist (Desejo)").forEach { st ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            FilterChip(
                                selected = editStatus == st,
                                onClick = { editStatus = st },
                                label = { Text(st, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SaiyanGold,
                                    selectedLabelColor = DarkSpaceBg
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editNote,
                        onValueChange = { editNote = it },
                        label = { Text("Anotações do Colecionador", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaiyanGold,
                            unfocusedBorderColor = DarkSpaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateFigureStatus(figure, editStatus, editNote)
                        editingFigure = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaiyanGold)
                ) {
                    Text("Salvar", color = DarkSpaceBg)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingFigure = null }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = DarkSpaceSurface
        )
    }
}

@Composable
fun ShelfMetricPill(label: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(DarkSpaceSurface)
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = color,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 9.sp
            )
        }
    }
}
