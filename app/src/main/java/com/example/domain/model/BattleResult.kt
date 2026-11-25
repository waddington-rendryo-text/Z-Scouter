package com.example.domain.model

import com.example.data.model.CharacterDto

data class BattleResult(
    val fighter1: CharacterDto,
    val fighter2: CharacterDto,
    val score1: Long,
    val score2: Long,
    val winner: CharacterDto,
    val differencePct: Int,
    val analysisVerdict: String,
    val barDebateQuote: String
)
