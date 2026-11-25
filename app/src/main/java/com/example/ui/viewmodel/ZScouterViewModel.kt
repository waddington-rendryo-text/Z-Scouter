package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.FigureEntity
import com.example.data.local.WorkoutLogEntity
import com.example.data.model.CharacterDetailDto
import com.example.data.model.CharacterDto
import com.example.data.model.PlanetDto
import com.example.data.repository.ZScouterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

data class BattleResult(
    val fighter1: CharacterDto,
    val fighter2: CharacterDto,
    val score1: Long,
    val score2: Long,
    val winner: CharacterDto,
    val differencePct: Int,
    val analysisVerdict: String,
    val barDebateQuote: String
)

class ZScouterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ZScouterRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ZScouterRepository(
            figureDao = db.figureDao(),
            workoutDao = db.workoutDao()
        )
    }

    // Characters state
    private val _charactersState = MutableStateFlow<UiState<List<CharacterDto>>>(UiState.Loading)
    val charactersState: StateFlow<UiState<List<CharacterDto>>> = _charactersState.asStateFlow()

    private val _allCharacters = MutableStateFlow<List<CharacterDto>>(emptyList())

    val searchQuery = MutableStateFlow("")
    val selectedRaceFilter = MutableStateFlow("Todos")
    val selectedAffiliationFilter = MutableStateFlow("Todos")

    // Filtered characters
    val filteredCharacters: StateFlow<List<CharacterDto>> = combine(
        _allCharacters,
        searchQuery,
        selectedRaceFilter,
        selectedAffiliationFilter
    ) { list, query, race, affiliation ->
        list.filter { char ->
            val matchesQuery = query.isBlank() ||
                    char.name.contains(query, ignoreCase = true) ||
                    (char.description?.contains(query, ignoreCase = true) == true)

            val matchesRace = if (race == "Todos") true else {
                when (race) {
                    "Saiyajin" -> char.race.equals("Saiyan", ignoreCase = true)
                    "Humano" -> char.race.equals("Human", ignoreCase = true)
                    "Namekuseijin" -> char.race.equals("Namekian", ignoreCase = true)
                    "Androide" -> char.race.equals("Android", ignoreCase = true)
                    "Majin" -> char.race.equals("Majin", ignoreCase = true)
                    "Frieza Race" -> char.race.equals("Frieza Race", ignoreCase = true)
                    "Deuses / Anjos" -> char.race.equals("God", ignoreCase = true) || char.race.equals("Angel", ignoreCase = true)
                    else -> true
                }
            }

            val matchesAffiliation = if (affiliation == "Todos") true else {
                when (affiliation) {
                    "Z Fighter" -> char.affiliation.equals("Z Fighter", ignoreCase = true)
                    "Exército de Freeza" -> char.affiliation.equals("Army of Frieza", ignoreCase = true) || char.affiliation.equals("Freelancer", ignoreCase = true)
                    "Red Ribbon" -> char.affiliation.equals("Red Ribbon Army", ignoreCase = true)
                    "Outros" -> !char.affiliation.equals("Z Fighter", ignoreCase = true)
                    else -> true
                }
            }

            matchesQuery && matchesRace && matchesAffiliation
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Character Detail
    private val _selectedCharacterDetail = MutableStateFlow<CharacterDetailDto?>(null)
    val selectedCharacterDetail: StateFlow<CharacterDetailDto?> = _selectedCharacterDetail.asStateFlow()

    private val _isLoadingDetail = MutableStateFlow(false)
    val isLoadingDetail: StateFlow<Boolean> = _isLoadingDetail.asStateFlow()

    // Planets
    private val _planetsState = MutableStateFlow<UiState<List<PlanetDto>>>(UiState.Loading)
    val planetsState: StateFlow<UiState<List<PlanetDto>>> = _planetsState.asStateFlow()

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
        loadCharacters()
        loadPlanets()
        seedInitialFiguresIfEmpty()
    }

    fun loadCharacters() {
        viewModelScope.launch {
            _charactersState.value = UiState.Loading
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
                    _charactersState.value = UiState.Error(error.localizedMessage ?: "Erro ao carregar guerreiros da API")
                }
        }
    }

    fun loadPlanets() {
        viewModelScope.launch {
            _planetsState.value = UiState.Loading
            repository.getPlanets()
                .onSuccess { list ->
                    _planetsState.value = UiState.Success(list)
                }
                .onFailure { error ->
                    _planetsState.value = UiState.Error(error.localizedMessage ?: "Erro ao carregar planetas")
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
                    // Fallback to basic info if detail endpoint fails
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

    fun isCharacterInFigures(characterId: Int): Boolean {
        return figures.value.any { it.characterId == characterId }
    }

    fun randomizeQuote() {
        val nextQuote = quotes.random()
        _currentQuote.value = nextQuote
    }

    fun logWorkout(exerciseName: String, sets: Int, reps: Int, weightKg: Double, notes: String) {
        viewModelScope.launch {
            val baseKi = (sets * reps * (weightKg.coerceAtLeast(1.0)) * 12.0).toLong()
            val bonusVegeta = if (exerciseName.contains("Sayajin", ignoreCase = true) || exerciseName.contains("Gravidade", ignoreCase = true)) 2500L else 1000L
            val totalKiGained = baseKi + bonusVegeta

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
        val gained = totalKiGained.value ?: 0L
        return 9001L + gained // Starts right above 9000! "Mais de 8000!"
    }

    fun getSaiyanRankTitle(totalKi: Long): String {
        return when {
            totalKi < 15000 -> "Guerreiro Sayajin Novato (Scouter Verde)"
            totalKi < 35000 -> "Sayajin de Elite (Classe Real)"
            totalKi < 75000 -> "Guerreiro Oozaru (Fúria Primordial)"
            totalKi < 150000 -> "Super Sayajin (Aura Dourada)"
            totalKi < 300000 -> "Super Sayajin 2 (Raios de Energia)"
            totalKi < 600000 -> "Super Sayajin 3 (Poder Máximo da Terra)"
            totalKi < 1200000 -> "Super Sayajin Deus (Ki Divino Escarlate)"
            totalKi < 2500000 -> "Super Sayajin Blue (Controle Perfeito)"
            else -> "Instinto / Ego Superior (Lenda do Universo 7)"
        }
    }

    fun runBattleSimulation() {
        val f1 = battleFighter1.value ?: return
        val f2 = battleFighter2.value ?: return

        isSimulatingBattle.value = true
        battleResult.value = null

        viewModelScope.launch {
            kotlinx.coroutines.delay(1200) // Animated scouter calculation delay

            // Compute power index based on parsed ki or string weight
            val p1 = estimateKiScore(f1)
            val p2 = estimateKiScore(f2)

            val winner = if (p1 >= p2) f1 else f2
            val maxScore = maxOf(p1, p2)
            val minScore = minOf(p1, p2).coerceAtLeast(1L)
            val diffPct = (((maxScore - minScore).toDouble() / maxScore) * 100).toInt().coerceIn(2, 98)

            val verdict = if (f1.name.contains("Vegeta", ignoreCase = true)) {
                "Vegeta aplica disciplina tática implacável e o orgulho real para superar os limites adversários!"
            } else if (f2.name.contains("Vegeta", ignoreCase = true)) {
                "Vegeta demonstra o poder devastador do Príncipe com um Final Flash milimétrico!"
            } else if (p1 > p2) {
                "${f1.name} domina o combate através de maior escalonamento de Ki e transformações superiores!"
            } else {
                "${f2.name} supera com vantagem de velocidade, técnica milenar e resistência sobre-humana!"
            }

            val debateQuote = listOf(
                "Mano, numa mesa de bar eu defendo até o fim que técnica supera força bruta aqui!",
                "Lembra na saga Cell? Treino na Sala do Tempo define totalmente essa luta!",
                "Com a animação no estilo Naotoshi Shida essa luta quebraria a internet!",
                "Em Dragon Ball: Sparking ZERO esse matchup é puro reflexo e esquiva de vanishing!"
            ).random()

            battleResult.value = BattleResult(
                fighter1 = f1,
                fighter2 = f2,
                score1 = p1,
                score2 = p2,
                winner = winner,
                differencePct = diffPct,
                analysisVerdict = verdict,
                barDebateQuote = debateQuote
            )
            isSimulatingBattle.value = false
        }
    }

    private fun estimateKiScore(character: CharacterDto): Long {
        var baseScore = 50000L
        val cleanKi = character.ki?.replace(".", "")?.replace(",", "")?.split(" ")?.firstOrNull()?.toLongOrNull()
        if (cleanKi != null && cleanKi > 0) {
            baseScore += cleanKi.coerceAtMost(10000000000L)
        }

        if (character.maxKi?.contains("Septillion", ignoreCase = true) == true) {
            baseScore += 50000000000L
        } else if (character.maxKi?.contains("Billion", ignoreCase = true) == true) {
            baseScore += 5000000000L
        } else if (character.maxKi?.contains("Trillion", ignoreCase = true) == true) {
            baseScore += 15000000000L
        }

        if (character.race.equals("Saiyan", ignoreCase = true)) baseScore += 2000000000L
        if (character.name.contains("Vegeta", ignoreCase = true)) baseScore += 3500000000L // Royal pride bonus!
        if (character.name.contains("Goku", ignoreCase = true)) baseScore += 3200000000L
        if (character.name.contains("Beerus", ignoreCase = true) || character.name.contains("Whis", ignoreCase = true)) baseScore += 80000000000L

        return baseScore + Random.nextLong(10000, 50000)
    }

    private fun seedInitialFiguresIfEmpty() {
        viewModelScope.launch {
            if (figures.value.isEmpty()) {
                // Seed 2 figures matching Luke's profile: Vegeta and Goku SH Figuarts
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
