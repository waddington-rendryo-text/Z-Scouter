package com.example.domain.usecase

import com.example.domain.model.Character
import com.example.domain.repository.DragonBallRepository
import kotlinx.coroutines.flow.Flow

class GetCharactersUseCase(
    private val repository: DragonBallRepository
) {
    fun observe(): Flow<List<Character>> = repository.charactersFlow

    suspend operator fun invoke(): Result<List<Character>> = repository.getCharacters()
}
