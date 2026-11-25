package com.example.domain.usecase

import com.example.domain.model.ActionFigure
import com.example.domain.model.Character
import com.example.domain.model.FigureStatus
import com.example.domain.repository.DragonBallRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ManageFiguresUseCase(
    private val repository: DragonBallRepository
) {
    fun observeFigures(): Flow<List<ActionFigure>> = repository.figuresFlow

    suspend fun toggleFigure(
        character: Character,
        currentFigures: List<ActionFigure>,
        defaultStatus: FigureStatus = FigureStatus.EXPOSED
    ) {
        val existing = currentFigures.firstOrNull { it.characterId == character.id }
        if (existing != null) {
            repository.removeFigure(existing)
        } else {
            repository.addFigure(
                ActionFigure(
                    characterId = character.id,
                    name = character.name,
                    race = character.race ?: "Desconhecida",
                    image = character.image ?: "",
                    status = defaultStatus,
                    rating = if (character.isPrinceVegeta) 5 else 4,
                    customNote = "Miniatura colecionável SH Figuarts do Luke"
                )
            )
        }
    }

    suspend fun updateFigure(figure: ActionFigure, newStatus: FigureStatus, newNote: String) {
        repository.addFigure(
            figure.copy(
                status = newStatus,
                customNote = newNote.trim()
            )
        )
    }

    suspend fun deleteFigure(figure: ActionFigure) {
        repository.removeFigure(figure)
    }

    suspend fun seedInitialCollectionIfEmpty() {
        val currentFigures = repository.figuresFlow.first()
        if (currentFigures.isEmpty()) {
            repository.addFigure(
                ActionFigure(
                    characterId = 2,
                    name = "Vegeta",
                    race = "Saiyan",
                    image = "https://dragonball-api.com/characters/vegeta_normal.webp",
                    status = FigureStatus.EXPOSED,
                    rating = 5,
                    customNote = "SH Figuarts Super Saiyan Blue - Destaque principal da estante"
                )
            )
            repository.addFigure(
                ActionFigure(
                    characterId = 1,
                    name = "Goku",
                    race = "Saiyan",
                    image = "https://dragonball-api.com/characters/goku_normal.webp",
                    status = FigureStatus.EXPOSED,
                    rating = 5,
                    customNote = "SH Figuarts S.H.F Son Goku - Edição Clássica com Esfera de 4 Estrelas"
                )
            )
        }
    }
}
