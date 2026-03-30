package com.example.wordleapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WordCollector {

    public List<List<String>> groupWordsByLength(String filename) {

        List<List<String>> grouped = new ArrayList<>();

        try (
            InputStream inputStream =
                getClass().getClassLoader().getResourceAsStream(filename);

            BufferedReader reader =
                new BufferedReader(new InputStreamReader(inputStream))
        ) {
            String word;

            while ((word = reader.readLine()) != null) {
		word = word.trim().toLowerCase();
                int length = word.length();

                // Expand list if necessary
                while (grouped.size() <= length) {
                    grouped.add(new ArrayList<>());
                }

                grouped.get(length).add(word);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to load file: " + filename, e);
        }

        return grouped;
    }
}
