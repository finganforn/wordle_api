package com.example.wordleapi;

import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class WordleController {

    private final Wordle solver;
	private final WordService wordService;
	
    public WordleController(Wordle solver, WordService wordService) {
        this.solver = solver;
	this.wordService = wordService;
    }

    @PostMapping("/solve")
    public SolveResponse solve(@RequestBody SolveRequest request) {

	    System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

		
		LocalTime now = LocalTime.now();
		int limitSec = request.maxSeconds();	
		boolean timedOut = false;
	
		String word = request.currentWord().toLowerCase();
		
		ArrayList<Character> required = new ArrayList<>(request.requiredLetters());
		ArrayList<Integer> wrongPos = new ArrayList<>(request.requiredLetterWrongIndices());
		ArrayList<Character> allowed = new ArrayList<>(request.allowedLetters());
		
		System.out.print("word : " + word.replace(" ", "_") + " ");
		for (int i = 0; i < required.size() && i < wrongPos.size(); i++) {
			System.out.print(required.get(i));
			System.out.print((1+wrongPos.get(i)));
			System.out.print(" ");
		}
		System.out.println("");

		ArrayList<String> ordelRes = new ArrayList<String>();
		ArrayList<String> yellowGens = Wordle.generateQueryWords(word, required, wrongPos, now, (int) (limitSec*0.65)); //line42
		
		if (Wordle.incomplete(yellowGens)) {
			System.out.println("yellowGens timed out at " + Wordle.timePassed(now) + "ms");
			timedOut = true;
		}
		for (String s2 : yellowGens)
			ordelRes.addAll(Wordle.ordel(s2, allowed, required, wrongPos, now, limitSec)); //line48
		System.out.println("generating " + ordelRes.size() + " words took " + Wordle.timePassed(now) + "ms");
		
		if (Wordle.incomplete(ordelRes)) {
			System.out.println("ordelFunc timed out");
			timedOut = true;
		}
		
		//if (timeRanOut)
		//	ordelRes.add(0, "TIME EXPIRED, INCOMPLETE LIST");
		
		List<String> items = wordService.filterValidWords(ordelRes, request.lang());
		int size = items.size();
		
		System.out.println(size + " filtered words, at " + Wordle.timePassed(now) + "ms");

	        if (size <= 10) {
        	    // Just print everything
	            for (String s : items) {
	                System.out.print(s + " ");
	            }
	        }
		else {
	            //Compute step using integer division
		    int step = size / 10;  // ensures ~10 samples
		    if (step == 0) step = 1; // safety, though size > 10 makes this unlikely

		    for (int i = 0; i < size; i += step) {
	       	    System.out.print(items.get(i) + " ");
	       	    }
		}
		System.out.println();
		
		

		return new SolveResponse(items, timedOut); //line50
				
		
		/*
		//ArrayList<String> wordle(String currentWord, ArrayList<Character> allowedLetters, ArrayList<Character> requiredLetters, ArrayList<Integer> requiredLetterWrongIndices, LocalTime startTime, int maxSeconds) {
			
		ArrayList<Character> allowed = new ArrayList<Character>();
		String word = "S ING";
		
		allowed.add('T'); allowed.add('L'); allowed.add('W'); allowed.add('Q'); allowed.add('R'); allowed.add('K');
		ArrayList<Character> required = new ArrayList<Character>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		LocalTime now = LocalTime.now();
		int limitSec = 25;
			
		Wordle.ordel(word, allowed, required, indices, now, limitSec);
        ArrayList<String> result = solver.ordel(word, allowed, required, indices, now, limitSec);
        return new SolveResponse(String.join(" ", result));
		*/
    }
	
	@GetMapping("/test")
	public String test() {
		return "API version 0.11, English words loaded: " + 
               wordService.getEnglishWords().size() + 
			   "\nSwedish words loaded: " +
			   wordService.getSwedishWords().size();
	}
	
	@GetMapping("/random")
	public RandomWordResponse randomWord(
        @RequestParam(defaultValue = "en") String lang,
        @RequestParam int length) {

		String word = wordService.getRandomWord(lang, length);
		return new RandomWordResponse(word);
	}
}
