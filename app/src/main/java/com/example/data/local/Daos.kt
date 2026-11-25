package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters ORDER BY id ASC")
    fun getAllCharacters(): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters ORDER BY id ASC")
    suspend fun getAllCharactersOnce(): List<CharacterEntity>

    @Query("SELECT * FROM characters WHERE id = :id LIMIT 1")
    suspend fun getCharacterById(id: Int): CharacterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<CharacterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity)

    @Query("DELETE FROM characters")
    suspend fun clearCharacters()
}

@Dao
interface PlanetDao {
    @Query("SELECT * FROM planets ORDER BY id ASC")
    fun getAllPlanets(): Flow<List<PlanetEntity>>

    @Query("SELECT * FROM planets ORDER BY id ASC")
    suspend fun getAllPlanetsOnce(): List<PlanetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanets(planets: List<PlanetEntity>)

    @Query("DELETE FROM planets")
    suspend fun clearPlanets()
}

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
