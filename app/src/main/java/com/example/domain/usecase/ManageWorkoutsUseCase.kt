package com.example.domain.usecase

import com.example.domain.model.WorkoutLog
import com.example.domain.repository.DragonBallRepository
import kotlinx.coroutines.flow.Flow

class ManageWorkoutsUseCase(
    private val repository: DragonBallRepository,
    private val calculateSaiyanRankUseCase: CalculateSaiyanRankUseCase
) {
    fun observeWorkouts(): Flow<List<WorkoutLog>> = repository.workoutsFlow

    fun observeTotalKiGained(): Flow<Long?> = repository.totalKiGainedFlow

    suspend fun logWorkout(
        exerciseName: String,
        sets: Int,
        reps: Int,
        weightKg: Double,
        notes: String
    ) {
        val safeSets = sets.coerceIn(1, 50)
        val safeReps = reps.coerceIn(1, 200)
        val safeWeight = weightKg.coerceIn(0.0, 1000.0)

        val kiGained = calculateSaiyanRankUseCase.calculateWorkoutKi(
            exerciseName = exerciseName,
            sets = safeSets,
            reps = safeReps,
            weightKg = safeWeight
        )

        repository.logWorkout(
            WorkoutLog(
                exerciseName = exerciseName.trim(),
                sets = safeSets,
                reps = safeReps,
                weightKg = safeWeight,
                kiGained = kiGained,
                notes = notes.trim()
            )
        )
    }

    suspend fun deleteWorkout(workout: WorkoutLog) {
        repository.deleteWorkout(workout)
    }
}
