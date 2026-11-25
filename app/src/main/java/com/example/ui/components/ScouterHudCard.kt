package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CapsuleCyan
import com.example.ui.theme.DarkSpaceBorder
import com.example.ui.theme.DarkSpaceCard
import com.example.ui.theme.KiFlameOrange
import com.example.ui.theme.SaiyanGold
import com.example.ui.theme.ScouterGreen
import com.example.ui.theme.ScouterReticle
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ScouterHudCard(
    currentKi: Long,
    rankTitle: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hudGlow"
    )

    val formattedKi = NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-BR")).format(currentKi)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkSpaceCard,
                        Color(0xFF0F1A24)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        ScouterGreen.copy(alpha = alphaAnim),
                        CapsuleCyan.copy(alpha = 0.8f),
                        SaiyanGold.copy(alpha = alphaAnim)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Radar,
                        contentDescription = "Scouter",
                        tint = ScouterReticle,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SCOUTER HUD v4.0",
                        style = MaterialTheme.typography.labelMedium,
                        color = ScouterReticle
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CapsuleCyan.copy(alpha = 0.15f))
                        .border(1.dp, CapsuleCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "CAPSULE CORP.",
                        style = MaterialTheme.typography.labelMedium,
                        color = CapsuleCyan,
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Ki Level Readout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "PODER DE LUTA (KI)",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                    Text(
                        text = formattedKi,
                        style = MaterialTheme.typography.displayMedium,
                        color = if (currentKi >= 9000) SaiyanGold else ScouterGreen,
                        fontSize = 28.sp
                    )
                }

                // "Mais de 8000!" badge
                if (currentKi >= 8000) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(KiFlameOrange.copy(alpha = 0.2f))
                            .border(1.dp, KiFlameOrange, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Mais de 8000",
                                tint = KiFlameOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "MAIS DE 8000!",
                                style = MaterialTheme.typography.labelMedium,
                                color = KiFlameOrange,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar to next transformation tier
            val progress = ((currentKi % 50000).toFloat() / 50000f).coerceIn(0.1f, 1.0f)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = SaiyanGold,
                trackColor = DarkSpaceBorder
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Current Class / Rank
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STATUS ATUAL:",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                    fontSize = 10.sp
                )
                Text(
                    text = rankTitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary,
                    fontSize = 11.sp
                )
            }
        }
    }
}
