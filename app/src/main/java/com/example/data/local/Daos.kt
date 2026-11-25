package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FigureDao {
    @Query("SELECT * FROM figures ORDER BY addedDate DESC")
    fun getAllFigures(): Flow<List<FigureEntity>>

    @Query("SELECT * FROM figures WHERE characterId = :characterId LIMIT 1")
    suspend fun getFigureByCharacterId(characterId: Int): FigureEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFigure(figure: FigureEntity)

    @Update
    suspend fun updateFigure(figure: FigureEntity)

    @Delete
    suspend fun deleteFigure(figure: FigureEntity)

    @Query("DELETE FROM figures WHERE characterId = :characterId")
    suspend fun deleteByCharacterId(characterId: Int)
}

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_logs ORDER BY date DESC")
    fun getAllWorkouts(): Flow<List<WorkoutLogEntity>>

    @Query("SELECT SUM(kiGained) FROM workout_logs")
    fun getTotalKiGained(): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutLogEntity)

    @Delete
    suspend fun deleteWorkout(workout: WorkoutLogEntity)
}
