package com.example.domain.usecase

import com.example.domain.model.SaiyanRank

class CalculateSaiyanRankUseCase {

    companion object {
        const val BASE_KI = 9001L // "Mais de 8000!"
    }

    fun calculateWorkoutKi(exerciseName: String, sets: Int, reps: Int, weightKg: Double): Long {
        val baseKi = (sets * reps * weightKg.coerceAtLeast(1.0) * 12.0).toLong()
        val bonusVegeta = if (
            exerciseName.contains("Sayajin", ignoreCase = true) ||
            exerciseName.contains("Gravidade", ignoreCase = true)
        ) 2500L else 1000L
        return baseKi + bonusVegeta
    }

    fun calculateTotalKi(totalKiGained: Long?): Long {
        return BASE_KI + (totalKiGained ?: 0L)
    }

    fun getSaiyanRank(totalKi: Long): SaiyanRank {
        return when {
            totalKi < 15_000 -> SaiyanRank("Guerreiro Sayajin Novato (Scouter Verde)", 0L, 15_000L)
            totalKi < 35_000 -> SaiyanRank("Sayajin de Elite (Classe Real)", 15_000L, 35_000L)
            totalKi < 75_000 -> SaiyanRank("Guerreiro Oozaru (Fúria Primordial)", 35_000L, 75_000L)
            totalKi < 150_000 -> SaiyanRank("Super Sayajin (Aura Dourada)", 75_000L, 150_000L)
            totalKi < 300_000 -> SaiyanRank("Super Sayajin 2 (Raios de Energia)", 150_000L, 300_000L)
            totalKi < 600_000 -> SaiyanRank("Super Sayajin 3 (Poder Máximo da Terra)", 300_000L, 600_000L)
            totalKi < 1_200_000 -> SaiyanRank("Super Sayajin Deus (Ki Divino Escarlate)", 600_000L, 1_200_000L)
            totalKi < 2_500_000 -> SaiyanRank("Super Sayajin Blue (Controle Perfeito)", 1_200_000L, 2_500_000L)
            else -> SaiyanRank("Instinto / Ego Superior (Lenda do Universo 7)", 2_500_000L, 5_000_000L)
        }
    }

    fun getSaiyanRankTitle(totalKi: Long): String {
        return getSaiyanRank(totalKi).title
    }
}
