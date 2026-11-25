package com.example

import com.example.domain.model.ActionFigure
import com.example.domain.model.Character
import com.example.domain.model.CharacterDetail
import com.example.domain.model.FigureStatus
import com.example.domain.model.OriginPlanet
import com.example.domain.model.Planet
import com.example.domain.model.Transformation
import com.example.domain.model.WorkoutLog
import com.example.domain.repository.DragonBallRepository
import com.example.domain.usecase.CalculateSaiyanRankUseCase
import com.example.domain.usecase.FilterCharactersUseCase
import com.example.domain.usecase.GetCharacterDetailUseCase
import com.example.domain.usecase.GetCharactersUseCase
import com.example.domain.usecase.GetPlanetsUseCase
import com.example.domain.usecase.ManageFiguresUseCase
import com.example.domain.usecase.ManageWorkoutsUseCase
import com.example.domain.usecase.SimulateBattleUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeDragonBallRepository : DragonBallRepository {
    val charactersStore = MutableStateFlow<List<Character>>(emptyList())
    val planetsStore = MutableStateFlow<List<Planet>>(emptyList())
    val figuresStore = MutableStateFlow<List<ActionFigure>>(emptyList())
    val workoutsStore = MutableStateFlow<List<WorkoutLog>>(emptyList())
    private val _isOfflineMode = MutableStateFlow(false)

    var charactersResult: Result<List<Character>> = Result.success(emptyList())
    var detailResultById: MutableMap<Int, Result<CharacterDetail>> = mutableMapOf()
    var planetsResult: Result<List<Planet>> = Result.success(emptyList())

    override val charactersFlow: Flow<List<Character>> = charactersStore
    override val planetsFlow: Flow<List<Planet>> = planetsStore
    override val figuresFlow: Flow<List<ActionFigure>> = figuresStore
    override val workoutsFlow: Flow<List<WorkoutLog>> = workoutsStore
    override val totalKiGainedFlow: Flow<Long?> = workoutsStore.map { logs ->
        if (logs.isEmpty()) null else logs.sumOf { it.kiGained }
    }
    override val isOfflineMode: StateFlow<Boolean> = _isOfflineMode

    override suspend fun getCharacters(): Result<List<Character>> = charactersResult.onSuccess {
        charactersStore.value = it
    }

    override suspend fun getCharacterDetail(id: Int): Result<CharacterDetail> {
        return detailResultById[id] ?: Result.failure(NoSuchElementException("Not found: $id"))
    }

    override suspend fun getPlanets(): Result<List<Planet>> = planetsResult.onSuccess {
        planetsStore.value = it
    }

    override suspend fun isFigureInCollection(characterId: Int): Boolean {
        return figuresStore.value.any { it.characterId == characterId }
    }

    override suspend fun addFigure(figure: ActionFigure) {
        val current = figuresStore.value.toMutableList()
        val index = current.indexOfFirst { it.characterId == figure.characterId }
        if (index >= 0) {
            current[index] = figure
        } else {
            current.add(figure)
        }
        figuresStore.value = current
    }

    override suspend fun removeFigure(figure: ActionFigure) {
        figuresStore.value = figuresStore.value.filterNot { it.characterId == figure.characterId }
    }

    override suspend fun removeFigureByCharacterId(characterId: Int) {
        figuresStore.value = figuresStore.value.filterNot { it.characterId == characterId }
    }

    override suspend fun logWorkout(workout: WorkoutLog) {
        val nextId = (workoutsStore.value.maxOfOrNull { it.id } ?: 0) + 1
        workoutsStore.value = listOf(workout.copy(id = nextId)) + workoutsStore.value
    }

    override suspend fun deleteWorkout(workout: WorkoutLog) {
        workoutsStore.value = workoutsStore.value.filterNot { it.id == workout.id }
    }
}

class UseCasesUnitTest {

    private val rankUseCase = CalculateSaiyanRankUseCase()
    private val filterUseCase = FilterCharactersUseCase()
    private val battleUseCase = SimulateBattleUseCase()

