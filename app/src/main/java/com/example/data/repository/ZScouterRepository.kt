package com.example.data.repository

import com.example.data.api.DragonBallApiService
import com.example.data.local.CharacterDao
import com.example.data.local.FigureDao
import com.example.data.local.PlanetDao
import com.example.data.local.WorkoutDao
import com.example.data.mapper.DragonBallMapper
import com.example.domain.model.ActionFigure
import com.example.domain.model.Character
import com.example.domain.model.CharacterDetail
import com.example.domain.model.Planet
import com.example.domain.model.WorkoutLog
import com.example.domain.repository.DragonBallRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ZScouterRepository(
    private val api: DragonBallApiService,
    private val characterDao: CharacterDao,
    private val planetDao: PlanetDao,
    private val figureDao: FigureDao,
    private val workoutDao: WorkoutDao,
    private val mapper: DragonBallMapper
) : DragonBallRepository {

    private val _isOfflineMode = MutableStateFlow(false)
    override val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

    override val charactersFlow: Flow<List<Character>> = characterDao.getAllCharacters()
        .map { entities -> entities.map { mapper.toDomain(it) } }

    override val planetsFlow: Flow<List<Planet>> = planetDao.getAllPlanets()
        .map { entities -> entities.map { mapper.toDomain(it) } }

    override val figuresFlow: Flow<List<ActionFigure>> = figureDao.getAllFigures()
        .map { entities -> entities.map { mapper.toDomain(it) } }

    override val workoutsFlow: Flow<List<WorkoutLog>> = workoutDao.getAllWorkouts()
        .map { entities -> entities.map { mapper.toDomain(it) } }

    override val totalKiGainedFlow: Flow<Long?> = workoutDao.getTotalKiGained()

    override suspend fun getCharacters(): Result<List<Character>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getCharacters(limit = 100)
            val existingById = characterDao.getAllCharactersOnce().associateBy { it.id }
            val entities = response.items.map { dto ->
                mapper.toEntity(dto, existingDetailCache = existingById[dto.id])
            }
            characterDao.insertCharacters(entities)
            _isOfflineMode.value = false
            Result.success(response.items.map { mapper.toDomain(it) })
        } catch (e: Exception) {
            val cached = characterDao.getAllCharactersOnce()
            if (cached.isNotEmpty()) {
                _isOfflineMode.value = true
                Result.success(cached.map { mapper.toDomain(it) })
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getCharacterDetail(id: Int): Result<CharacterDetail> = withContext(Dispatchers.IO) {
        try {
            val detail = api.getCharacterDetail(id)
            characterDao.insertCharacter(mapper.toEntity(detail))
            _isOfflineMode.value = false
            Result.success(mapper.toDomain(detail))
        } catch (e: Exception) {
            val cached = characterDao.getCharacterById(id)
            if (cached != null) {
                _isOfflineMode.value = true
                Result.success(mapper.toCharacterDetailDomain(cached))
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getPlanets(): Result<List<Planet>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getPlanets(limit = 50)
            val entities = response.items.map { mapper.toEntity(it) }
            planetDao.insertPlanets(entities)
            _isOfflineMode.value = false
            Result.success(response.items.map { mapper.toDomain(it) })
        } catch (e: Exception) {
            val cached = planetDao.getAllPlanetsOnce()
            if (cached.isNotEmpty()) {
                _isOfflineMode.value = true
                Result.success(cached.map { mapper.toDomain(it) })
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun isFigureInCollection(characterId: Int): Boolean = withContext(Dispatchers.IO) {
        figureDao.getFigureByCharacterId(characterId) != null
    }

    override suspend fun addFigure(figure: ActionFigure) = withContext(Dispatchers.IO) {
        figureDao.insertFigure(mapper.toEntity(figure))
    }

    override suspend fun removeFigure(figure: ActionFigure) = withContext(Dispatchers.IO) {
        figureDao.deleteFigure(mapper.toEntity(figure))
    }

    override suspend fun removeFigureByCharacterId(characterId: Int) = withContext(Dispatchers.IO) {
        figureDao.deleteByCharacterId(characterId)
    }

    override suspend fun logWorkout(workout: WorkoutLog) = withContext(Dispatchers.IO) {
        workoutDao.insertWorkout(mapper.toEntity(workout))
    }

    override suspend fun deleteWorkout(workout: WorkoutLog) = withContext(Dispatchers.IO) {
        workoutDao.deleteWorkout(mapper.toEntity(workout))
    }
}
