package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.FigureEntity
import com.example.data.local.WorkoutLogEntity
import com.example.data.model.CharacterDetailDto
import com.example.data.model.CharacterDto
import com.example.data.model.PlanetDto
import com.example.domain.model.BattleResult
import com.example.domain.repository.DragonBallRepository
import com.example.domain.usecase.CalculateSaiyanRankUseCase
import com.example.domain.usecase.FilterCharactersUseCase
import com.example.domain.usecase.SimulateBattleUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class ZScouterViewModel(
    private val repository: DragonBallRepository,
    private val filterCharactersUseCase: FilterCharactersUseCase,
    private val calculateSaiyanRankUseCase: CalculateSaiyanRankUseCase,
    private val simulateBattleUseCase: SimulateBattleUseCase
) : ViewModel() {

    // Characters state
    private val _charactersState = MutableStateFlow<UiState<List<CharacterDto>>>(UiState.Loading)
    val charactersState: StateFlow<UiState<List<CharacterDto>>> = _charactersState.asStateFlow()

    private val _allCharacters = MutableStateFlow<List<CharacterDto>>(emptyList())

    val searchQuery = MutableStateFlow("")
    val selectedRaceFilter = MutableStateFlow("Todos")
    val selectedAffiliationFilter = MutableStateFlow("Todos")

    // Filtered characters via Domain UseCase
    val filteredCharacters: StateFlow<List<CharacterDto>> = combine(
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

    // Character Detail
    private val _selectedCharacterDetail = MutableStateFlow<CharacterDetailDto?>(null)
    val selectedCharacterDetail: StateFlow<CharacterDetailDto?> = _selectedCharacterDetail.asStateFlow()

    private val _isLoadingDetail = MutableStateFlow(false)
    val isLoadingDetail: StateFlow<Boolean> = _isLoadingDetail.asStateFlow()

    // Planets
    private val _planetsState = MutableStateFlow<UiState<List<PlanetDto>>>(UiState.Loading)
    val planetsState: StateFlow<UiState<List<PlanetDto>>> = _planetsState.asStateFlow()

    // Offline-First status
    val isOfflineMode: StateFlow<Boolean> = repository.isOfflineMode

    // Figures Collection from Room
    val figures: StateFlow<List<FigureEntity>> = repository.figuresFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Workout logs from Room
    val workouts: StateFlow<List<WorkoutLogEntity>> = repository.workoutsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalKiGained: StateFlow<Long?> = repository.totalKiGainedFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // Motivational Quote
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

    // Battle Simulator State
    val battleFighter1 = MutableStateFlow<CharacterDto?>(null)
    val battleFighter2 = MutableStateFlow<CharacterDto?>(null)
    val isSimulatingBattle = MutableStateFlow(false)
    val battleResult = MutableStateFlow<BattleResult?>(null)

    init {
        observeCachedData()
        loadCharacters()
        loadPlanets()
        seedInitialFiguresIfEmpty()
    }

    private fun observeCachedData() {
        viewModelScope.launch {
            repository.charactersFlow.collect { cached ->
                if (cached.isNotEmpty()) {
                    _allCharacters.value = cached
                    _charactersState.value = UiState.Success(cached)
                    if (battleFighter1.value == null) {
                        battleFighter1.value = cached.firstOrNull { it.name.contains("Vegeta", ignoreCase = true) } ?: cached.first()
                    }
                    if (battleFighter2.value == null && cached.size > 1) {
                        battleFighter2.value = cached.firstOrNull { it.name.contains("Goku", ignoreCase = true) } ?: cached[1]
                    }
                }
            }
        }
        viewModelScope.launch {
            repository.planetsFlow.collect { cachedPlanets ->
                if (cachedPlanets.isNotEmpty()) {
                    _planetsState.value = UiState.Success(cachedPlanets)
                }
            }
        }
    }

    fun loadCharacters() {
        viewModelScope.launch {
            if (_allCharacters.value.isEmpty()) {
                _charactersState.value = UiState.Loading
            }
            repository.getCharacters()
                .onSuccess { list ->
                    _allCharacters.value = list
                    _charactersState.value = UiState.Success(list)
                    if (battleFighter1.value == null && list.isNotEmpty()) {
                        battleFighter1.value = list.firstOrNull { it.name.contains("Vegeta", ignoreCase = true) } ?: list.first()
                    }
                    if (battleFighter2.value == null && list.size > 1) {
                        battleFighter2.value = list.firstOrNull { it.name.contains("Goku", ignoreCase = true) } ?: list[1]
                    }
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
            repository.getPlanets()
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

    fun selectCharacterForDetail(character: CharacterDto) {
        viewModelScope.launch {
            _isLoadingDetail.value = true
            _selectedCharacterDetail.value = null
            repository.getCharacterDetail(character.id)
                .onSuccess { detail ->
                    _selectedCharacterDetail.value = detail
                    _isLoadingDetail.value = false
                }
                .onFailure {
                    _selectedCharacterDetail.value = CharacterDetailDto(
                        id = character.id,
                        name = character.name,
                        ki = character.ki,
                        maxKi = character.maxKi,
                        race = character.race,
                        gender = character.gender,
                        description = character.description,
                        image = character.image,
                        affiliation = character.affiliation
                    )
                    _isLoadingDetail.value = false
                }
        }
    }

    fun closeCharacterDetail() {
        _selectedCharacterDetail.value = null
    }

    fun toggleFigureCollection(character: CharacterDto, defaultStatus: String = "Exposto no Estúdio") {
        viewModelScope.launch {
            val existing = figures.value.firstOrNull { it.characterId == character.id }
            if (existing != null) {
                repository.removeFigure(existing)
            } else {
                repository.addFigure(
                    FigureEntity(
                        characterId = character.id,
                        name = character.name,
                        race = character.race ?: "Desconhecida",
                        image = character.image ?: "",
                        status = defaultStatus,
                        rating = if (character.name.contains("Vegeta", ignoreCase = true)) 5 else 4,
                        customNote = "Miniatura colecionável SH Figuarts do Luke"
                    )
                )
            }
        }
    }

    fun updateFigureStatus(figure: FigureEntity, newStatus: String, newNote: String) {
        viewModelScope.launch {
            repository.addFigure(figure.copy(status = newStatus, customNote = newNote))
        }
    }

    fun deleteFigure(figure: FigureEntity) {
        viewModelScope.launch {
            repository.removeFigure(figure)
        }
    }

    fun randomizeQuote() {
        _currentQuote.value = quotes.random()
    }

    fun logWorkout(exerciseName: String, sets: Int, reps: Int, weightKg: Double, notes: String) {
        viewModelScope.launch {
            val totalKiGained = calculateSaiyanRankUseCase.calculateWorkoutKi(
                exerciseName = exerciseName,
                sets = sets,
                reps = reps,
                weightKg = weightKg
            )

            repository.logWorkout(
                WorkoutLogEntity(
                    exerciseName = exerciseName,
                    sets = sets,
                    reps = reps,
                    weightKg = weightKg,
                    kiGained = totalKiGained,
                    notes = notes
                )
            )
            randomizeQuote()
        }
    }

    fun deleteWorkout(workout: WorkoutLogEntity) {
        viewModelScope.launch {
            repository.deleteWorkout(workout)
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

    private fun seedInitialFiguresIfEmpty() {
        viewModelScope.launch {
            val currentFigures = repository.figuresFlow.first()
            if (currentFigures.isEmpty()) {
                repository.addFigure(
                    FigureEntity(
                        characterId = 2,
                        name = "Vegeta",
                        race = "Saiyan",
                        image = "https://dragonball-api.com/characters/vegeta_normal.webp",
                        status = "Exposto no Estúdio",
                        rating = 5,
                        customNote = "SH Figuarts Super Saiyan Blue - Destaque principal da estante"
                    )
                )
                repository.addFigure(
                    FigureEntity(
                        characterId = 1,
                        name = "Goku",
                        race = "Saiyan",
                        image = "https://dragonball-api.com/characters/goku_normal.webp",
                        status = "Exposto no Estúdio",
                        rating = 5,
                        customNote = "SH Figuarts S.H.F Son Goku - Edição Clássica com Esfera de 4 Estrelas"
                    )
                )
            }
        }
    }
}
