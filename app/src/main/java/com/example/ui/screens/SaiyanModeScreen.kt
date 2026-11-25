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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ScouterHudCard
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
import com.example.ui.viewmodel.ZScouterViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaiyanModeScreen(
    viewModel: ZScouterViewModel,
    modifier: Modifier = Modifier
) {
    val totalKiGained by viewModel.totalKiGained.collectAsStateWithLifecycle()
    val currentKi = 9001L + (totalKiGained ?: 0L)
    val saiyanRank = viewModel.getSaiyanRankTitle(currentKi)
    val currentQuote by viewModel.currentQuote.collectAsStateWithLifecycle()
    val workouts by viewModel.workouts.collectAsStateWithLifecycle()

    val exercisePresets = listOf(
        "Supino na Câmara de Gravidade 100x",
        "Agachamento Super Saiyajin",
        "Elevação Lateral / Final Flash",
        "Barra Fixa do Templo Sagrado",
        "Cardio da Nuvem Voadora",
        "Treino de Pernas dos Deuses"
    )

    var expandedDropdown by remember { mutableStateOf(false) }
    var selectedExercise by remember { mutableStateOf(exercisePresets[0]) }
    var setsInput by remember { mutableStateOf("4") }
    var repsInput by remember { mutableStateOf("10") }
    var weightInput by remember { mutableStateOf("70") }
    var notesInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkSpaceBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Scouter HUD Banner
        item {
            ScouterHudCard(
                currentKi = currentKi,
                rankTitle = saiyanRank
            )
        }

        // Daily Motivation: Prince Vegeta & Brazilian Dubbing Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSpaceCard)
                    .border(1.dp, KiFlameOrange.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = null,
                                tint = KiFlameOrange,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MOTIVAÇÃO DO PRÍNCIPE VEGETA",
                                style = MaterialTheme.typography.labelMedium,
                                color = KiFlameOrange,
                                fontSize = 10.sp
                            )
                        }

                        IconButton(
                            onClick = { viewModel.randomizeQuote() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Trocar Frase",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "\"$currentQuote\"",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Voz da Dublagem Clássica BR • Foco Total na Academia",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Workout Logging Form
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSpaceSurface)
                    .border(1.dp, DarkSpaceBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = SaiyanGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "REGISTRAR TREINO SAIYAJIN",
                            style = MaterialTheme.typography.titleMedium,
                            color = SaiyanGold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Exercise Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedDropdown,
                        onExpandedChange = { expandedDropdown = !expandedDropdown }
                    ) {
                        OutlinedTextField(
                            value = selectedExercise,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Exercício / Desafio de Força", color = TextSecondary) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SaiyanGold,
                                unfocusedBorderColor = DarkSpaceBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = DarkSpaceCard,
                                unfocusedContainerColor = DarkSpaceCard
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false },
                            modifier = Modifier.background(DarkSpaceCard)
                        ) {
                            exercisePresets.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item, color = TextPrimary, fontSize = 13.sp) },
                                    onClick = {
                                        selectedExercise = item
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sets, Reps, Weight in Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = setsInput,
                            onValueChange = { setsInput = it },
                            label = { Text("Séries", color = TextSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SaiyanGold,
                                unfocusedBorderColor = DarkSpaceBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = DarkSpaceCard,
                                unfocusedContainerColor = DarkSpaceCard
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = repsInput,
                            onValueChange = { repsInput = it },
                            label = { Text("Reps", color = TextSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SaiyanGold,
                                unfocusedBorderColor = DarkSpaceBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = DarkSpaceCard,
                                unfocusedContainerColor = DarkSpaceCard
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = weightInput,
                            onValueChange = { weightInput = it },
                            label = { Text("Carga (kg)", color = TextSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1.2f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SaiyanGold,
                                unfocusedBorderColor = DarkSpaceBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = DarkSpaceCard,
                                unfocusedContainerColor = DarkSpaceCard
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = notesInput,
                        onValueChange = { notesInput = it },
                        label = { Text("Sensação do Treino / Observações", color = TextSecondary) },
                        placeholder = { Text("Ex: Fui além da falha!", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaiyanGold,
                            unfocusedBorderColor = DarkSpaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = DarkSpaceCard,
                            unfocusedContainerColor = DarkSpaceCard
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val sets = setsInput.toIntOrNull() ?: 4
                            val reps = repsInput.toIntOrNull() ?: 10
                            val weight = weightInput.toDoubleOrNull() ?: 50.0
                            viewModel.logWorkout(
                                exerciseName = selectedExercise,
                                sets = sets,
                                reps = reps,
                                weightKg = weight,
                                notes = notesInput
                            )
                            notesInput = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("log_workout_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = SaiyanGold),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = DarkSpaceBg)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ELEVAR KI DE GUERREIRO (+KI)",
                            style = MaterialTheme.typography.labelLarge,
                            color = DarkSpaceBg
                        )
                    }
                }
            }
        }

        // Training History Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HISTÓRICO NA SALA DO TEMPO (${workouts.size})",
                    style = MaterialTheme.typography.labelMedium,
                    color = CapsuleCyan,
                    fontSize = 11.sp
                )
                Text(
                    text = "MAIS FORTE A CADA DIA",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                    fontSize = 9.sp
                )
            }
        }

        // Training History Items
        if (workouts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum treino registrado ainda. Comece a elevar seu Ki!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        } else {
            items(workouts, key = { it.id }) { log ->
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val formattedDate = dateFormat.format(Date(log.date))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSpaceCard)
                        .border(1.dp, DarkSpaceBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = log.exerciseName,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${log.sets} séries x ${log.reps} reps • ${log.weightKg} kg",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            if (log.notes.isNotBlank()) {
                                Text(
                                    text = "\"${log.notes}\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CapsuleCyan,
                                    fontSize = 10.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 9.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ScouterGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "+${log.kiGained} Ki",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = ScouterGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(
                                onClick = { viewModel.deleteWorkout(log) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Excluir treino",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
