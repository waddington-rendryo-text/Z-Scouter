package com.example.domain.usecase

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

    fun getSaiyanRankTitle(totalKi: Long): String {
        return when {
            totalKi < 15000 -> "Guerreiro Sayajin Novato (Scouter Verde)"
            totalKi < 35000 -> "Sayajin de Elite (Classe Real)"
            totalKi < 75000 -> "Guerreiro Oozaru (Fúria Primordial)"
            totalKi < 150000 -> "Super Sayajin (Aura Dourada)"
            totalKi < 300000 -> "Super Sayajin 2 (Raios de Energia)"
            totalKi < 600000 -> "Super Sayajin 3 (Poder Máximo da Terra)"
            totalKi < 1200000 -> "Super Sayajin Deus (Ki Divino Escarlate)"
            totalKi < 2500000 -> "Super Sayajin Blue (Controle Perfeito)"
            else -> "Instinto / Ego Superior (Lenda do Universo 7)"
        }
    }
}
