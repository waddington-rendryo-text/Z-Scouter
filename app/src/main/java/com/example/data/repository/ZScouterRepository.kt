package com.example.data.repository

import com.example.data.api.ApiClient
import com.example.data.api.DragonBallApiService
import com.example.data.local.FigureDao
import com.example.data.local.FigureEntity
import com.example.data.local.WorkoutDao
import com.example.data.local.WorkoutLogEntity
import com.example.data.model.CharacterDetailDto
import com.example.data.model.CharacterDto
import com.example.data.model.PlanetDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ZScouterRepository(
    private val api: DragonBallApiService = ApiClient.apiService,
    private val figureDao: FigureDao,
    private val workoutDao: WorkoutDao
) {
    val figuresFlow: Flow<List<FigureEntity>> = figureDao.getAllFigures()
    val workoutsFlow: Flow<List<WorkoutLogEntity>> = workoutDao.getAllWorkouts()
    val totalKiGainedFlow: Flow<Long?> = workoutDao.getTotalKiGained()

    suspend fun getCharacters(): Result<List<CharacterDto>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getCharacters(limit = 100)
            Result.success(response.items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCharacterDetail(id: Int): Result<CharacterDetailDto> = withContext(Dispatchers.IO) {
        try {
            val detail = api.getCharacterDetail(id)
            Result.success(detail)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPlanets(): Result<List<PlanetDto>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getPlanets(limit = 50)
            Result.success(response.items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isFigureInCollection(characterId: Int): Boolean = withContext(Dispatchers.IO) {
        figureDao.getFigureByCharacterId(characterId) != null
    }

    suspend fun addFigure(figure: FigureEntity) = withContext(Dispatchers.IO) {
        figureDao.insertFigure(figure)
    }

    suspend fun removeFigure(figure: FigureEntity) = withContext(Dispatchers.IO) {
        figureDao.deleteFigure(figure)
    }

    suspend fun removeFigureByCharacterId(characterId: Int) = withContext(Dispatchers.IO) {
        figureDao.deleteByCharacterId(characterId)
    }

    suspend fun logWorkout(workout: WorkoutLogEntity) = withContext(Dispatchers.IO) {
        workoutDao.insertWorkout(workout)
    }

    suspend fun deleteWorkout(workout: WorkoutLogEntity) = withContext(Dispatchers.IO) {
        workoutDao.deleteWorkout(workout)
    }
}
