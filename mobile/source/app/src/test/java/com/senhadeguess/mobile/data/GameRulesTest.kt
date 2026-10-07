package com.senhadeguess.mobile.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameRulesTest {
    @Test
    fun acceptsFourDistinctDigits() {
        assertTrue(isValidCode("4820"))
    }

    @Test
    fun rejectsRepeatedOrIncompleteCodes() {
        assertFalse(isValidCode("1123"))
        assertFalse(isValidCode("123"))
        assertFalse(isValidCode("12a4"))
    }

    @Test
    fun countsCorrectAndMisplacedDigits() {
        assertEquals(GuessFeedback(correct = 1, misplaced = 3), evaluateGuess("4082", "4820"))
    }

    @Test
    fun exactMatchHasFourCorrectDigits() {
        assertEquals(GuessFeedback(correct = 4, misplaced = 0), evaluateGuess("1234", "1234"))
    }
}
