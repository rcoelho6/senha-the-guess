package com.senhadeguess.backend.gameplay.application;

import com.senhadeguess.backend.gameplay.domain.GuessResult;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class GuessEvaluator {
    public void validateDigits(String digits) {
        if (digits == null || !digits.matches("[0-9]{4}")) {
            throw new IllegalArgumentException("A senha deve conter exatamente quatro algarismos de 0 a 9.");
        }
        Set<Character> unique = new HashSet<>();
        for (char digit : digits.toCharArray()) unique.add(digit);
        if (unique.size() != 4) throw new IllegalArgumentException("Os quatro algarismos devem ser distintos.");
    }

    public GuessResult evaluate(String guess, String secret) {
        validateDigits(guess);
        validateDigits(secret);
        int correct = 0;
        int present = 0;
        for (int index = 0; index < 4; index++) {
            if (guess.charAt(index) == secret.charAt(index)) correct++;
            if (secret.indexOf(guess.charAt(index)) >= 0) present++;
        }
        return new GuessResult(correct, present - correct);
    }
}