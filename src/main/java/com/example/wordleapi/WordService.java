package com.example.wordleapi;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WordService {

    private List<List<String>> englishWords;
    private List<List<String>> swedishWords;
	
    private final Random random = new Random();

    private Set<String> englishSet;
    private Set<String> swedishSet;

    @PostConstruct
    public void init() {
        WordCollector wc = new WordCollector();

        englishWords = wc.groupWordsByLength("english-words.txt");
        swedishWords = wc.groupWordsByLength("svenska-ord.txt");



	englishSet = englishWords.stream()
            .flatMap(List::stream)
            .collect(Collectors.toSet());

       swedishSet = swedishWords.stream()
            .flatMap(List::stream)
            .collect(Collectors.toSet());

       System.out.println("Dictionaries loaded.");
		
		/*
		for (int i = 0; i < 10 && i < englishWords.size(); i++) {
			for (int j = 0; j < 20 && j < englishWords.get(i).size(); j++) {
				System.out.println("english list " + i + " word " + j + " " + englishWords.get(i).get(j));
			}
		}
		for (int i = 0; i < 10 && i < swedishWords.size(); i++) {
			for (int j = 0; j < 20 && j < swedishWords.get(i).size(); j++) {
				System.out.println("swedish list " + i + " word " + j + " " + swedishWords.get(i).get(j));
			}
		}*/
    }

    public List<List<String>> getEnglishWords() {
        return englishWords;
    }

    public List<List<String>> getSwedishWords() {
        return swedishWords;
    }
    public String getRandomWord(String lang, int length) {

        List<List<String>> source;

        if ("sv".equalsIgnoreCase(lang)) {
            source = swedishWords;
        } else {
            source = englishWords;
        }

        if (length >= source.size() || source.get(length).isEmpty()) {
            throw new IllegalArgumentException(
                "No words found for length " + length + " in language " + lang
            );
        }

        List<String> wordsOfLength = source.get(length);

        return wordsOfLength.get(random.nextInt(wordsOfLength.size()));
    }

    public List<String> filterValidWords(List<String> candidates, String lang) {

        Set<String> dictionary =
            "sv".equalsIgnoreCase(lang) ? swedishSet : englishSet;

        return candidates.stream()
	    .map(String::toLowerCase)
            .filter(dictionary::contains)
            .toList();
    }

}