    @Test
    fun `calculateSaiyanRank covers all rank tiers and exercise bonuses`() {
        assertEquals(9001L, rankUseCase.calculateTotalKi(null))
        assertEquals(19001L, rankUseCase.calculateTotalKi(10_000L))

        val normalBonus = rankUseCase.calculateWorkoutKi("Agachamento Livre", sets = 3, reps = 10, weightKg = 100.0)
        val gravBonus = rankUseCase.calculateWorkoutKi("Agachamento Gravidade 100x", sets = 3, reps = 10, weightKg = 100.0)
        assertEquals(37_000L, normalBonus)
        assertEquals(38_500L, gravBonus)

        assertEquals("Guerreiro Sayajin Novato (Scouter Verde)", rankUseCase.getSaiyanRankTitle(9_001))
        assertEquals("Sayajin de Elite (Classe Real)", rankUseCase.getSaiyanRankTitle(20_000))
        assertEquals("Guerreiro Oozaru (Fúria Primordial)", rankUseCase.getSaiyanRankTitle(50_000))
        assertEquals("Super Sayajin (Aura Dourada)", rankUseCase.getSaiyanRankTitle(100_000))
        assertEquals("Super Sayajin 2 (Raios de Energia)", rankUseCase.getSaiyanRankTitle(200_000))
        assertEquals("Super Sayajin 3 (Poder Máximo da Terra)", rankUseCase.getSaiyanRankTitle(400_000))
        assertEquals("Super Sayajin Deus (Ki Divino Escarlate)", rankUseCase.getSaiyanRankTitle(900_000))
        assertEquals("Super Sayajin Blue (Controle Perfeito)", rankUseCase.getSaiyanRankTitle(2_000_000))
        assertEquals("Instinto / Ego Superior (Lenda do Universo 7)", rankUseCase.getSaiyanRankTitle(3_000_000))
    }

    @Test
    fun `filterCharacters filters by query description race and affiliation`() {
        val roster = listOf(
            Character(id = 1, name = "Goku", race = "Saiyan", affiliation = "Z Fighter", description = "Guerreiro criado na Terra"),
            Character(id = 2, name = "Vegeta", race = "Saiyan", affiliation = "Z Fighter", description = "Príncipe dos Saiyajins"),
            Character(id = 3, name = "Freeza", race = "Frieza Race", affiliation = "Army of Frieza", description = "Imperador galáctico"),
            Character(id = 4, name = "Android 17", race = "Android", affiliation = "Red Ribbon Army", description = "Guarda florestal"),
            Character(id = 5, name = "Whis", race = "Angel", affiliation = "Assistant of Beerus", description = "Mestre do Instinto Superior")
        )

        assertEquals(5, filterUseCase(roster, "", "Todos", "Todos").size)
        assertEquals(listOf("Vegeta"), filterUseCase(roster, "príncipe", "Todos", "Todos").map { it.name })
        assertEquals(listOf("Freeza"), filterUseCase(roster, "", "Frieza Race", "Exército de Freeza").map { it.name })
        assertEquals(listOf("Android 17"), filterUseCase(roster, "", "Androide", "Red Ribbon").map { it.name })
        assertEquals(listOf("Whis"), filterUseCase(roster, "", "Deuses / Anjos", "Outros").map { it.name })
    }

    @Test
    fun `getCharacters getCharacterDetail and getPlanets delegate to repository and observe flows`() = runTest {
        val repo = FakeDragonBallRepository()
        val sampleChars = listOf(Character(id = 2, name = "Vegeta", race = "Saiyan"))
        val samplePlanets = listOf(Planet(id = 3, name = "Planet Vegeta", isDestroyed = true))
        val detail = CharacterDetail(
            id = 2,
            name = "Vegeta",
            originPlanet = OriginPlanet(id = 3, name = "Planet Vegeta", isDestroyed = true),
            transformations = listOf(Transformation(id = 1, name = "Super Saiyan", ki = "150.000.000"))
        )

        repo.charactersResult = Result.success(sampleChars)
        repo.planetsResult = Result.success(samplePlanets)
        repo.detailResultById[2] = Result.success(detail)

        val getCharacters = GetCharactersUseCase(repo)
        val getDetail = GetCharacterDetailUseCase(repo)
        val getPlanets = GetPlanetsUseCase(repo)

        assertEquals(sampleChars, getCharacters().getOrThrow())
        assertEquals(sampleChars, getCharacters.observe().first())
        assertEquals("Planet Vegeta", getDetail(2).getOrThrow().originPlanet?.name)
        assertTrue(getDetail(999).isFailure)
        assertEquals(samplePlanets, getPlanets().getOrThrow())
        assertEquals(samplePlanets, getPlanets.observe().first())
    }

