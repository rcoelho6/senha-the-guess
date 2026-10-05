package com.senhadeguess.backend.gameplay.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.senhadeguess.backend.gameplay.domain.GuessResult;
import org.junit.jupiter.api.Test;

class GuessEvaluatorTest {
    private final GuessEvaluator evaluator = new GuessEvaluator();

    @Test
    void countsCorrectAndPartialDigits() {
        assertEquals(new GuessResult(1, 1), evaluator.evaluate("1234", "1387"));
    }

    @Test
    void countsAllDigitsAsPartialWhenOrderIsReversed() {
        assertEquals(new GuessResult(0, 4), evaluator.evaluate("1234", "4321"));
    }

    @Test
    void acceptsLeadingZeroes() {
        assertEquals(new GuessResult(4, 0), evaluator.evaluate("0482", "0482"));
    }

    @Test
    void rejectsRepeatedDigits() {
        assertThrows(IllegalArgumentException.class, () -> evaluator.validateDigits("4492"));
    }

    @Test
    void rejectsNonFourDigitInput() {
        assertThrows(IllegalArgumentException.class, () -> evaluator.validateDigits("12a4"));
        assertThrows(IllegalArgumentException.class, () -> evaluator.validateDigits("123"));
    }
}