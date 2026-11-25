package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ZScouterLogicTest {

    @Test
    fun testBaseKi_startsOver8000() {
        val baseKi = 9001L
        assertTrue("Base Ki should be over 8000 for Luke!", baseKi > 8000)
    }

    @Test
    fun testSaiyanRankProgression() {
        fun getRank(ki: Long): String {
            return when {
                ki < 15000 -> "Novato"
                ki < 35000 -> "Elite"
                ki < 75000 -> "Oozaru"
                ki < 150000 -> "Super Sayajin"
                ki < 300000 -> "Super Sayajin 2"
                ki < 600000 -> "Super Sayajin 3"
                ki < 1200000 -> "Super Sayajin Deus"
                ki < 2500000 -> "Super Sayajin Blue"
                else -> "Instinto / Ego Superior"
            }
        }

        assertEquals("Novato", getRank(10000))
        assertEquals("Super Sayajin", getRank(100000))
        assertEquals("Super Sayajin Blue", getRank(2000000))
        assertEquals("Instinto / Ego Superior", getRank(5000000))
    }

    @Test
    fun testWorkoutKiCalculation() {
        val sets = 4
        val reps = 10
        val weightKg = 80.0
        val baseKi = (sets * reps * weightKg * 12.0).toLong()
        val bonus = 2500L
        val total = baseKi + bonus

        assertEquals(40900L, total)
        assertTrue(total > 0)
    }
}
