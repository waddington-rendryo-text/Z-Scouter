package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.api.DragonBallApiService
import com.example.data.local.AppDatabase
import com.example.data.mapper.DragonBallMapper
import com.example.data.model.CharacterDetailDto
import com.example.data.model.CharacterDto
import com.example.data.model.CharactersResponse
import com.example.data.model.OriginPlanetDto
import com.example.data.model.PlanetDto
import com.example.data.model.PlanetsResponse
import com.example.data.model.TransformationDto
import com.example.data.repository.ZScouterRepository
import com.example.domain.model.ActionFigure
import com.example.domain.model.FigureStatus
import com.example.domain.model.WorkoutLog
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class OfflineFirstRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var mapper: DragonBallMapper
    private var shouldFailNetwork = false

    private val fakeApi = object : DragonBallApiService {
        override suspend fun getCharacters(limit: Int): CharactersResponse {
            if (shouldFailNetwork) throw IOException("Sem conexão")
            return CharactersResponse(
                items = listOf(
                    CharacterDto(id = 2, name = "Vegeta", ki = "54.000.000", race = "Saiyan")
                )
            )
        }

        override suspend fun getCharacterDetail(id: Int): CharacterDetailDto {
            if (shouldFailNetwork) throw IOException("Sem conexão")
            return CharacterDetailDto(
                id = 2,
                name = "Vegeta",
                ki = "54.000.000",
                race = "Saiyan",
                originPlanet = OriginPlanetDto(id = 3, name = "Vegeta", isDestroyed = true),
                transformations = listOf(TransformationDto(id = 7, name = "Vegeta SSJ", ki = "330.000.000"))
            )
        }

        override suspend fun getPlanets(limit: Int): PlanetsResponse {
            if (shouldFailNetwork) throw IOException("Sem conexão")
            return PlanetsResponse(
                items = listOf(PlanetDto(id = 3, name = "Vegeta", isDestroyed = true))
            )
        }
    }

    private lateinit var repository: ZScouterRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        mapper = DragonBallMapper(moshi)
        repository = ZScouterRepository(
            api = fakeApi,
            characterDao = db.characterDao(),
            planetDao = db.planetDao(),
            figureDao = db.figureDao(),
            workoutDao = db.workoutDao(),
            mapper = mapper
        )
    }

    @After
    fun tearDown() {
        db.close()
        stopKoin()
    }

    @Test
    fun `offline first caches api responses in room and serves cache when offline`() = runTest {
        // 1. Online sync populates Room
        shouldFailNetwork = false
        val onlineChars = repository.getCharacters()
        val onlineDetail = repository.getCharacterDetail(2)
        val onlinePlanets = repository.getPlanets()

        assertTrue(onlineChars.isSuccess)
        assertTrue(onlineDetail.isSuccess)
        assertTrue(onlinePlanets.isSuccess)
        assertFalse(repository.isOfflineMode.value)

        // 2. Simulate offline mode
        shouldFailNetwork = true
        val offlineChars = repository.getCharacters()
        val offlineDetail = repository.getCharacterDetail(2)
        val offlinePlanets = repository.getPlanets()

        assertTrue(offlineChars.isSuccess)
        assertEquals("Vegeta", offlineChars.getOrNull()?.first()?.name)

        assertTrue(offlineDetail.isSuccess)
        assertEquals("Vegeta", offlineDetail.getOrNull()?.originPlanet?.name)
        assertEquals("Vegeta SSJ", offlineDetail.getOrNull()?.transformations?.firstOrNull()?.name)

        assertTrue(offlinePlanets.isSuccess)
        assertEquals("Vegeta", offlinePlanets.getOrNull()?.first()?.name)
        assertTrue(repository.isOfflineMode.value)
    }

    @Test
    fun `offline request with empty cache returns failure without offline cache flag`() = runTest {
        shouldFailNetwork = true

        val charsResult = repository.getCharacters()
        val detailResult = repository.getCharacterDetail(99)
        val planetsResult = repository.getPlanets()

        assertTrue(charsResult.isFailure)
        assertTrue(detailResult.isFailure)
        assertTrue(planetsResult.isFailure)
        assertFalse(repository.isOfflineMode.value)
    }

    @Test
    fun `local figures and workouts crud update reactive flows in room`() = runTest {
        val figure = ActionFigure(
            characterId = 2,
            name = "Vegeta",
            race = "Saiyan",
            image = "vegeta.webp",
            status = FigureStatus.EXPOSED,
            rating = 5,
            customNote = "SH Figuarts"
        )
        repository.addFigure(figure)
        assertTrue(repository.isFigureInCollection(2))
        val storedFigures = repository.figuresFlow.first()
        assertEquals(1, storedFigures.size)
        assertEquals("Vegeta", storedFigures.first().name)

        repository.removeFigureByCharacterId(2)
        assertFalse(repository.isFigureInCollection(2))
        assertTrue(repository.figuresFlow.first().isEmpty())

        val workout = WorkoutLog(
            exerciseName = "Agachamento Sayajin",
            sets = 4,
            reps = 10,
            weightKg = 100.0,
            kiGained = 50_500L,
            notes = "Série pesada"
        )
        repository.logWorkout(workout)

        val storedWorkouts = repository.workoutsFlow.first()
        assertEquals(1, storedWorkouts.size)
        assertEquals("Agachamento Sayajin", storedWorkouts.first().exerciseName)
        assertEquals(50_500L, repository.totalKiGainedFlow.first())

        repository.deleteWorkout(storedWorkouts.first())
        assertTrue(repository.workoutsFlow.first().isEmpty())
    }
}

