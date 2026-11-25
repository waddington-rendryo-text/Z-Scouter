package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import com.example.domain.model.CharacterDetail
import com.example.ui.theme.CapsuleCyan
import com.example.ui.theme.DarkSpaceBorder
import com.example.ui.theme.DarkSpaceCard
import com.example.ui.theme.DarkSpaceSurface
import com.example.ui.theme.KiFlameOrange
import com.example.ui.theme.SaiyanGold
import com.example.ui.theme.ScouterGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CharacterDetailDialog(
    character: CharacterDetail,
    isInCollection: Boolean,
    onDismiss: () -> Unit,
    onToggleCollection: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, SaiyanGold.copy(alpha = 0.7f), RoundedCornerShape(24.dp)),
            color = DarkSpaceSurface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar with Close Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(ScouterGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FICHA SCOUTER",
                            style = MaterialTheme.typography.labelMedium,
                            color = ScouterGreen
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("close_detail_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = TextSecondary
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    // Character Visual Hero
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            DarkSpaceCard,
                                            Color(0xFF090D14)
                                        )
                                    )
                                ),
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

                    // Title & Badges
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
                            character.race?.let { race ->
                                DetailBadge(text = race, color = KiFlameOrange)
                            }
                            character.affiliation?.let { affiliation ->
                                DetailBadge(text = affiliation, color = CapsuleCyan)
                            }
                            character.gender?.let { gender ->
                                DetailBadge(text = gender, color = TextSecondary)
                            }
                        }
                    }

                    // Ki & Max Ki Metrics
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

                    // Bio / Description
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

                    // Origin Planet
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

                    // Transformations List
                    item {
                        val forms = character.transformations.orEmpty()
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

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
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

                // Bottom Action for Luke's Figure Shelf
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSpaceSurface)
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = onToggleCollection,
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

@Composable
fun DetailBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontSize = 10.sp
        )
    }
}

@Composable
fun MetricBox(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSpaceCard)
            .border(1.dp, DarkSpaceBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                fontSize = 9.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = color,
                fontSize = 13.sp
            )
        }
    }
}
