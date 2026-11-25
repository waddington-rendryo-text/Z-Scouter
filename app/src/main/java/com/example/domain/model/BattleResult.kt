package com.example.domain.model

data class BattleResult(
    val fighter1: Character,
    val fighter2: Character,
    val score1: Long,
    val score2: Long,
    val winner: Character,
    val differencePct: Int,
    val analysisVerdict: String,
    val barDebateQuote: String
)
