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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.example.ui.components.DetailBadge
import com.example.ui.components.MetricBox
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
fun CharacterDetailScreen(
    characterId: Int,
    viewModel: ZScouterViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(characterId) {
        viewModel.loadCharacterDetail(characterId)
    }

    val detailState by viewModel.characterDetailState.collectAsStateWithLifecycle()
    val figures by viewModel.figures.collectAsStateWithLifecycle()
    val isInCollection = figures.any { it.characterId == characterId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkSpaceBg)
    ) {
        // Top Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSpaceSurface)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("detail_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar ao Radar Z",
                        tint = SaiyanGold
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "FICHA SCOUTER #$characterId",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(ScouterGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SCANNER",
                    style = MaterialTheme.typography.labelMedium,
                    color = ScouterGreen,
                    fontSize = 10.sp
                )
            }
        }

        when (val state = detailState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = SaiyanGold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Lendo nível de Ki no Scouter...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkSpaceCard)
                            .border(1.dp, KiFlameOrange.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Erro ao ler ficha do guerreiro",
                            style = MaterialTheme.typography.titleMedium,
                            color = KiFlameOrange
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadCharacterDetail(characterId) },
                            colors = ButtonDefaults.buttonColors(containerColor = SaiyanGold),
                            modifier = Modifier.testTag("retry_detail_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = DarkSpaceBg)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tentar Novamente", color = DarkSpaceBg)
                        }
                    }
                }
            }

            is UiState.Success -> {
                val character = state.data
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(270.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(DarkSpaceCard, Color(0xFF090D14))
                                        )
                                    )
                                    .border(1.dp, DarkSpaceBorder, RoundedCornerShape(18.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                SubcomposeAsyncImage(
                                    model = character.image,
                                    contentDescription = character.name,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    loading = {
                                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(color = SaiyanGold)
                                        }
                                    }
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = character.name,
                                style = MaterialTheme.typography.displayMedium,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                character.race?.let { DetailBadge(text = it, color = KiFlameOrange) }
                                character.affiliation?.let { DetailBadge(text = it, color = CapsuleCyan) }
                                character.gender?.let { DetailBadge(text = it, color = TextSecondary) }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                MetricBox(
                                    label = "KI BASE",
                                    value = character.ki ?: "Desconhecido",
                                    color = SaiyanGold,
                                    modifier = Modifier.weight(1f)
                                )
                                MetricBox(
                                    label = "MAX KI REGISTRADO",
                                    value = character.maxKi ?: "Desconhecido",
                                    color = KiFlameOrange,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "BIOGRAFIA & LORE",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = character.description ?: "Sem dados biográficos registrados no Scouter.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                lineHeight = 20.sp
                            )
                        }

                        item {
                            character.originPlanet?.let { planet ->
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "PLANETA DE ORIGEM",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(DarkSpaceCard)
                                        .border(1.dp, DarkSpaceBorder, RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Public,
                                            contentDescription = null,
                                            tint = CapsuleCyan,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = planet.name ?: "Desconhecido",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = if (planet.isDestroyed == true) "Status: Destruído" else "Status: Intacto",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (planet.isDestroyed == true) KiFlameOrange else ScouterGreen
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            val forms = character.transformations
                            if (forms.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(20.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = SaiyanGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "TRANSFORMAÇÕES (${forms.size})",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = SaiyanGold
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))

                                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    items(forms) { form ->
                                        Box(
                                            modifier = Modifier
                                                .width(130.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(DarkSpaceCard)
                                                .border(1.dp, DarkSpaceBorder, RoundedCornerShape(12.dp))
                                                .padding(8.dp)
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                SubcomposeAsyncImage(
                                                    model = form.image,
                                                    contentDescription = form.name,
                                                    contentScale = ContentScale.Fit,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(100.dp)
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = form.name,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = TextPrimary,
                                                    fontSize = 10.sp,
                                                    maxLines = 1
                                                )
                                                Text(
                                                    text = "Ki: ${form.ki ?: "Elevado"}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = SaiyanGold,
                                                    fontSize = 9.sp,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkSpaceSurface)
                            .padding(16.dp)
                    ) {
                        Button(
                            onClick = { viewModel.toggleFigureCollection(character.toBasicCharacter()) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("toggle_figure_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isInCollection) DarkSpaceCard else SaiyanGold,
                                contentColor = if (isInCollection) SaiyanGold else DarkSpaceSurface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = if (isInCollection) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isInCollection) "REMOVER DA PRATELEIRA" else "ADICIONAR À PRATELEIRA (FIGURE)",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}
