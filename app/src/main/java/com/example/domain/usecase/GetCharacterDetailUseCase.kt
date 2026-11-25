package com.example.domain.usecase

import com.example.domain.model.CharacterDetail
import com.example.domain.repository.DragonBallRepository

class GetCharacterDetailUseCase(
    private val repository: DragonBallRepository
) {
    suspend operator fun invoke(characterId: Int): Result<CharacterDetail> {
        return repository.getCharacterDetail(characterId)
    }
}
