package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "figures")
data class FigureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val characterId: Int,
    val name: String,
    val race: String,
    val image: String,
    val status: String = "Exposto no Estúdio", // "Exposto no Estúdio", "Na Caixa Original", "Wishlist (Desejo)"
    val rating: Int = 5,
    val customNote: String = "",
    val addedDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "workout_logs")
data class WorkoutLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val exerciseName: String,
    val sets: Int,
    val reps: Int,
    val weightKg: Double,
    val kiGained: Long,
    val date: Long = System.currentTimeMillis(),
    val notes: String = ""
)
