package com.example

import com.example.domain.model.Character
import com.example.domain.usecase.CalculateSaiyanRankUseCase
import com.example.domain.usecase.FilterCharactersUseCase
import com.example.domain.usecase.SimulateBattleUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ZScouterLogicTest {

    private val rankUseCase = CalculateSaiyanRankUseCase()
    private val filterUseCase = FilterCharactersUseCase()
    private val battleUseCase = SimulateBattleUseCase()

    @Test
    fun testBaseKi_startsOver8000() {
        val totalKi = rankUseCase.calculateTotalKi(0L)
        assertEquals(9001L, totalKi)
        assertTrue("Base Ki should be over 8000 for Luke!", totalKi > 8000)
    }

    @Test
    fun testSaiyanRankProgression() {
        assertEquals("Guerreiro Sayajin Novato (Scouter Verde)", rankUseCase.getSaiyanRankTitle(10_000))
        assertEquals("Super Sayajin (Aura Dourada)", rankUseCase.getSaiyanRankTitle(100_000))
        assertEquals("Super Sayajin Blue (Controle Perfeito)", rankUseCase.getSaiyanRankTitle(2_000_000))
        assertEquals("Instinto / Ego Superior (Lenda do Universo 7)", rankUseCase.getSaiyanRankTitle(5_000_000))
    }

    @Test
    fun testWorkoutKiCalculation() {
        val total = rankUseCase.calculateWorkoutKi(
            exerciseName = "Supino na Câmara de Gravidade 100x",
            sets = 4,
            reps = 10,
            weightKg = 80.0
        )
        assertEquals(40_900L, total)
    }

    @Test
    fun testFilterCharactersByRaceAndQuery() {
        val sample = listOf(
            Character(id = 1, name = "Goku", race = "Saiyan", affiliation = "Z Fighter"),
            Character(id = 2, name = "Vegeta", race = "Saiyan", affiliation = "Z Fighter"),
            Character(id = 3, name = "Piccolo", race = "Namekian", affiliation = "Z Fighter")
        )

        val saiyans = filterUseCase(sample, query = "", raceFilter = "Saiyajin", affiliationFilter = "Todos")
        assertEquals(2, saiyans.size)

        val vegeta = filterUseCase(sample, query = "veg", raceFilter = "Saiyajin", affiliationFilter = "Z Fighter")
        assertEquals(1, vegeta.size)
        assertEquals("Vegeta", vegeta.first().name)
        assertTrue(vegeta.first().isPrinceVegeta)
    }

    @Test
    fun testSimulateBattleBetweenSaiyans() {
        val vegeta = Character(id = 2, name = "Vegeta", ki = "54.000.000", maxKi = "19.84 Septillion", race = "Saiyan")
        val krillin = Character(id = 10, name = "Krillin", ki = "1.000.000", maxKi = "2.000.000", race = "Human")

        val result = battleUseCase(vegeta, krillin)
        assertEquals("Vegeta", result.winner.name)
        assertTrue(result.differencePct in 2..98)
    }
}
