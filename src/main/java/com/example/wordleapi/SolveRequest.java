package com.example.wordleapi;

import java.util.List;

public record SolveRequest(
        String currentWord,
        List<Character> allowedLetters,
        List<Character> requiredLetters,
        List<Integer> requiredLetterWrongIndices,
        int maxSeconds,
	String lang
) {}
