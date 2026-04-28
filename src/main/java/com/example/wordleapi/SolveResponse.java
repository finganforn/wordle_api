package com.example.wordleapi;
import java.util.List;

public record SolveResponse(List<String> words, boolean timedOut) {}
