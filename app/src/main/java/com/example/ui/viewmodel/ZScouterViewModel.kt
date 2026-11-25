package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.ActionFigure
import com.example.domain.model.BattleResult
import com.example.domain.model.Character
import com.example.domain.model.CharacterDetail
import com.example.domain.model.FigureStatus
import com.example.domain.model.Planet
import com.example.domain.model.WorkoutLog
import com.example.domain.usecase.CalculateSaiyanRankUseCase
import com.example.domain.usecase.FilterCharactersUseCase
import com.example.domain.usecase.GetCharacterDetailUseCase
import com.example.domain.usecase.GetCharactersUseCase
import com.example.domain.usecase.GetPlanetsUseCase
import com.example.domain.usecase.ManageFiguresUseCase
import com.example.domain.usecase.ManageWorkoutsUseCase
import com.example.domain.usecase.SimulateBattleUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class ZScouterViewModel(
    private val getCharactersUseCase: GetCharactersUseCase,
    private val getCharacterDetailUseCase: GetCharacterDetailUseCase,
    private val getPlanetsUseCase: GetPlanetsUseCase,
    private val filterCharactersUseCase: FilterCharactersUseCase,
    private val calculateSaiyanRankUseCase: CalculateSaiyanRankUseCase,
    private val manageFiguresUseCase: ManageFiguresUseCase,
    private val manageWorkoutsUseCase: ManageWorkoutsUseCase,
    private val simulateBattleUseCase: SimulateBattleUseCase,
    isOfflineModeFlow: StateFlow<Boolean>
) : ViewModel() {

    private val _charactersState = MutableStateFlow<UiState<List<Character>>>(UiState.Loading)
    val charactersState: StateFlow<UiState<List<Character>>> = _charactersState.asStateFlow()

    private val _characterDetailState = MutableStateFlow<UiState<CharacterDetail>>(UiState.Loading)
    val characterDetailState: StateFlow<UiState<CharacterDetail>> = _characterDetailState.asStateFlow()

    private val _allCharacters = MutableStateFlow<List<Character>>(emptyList())

    val searchQuery = MutableStateFlow("")
    val selectedRaceFilter = MutableStateFlow("Todos")
    val selectedAffiliationFilter = MutableStateFlow("Todos")

    val filteredCharacters: StateFlow<List<Character>> = combine(
        _allCharacters,
        searchQuery,
        selectedRaceFilter,
        selectedAffiliationFilter
    ) { list, query, race, affiliation ->
        filterCharactersUseCase(
            characters = list,
            query = query,
            raceFilter = race,
            affiliationFilter = affiliation
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _planetsState = MutableStateFlow<UiState<List<Planet>>>(UiState.Loading)
    val planetsState: StateFlow<UiState<List<Planet>>> = _planetsState.asStateFlow()

    val isOfflineMode: StateFlow<Boolean> = isOfflineModeFlow

    val figures: StateFlow<List<ActionFigure>> = manageFiguresUseCase.observeFigures()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workouts: StateFlow<List<WorkoutLog>> = manageWorkoutsUseCase.observeWorkouts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalKiGained: StateFlow<Long?> = manageWorkoutsUseCase.observeTotalKiGained()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val quotes = listOf(
        "O miserável é um gênio! Não posso ficar para trás nesse treino!",
        "Eu sou o Príncipe dos Saiyajins! Nenhum limite humano pode me deter!",
        "Você pode controlar meu corpo e minha mente, mas nunca terá o meu ORGULHO!",
        "Supere a dor! Um guerreiro de verdade não chora na academia, ele se supera!",
        "Kakarotto pode ter nascido com talento, mas minha disciplina destruirá qualquer obstáculo!",
        "Hoje o treino foi para romper as barreiras do Instinto Superior!",
        "Mais de 8.000 de Ki! O Scouter não aguenta tamanha determinação!",
        "Treinar na câmara de gravidade 100x até atingir a forma perfeita!"
    )
    private val _currentQuote = MutableStateFlow(quotes[0])
    val currentQuote: StateFlow<String> = _currentQuote.asStateFlow()

    val battleFighter1 = MutableStateFlow<Character?>(null)
    val battleFighter2 = MutableStateFlow<Character?>(null)
    val isSimulatingBattle = MutableStateFlow(false)
    val battleResult = MutableStateFlow<BattleResult?>(null)

    init {
        observeCachedData()
        loadCharacters()
        loadPlanets()
        viewModelScope.launch {
            manageFiguresUseCase.seedInitialCollectionIfEmpty()
        }
    }

    private fun observeCachedData() {
        viewModelScope.launch {
            getCharactersUseCase.observe().collect { cached ->
                if (cached.isNotEmpty()) {
                    _allCharacters.value = cached
                    _charactersState.value = UiState.Success(cached)
                    assignDefaultBattleFighters(cached)
                }
            }
        }
        viewModelScope.launch {
            getPlanetsUseCase.observe().collect { cachedPlanets ->
                if (cachedPlanets.isNotEmpty()) {
                    _planetsState.value = UiState.Success(cachedPlanets)
                }
            }
        }
    }

    private fun assignDefaultBattleFighters(list: List<Character>) {
        if (battleFighter1.value == null && list.isNotEmpty()) {
            battleFighter1.value = list.firstOrNull { it.isPrinceVegeta } ?: list.first()
        }
        if (battleFighter2.value == null && list.size > 1) {
            battleFighter2.value = list.firstOrNull { it.name.contains("Goku", ignoreCase = true) } ?: list[1]
        }
    }

    fun loadCharacters() {
        viewModelScope.launch {
            if (_allCharacters.value.isEmpty()) {
                _charactersState.value = UiState.Loading
            }
            getCharactersUseCase()
                .onSuccess { list ->
                    _allCharacters.value = list
                    _charactersState.value = UiState.Success(list)
                    assignDefaultBattleFighters(list)
                }
                .onFailure { error ->
                    if (_allCharacters.value.isEmpty()) {
                        _charactersState.value = UiState.Error(error.localizedMessage ?: "Erro ao carregar guerreiros da API")
                    }
                }
        }
    }

    fun loadPlanets() {
        viewModelScope.launch {
            if (_planetsState.value !is UiState.Success) {
                _planetsState.value = UiState.Loading
            }
            getPlanetsUseCase()
                .onSuccess { list ->
                    _planetsState.value = UiState.Success(list)
                }
                .onFailure { error ->
                    if (_planetsState.value !is UiState.Success) {
                        _planetsState.value = UiState.Error(error.localizedMessage ?: "Erro ao carregar planetas")
                    }
                }
        }
    }

    fun loadCharacterDetail(characterId: Int) {
        viewModelScope.launch {
            _characterDetailState.value = UiState.Loading
            getCharacterDetailUseCase(characterId)
                .onSuccess { detail ->
                    _characterDetailState.value = UiState.Success(detail)
                }
                .onFailure { error ->
                    val fallback = _allCharacters.value.firstOrNull { it.id == characterId }
                    if (fallback != null) {
                        _characterDetailState.value = UiState.Success(
                            CharacterDetail(
                                id = fallback.id,
                                name = fallback.name,
                                ki = fallback.ki,
                                maxKi = fallback.maxKi,
                                race = fallback.race,
                                gender = fallback.gender,
                                description = fallback.description,
                                image = fallback.image,
                                affiliation = fallback.affiliation
                            )
                        )
                    } else {
                        _characterDetailState.value = UiState.Error(
                            error.localizedMessage ?: "Não foi possível carregar os dados deste guerreiro."
                        )
                    }
                }
        }
    }

    fun toggleFigureCollection(character: Character, defaultStatus: FigureStatus = FigureStatus.EXPOSED) {
        viewModelScope.launch {
            manageFiguresUseCase.toggleFigure(character, figures.value, defaultStatus)
        }
    }

    fun updateFigureStatus(figure: ActionFigure, newStatus: FigureStatus, newNote: String) {
        viewModelScope.launch {
            manageFiguresUseCase.updateFigure(figure, newStatus, newNote)
        }
    }

    fun deleteFigure(figure: ActionFigure) {
        viewModelScope.launch {
            manageFiguresUseCase.deleteFigure(figure)
        }
    }

    fun randomizeQuote() {
        _currentQuote.value = quotes.random()
    }

    fun logWorkout(exerciseName: String, sets: Int, reps: Int, weightKg: Double, notes: String) {
        viewModelScope.launch {
            manageWorkoutsUseCase.logWorkout(exerciseName, sets, reps, weightKg, notes)
            randomizeQuote()
        }
    }

    fun deleteWorkout(workout: WorkoutLog) {
        viewModelScope.launch {
            manageWorkoutsUseCase.deleteWorkout(workout)
        }
    }

    fun calculateTotalKi(): Long {
        return calculateSaiyanRankUseCase.calculateTotalKi(totalKiGained.value)
    }

    fun getSaiyanRankTitle(totalKi: Long): String {
        return calculateSaiyanRankUseCase.getSaiyanRankTitle(totalKi)
    }

    fun runBattleSimulation() {
        val f1 = battleFighter1.value ?: return
        val f2 = battleFighter2.value ?: return

        isSimulatingBattle.value = true
        battleResult.value = null

        viewModelScope.launch {
            delay(1200)
            battleResult.value = simulateBattleUseCase(f1, f2)
            isSimulatingBattle.value = false
        }
    }
}
