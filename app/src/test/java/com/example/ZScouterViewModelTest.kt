package com.example

import com.example.domain.model.Character
import com.example.domain.model.CharacterDetail
import com.example.domain.model.Planet
import com.example.domain.usecase.CalculateSaiyanRankUseCase
import com.example.domain.usecase.FilterCharactersUseCase
import com.example.domain.usecase.GetCharacterDetailUseCase
import com.example.domain.usecase.GetCharactersUseCase
import com.example.domain.usecase.GetPlanetsUseCase
import com.example.domain.usecase.ManageFiguresUseCase
import com.example.domain.usecase.ManageWorkoutsUseCase
import com.example.domain.usecase.SimulateBattleUseCase
import com.example.ui.viewmodel.UiState
import com.example.ui.viewmodel.ZScouterViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ZScouterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeDragonBallRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeDragonBallRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): ZScouterViewModel {
        val rankUseCase = CalculateSaiyanRankUseCase()
        return ZScouterViewModel(
            getCharactersUseCase = GetCharactersUseCase(repository),
            getCharacterDetailUseCase = GetCharacterDetailUseCase(repository),
            getPlanetsUseCase = GetPlanetsUseCase(repository),
            filterCharactersUseCase = FilterCharactersUseCase(),
            calculateSaiyanRankUseCase = rankUseCase,
            manageFiguresUseCase = ManageFiguresUseCase(repository),
            manageWorkoutsUseCase = ManageWorkoutsUseCase(repository, rankUseCase),
            simulateBattleUseCase = SimulateBattleUseCase(),
            isOfflineModeFlow = repository.isOfflineMode
        )
    }

    @Test
    fun `initial load populates characters planets default fighters and seed figures`() = runTest {
        val goku = Character(id = 1, name = "Goku", race = "Saiyan", affiliation = "Z Fighter")
        val vegeta = Character(id = 2, name = "Vegeta", race = "Saiyan", affiliation = "Z Fighter")
        repository.charactersResult = Result.success(listOf(goku, vegeta))
        repository.planetsResult = Result.success(listOf(Planet(id = 1, name = "Earth")))

        val viewModel = createViewModel()
        advanceUntilIdle()

        val charsState = viewModel.charactersState.value
        assertTrue(charsState is UiState.Success)
        assertEquals(2, (charsState as UiState.Success).data.size)

        val planetsState = viewModel.planetsState.value
        assertTrue(planetsState is UiState.Success)
        assertEquals("Earth", (planetsState as UiState.Success).data.first().name)

        assertEquals("Vegeta", viewModel.battleFighter1.value?.name)
        assertEquals("Goku", viewModel.battleFighter2.value?.name)
        assertEquals(2, repository.figuresStore.value.size)
    }

    @Test
    fun `loadCharacterDetail handles success fallback and error states`() = runTest {
        val vegeta = Character(id = 2, name = "Vegeta", race = "Saiyan", description = "Príncipe")
        repository.charactersResult = Result.success(listOf(vegeta))
        repository.detailResultById[2] = Result.success(
            CharacterDetail(id = 2, name = "Vegeta", race = "Saiyan", description = "Detalhe completo")
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        // 1. Success from detail endpoint
        viewModel.loadCharacterDetail(2)
        advanceUntilIdle()
        val successState = viewModel.characterDetailState.value
        assertTrue(successState is UiState.Success)
        assertEquals("Detalhe completo", (successState as UiState.Success).data.description)

        // 2. Fallback to cached character when detail fails
        repository.detailResultById.remove(2)
        viewModel.loadCharacterDetail(2)
        advanceUntilIdle()
        val fallbackState = viewModel.characterDetailState.value
        assertTrue(fallbackState is UiState.Success)
        assertEquals("Príncipe", (fallbackState as UiState.Success).data.description)

        // 3. Error when character does not exist anywhere
        viewModel.loadCharacterDetail(999)
        advanceUntilIdle()
        assertTrue(viewModel.characterDetailState.value is UiState.Error)
    }

    @Test
    fun `logWorkout updates totalKi and runBattleSimulation produces result`() = runTest {
        val goku = Character(id = 1, name = "Goku", ki = "60.000.000", race = "Saiyan")
        val vegeta = Character(id = 2, name = "Vegeta", ki = "54.000.000", race = "Saiyan")
        repository.charactersResult = Result.success(listOf(goku, vegeta))

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.logWorkout("Supino Sayajin", sets = 4, reps = 10, weightKg = 50.0, notes = "Foco")
        advanceUntilIdle()

        assertEquals(26_500L, repository.workoutsStore.value.first().kiGained)
        assertEquals(9001L + 26_500L, CalculateSaiyanRankUseCase().calculateTotalKi(repository.totalKiGainedFlow.first()))

        viewModel.runBattleSimulation()
        advanceUntilIdle()
        val result = viewModel.battleResult.value
        assertEquals(false, viewModel.isSimulatingBattle.value)
        assertTrue(result != null)
    }
}
