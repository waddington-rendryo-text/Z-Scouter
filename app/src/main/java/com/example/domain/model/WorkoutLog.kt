package com.example.domain.model

data class WorkoutLog(
    val id: Int = 0,
    val exerciseName: String,
    val sets: Int,
    val reps: Int,
    val weightKg: Double,
    val kiGained: Long,
    val date: Long = System.currentTimeMillis(),
    val notes: String = ""
)

data class SaiyanRank(
    val title: String,
    val minKi: Long,
    val nextTierKi: Long
)
