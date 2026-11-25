package com.example.domain.usecase

import com.example.data.model.CharacterDto
import com.example.domain.model.BattleResult
import kotlin.random.Random

class SimulateBattleUseCase {

    private val debateQuotes = listOf(
        "Mano, numa mesa de bar eu defendo até o fim que técnica supera força bruta aqui!",
        "Lembra na saga Cell? Treino na Sala do Tempo define totalmente essa luta!",
        "Com a animação no estilo Naotoshi Shida essa luta quebraria a internet!",
        "Em Dragon Ball: Sparking ZERO esse matchup é puro reflexo e esquiva de vanishing!"
    )

    operator fun invoke(fighter1: CharacterDto, fighter2: CharacterDto): BattleResult {
        val p1 = estimateKiScore(fighter1)
        val p2 = estimateKiScore(fighter2)

        val winner = if (p1 >= p2) fighter1 else fighter2
        val maxScore = maxOf(p1, p2)
        val minScore = minOf(p1, p2).coerceAtLeast(1L)
        val diffPct = (((maxScore - minScore).toDouble() / maxScore) * 100).toInt().coerceIn(2, 98)

        val verdict = if (fighter1.name.contains("Vegeta", ignoreCase = true)) {
            "Vegeta aplica disciplina tática implacável e o orgulho real para superar os limites adversários!"
        } else if (fighter2.name.contains("Vegeta", ignoreCase = true)) {
            "Vegeta demonstra o poder devastador do Príncipe com um Final Flash milimétrico!"
        } else if (p1 > p2) {
            "${fighter1.name} domina o combate através de maior escalonamento de Ki e transformações superiores!"
        } else {
            "${fighter2.name} supera com vantagem de velocidade, técnica milenar e resistência sobre-humana!"
        }

        return BattleResult(
            fighter1 = fighter1,
            fighter2 = fighter2,
            score1 = p1,
            score2 = p2,
            winner = winner,
            differencePct = diffPct,
            analysisVerdict = verdict,
            barDebateQuote = debateQuotes.random()
        )
    }

    fun estimateKiScore(character: CharacterDto): Long {
        var baseScore = 50000L
        val cleanKi = character.ki
            ?.replace(".", "")
            ?.replace(",", "")
            ?.split(" ")
            ?.firstOrNull()
            ?.toLongOrNull()

        if (cleanKi != null && cleanKi > 0) {
            baseScore += cleanKi.coerceAtMost(10000000000L)
        }

        if (character.maxKi?.contains("Septillion", ignoreCase = true) == true) {
            baseScore += 50000000000L
        } else if (character.maxKi?.contains("Billion", ignoreCase = true) == true) {
            baseScore += 5000000000L
        } else if (character.maxKi?.contains("Trillion", ignoreCase = true) == true) {
            baseScore += 15000000000L
        }

        if (character.race.equals("Saiyan", ignoreCase = true)) baseScore += 2000000000L
        if (character.name.contains("Vegeta", ignoreCase = true)) baseScore += 3500000000L
        if (character.name.contains("Goku", ignoreCase = true)) baseScore += 3200000000L
        if (character.name.contains("Beerus", ignoreCase = true) || character.name.contains("Whis", ignoreCase = true)) {
            baseScore += 80000000000L
        }

        return baseScore + Random.nextLong(10000, 50000)
    }
}