    @Test
    fun `manageFigures seeds once toggles adds updates and deletes figures`() = runTest {
        val repo = FakeDragonBallRepository()
        val manageFigures = ManageFiguresUseCase(repo)

        // Seed only when empty
        manageFigures.seedInitialCollectionIfEmpty()
        assertEquals(2, repo.figuresStore.value.size)

        // Second seed should be a no-op
        manageFigures.seedInitialCollectionIfEmpty()
        assertEquals(2, repo.figuresStore.value.size)

        // Toggle new figure (Piccolo)
        val piccolo = Character(id = 3, name = "Piccolo", race = "Namekian", image = "piccolo.webp")
        manageFigures.toggleFigure(piccolo, repo.figuresStore.value, FigureStatus.BOXED)
        val addedPiccolo = repo.figuresStore.value.first { it.characterId == 3 }
        assertEquals(FigureStatus.BOXED, addedPiccolo.status)
        assertEquals(4, addedPiccolo.rating)

        // Update figure status and trim note
        manageFigures.updateFigure(addedPiccolo, FigureStatus.WISHLIST, "  Aguardando relançamento SHF  ")
        val updatedPiccolo = repo.figuresStore.value.first { it.characterId == 3 }
        assertEquals(FigureStatus.WISHLIST, updatedPiccolo.status)
        assertEquals("Aguardando relançamento SHF", updatedPiccolo.customNote)

        // Toggle existing figure removes it
        manageFigures.toggleFigure(piccolo, repo.figuresStore.value)
        assertFalse(repo.figuresStore.value.any { it.characterId == 3 })

        // Delete remaining Vegeta figure
        val vegeta = repo.figuresStore.value.first { it.characterId == 2 }
        manageFigures.deleteFigure(vegeta)
        assertFalse(repo.figuresStore.value.any { it.characterId == 2 })
    }

    @Test
    fun `manageWorkouts clamps bounds calculates ki trims text and deletes logs`() = runTest {
        val repo = FakeDragonBallRepository()
        val manageWorkouts = ManageWorkoutsUseCase(repo, rankUseCase)

        manageWorkouts.logWorkout(
            exerciseName = "  Treino Sayajin Intenso  ",
            sets = 99,      // clamped to 50
            reps = 500,     // clamped to 200
            weightKg = -10.0, // clamped to 0.0 -> coerced to 1.0 in calculation
            notes = "  Foco total  "
        )

        val logs = manageWorkouts.observeWorkouts().first()
        assertEquals(1, logs.size)
        val log = logs.first()
        assertEquals("Treino Sayajin Intenso", log.exerciseName)
        assertEquals(50, log.sets)
        assertEquals(200, log.reps)
        assertEquals(0.0, log.weightKg, 0.001)
        assertEquals("Foco total", log.notes)
        assertEquals(122_500L, log.kiGained)
        assertEquals(122_500L, manageWorkouts.observeTotalKiGained().first())

        manageWorkouts.deleteWorkout(log)
        assertTrue(manageWorkouts.observeWorkouts().first().isEmpty())
    }

    @Test
    fun `simulateBattle applies ki heuristics and prince vegeta verdicts`() {
        val beerus = Character(id = 20, name = "Beerus", ki = "100.000.000", maxKi = "10 Septillion", race = "God")
        val krillin = Character(id = 10, name = "Krillin", ki = "1.000.000", maxKi = "2.000.000", race = "Human")
        val vegeta = Character(id = 2, name = "Vegeta", ki = "54.000.000", maxKi = "19.84 Septillion", race = "Saiyan")

        val godVsHuman = battleUseCase(beerus, krillin)
        assertEquals("Beerus", godVsHuman.winner.name)
        assertTrue(godVsHuman.score1 > godVsHuman.score2)
        assertTrue(godVsHuman.differencePct in 2..98)
        assertTrue(godVsHuman.analysisVerdict.contains("Beerus"))

        val vegetaMatch = battleUseCase(krillin, vegeta)
        assertEquals("Vegeta", vegetaMatch.winner.name)
        assertTrue(vegetaMatch.analysisVerdict.contains("Príncipe"))
    }
}
