package main.distle;

import static main.distle.EditDistanceUtils.*;
import java.util.*;

/**
 * AI Distle Player! Contains all logic used to automagically play the game of
 * Distle with frightening accuracy (hopefully).
 */
public class DistlePlayer {
    
    // [!] TODO: Any fields you want here!
    
	// Counts the guesses and turns of the game
	int guessesCount;
	
	// Holds length of the word
	int stringLength;
	
	// Dictionary to keep everything in and storing
	Set<String> distleDictionary;
	
	// Random number generator for the game
	Random numberGenerator;
	
    /**
     * Constructs a new DistlePlayer.
     * [!] You MAY NOT change this signature, meaning it may not accept any arguments.
     * Still, you can use this constructor to initialize any fields that need to be,
     * though you may prefer to do this in the {@link #startNewGame(Set<String> dictionary, int maxGuesses)}
     * method.
     */
    public DistlePlayer () {
        // [!] TODO: Any initialization of fields you want here (can also leave empty)
    }
    
    /**
     * Called at the start of every new game of Distle, and parameterized by the
     * dictionary composing all possible words that can be used as guesses / one of
     * which is the correct.
     * 
     * @param dictionary The dictionary from which the correct answer and guesses
     * can be drawn.
     * @param maxGuesses The max number of guesses available to the player.
     */
    public void startNewGame (Set<String> dictionary, int maxGuesses) {
        // [!] TODO!
        
    	// Cloning the dictionary
    	distleDictionary = dictionary;
    	
    	// New random number from the random number generator
    	this.numberGenerator = new Random();
    }
    
    /**
     * Requests a new guess to be made in the current game of Distle. Uses the
     * DistlePlayer's fields to arrive at this decision.
     * 
     * @return The next guess from this DistlePlayer.
     */
    public String makeGuess () {
        // [!] TODO!
        
    	// Holds all the guesses and then manages all possible guesses at the round of the game
    	return this.distleDictionary.stream().skip(this.numberGenerator.nextInt(this.distleDictionary.size())).findFirst().orElse(null);
    }
    
    /**
     * Called by the DistleGame after the DistlePlayer has made an incorrect guess. The
     * feedback furnished is as follows:
     * <ul>
     *   <li>guess, the player's incorrect guess (repeated here for convenience)</li>
     *   <li>editDistance, the numerical edit distance between the guess and secret word</li>
     *   <li>transforms, a list of top-down transforms needed to turn the guess into the secret word</li>
     * </ul>
     * [!] This method should be used by the DistlePlayer to update its fields and plan for
     * the next guess to be made.
     * 
     * @param guess The last, incorrect, guess made by the DistlePlayer
     * @param editDistance Numerical distance between the guess and the secret word
     * @param transforms List of top-down transforms needed to turn the guess into the secret word
     */
    public void getFeedback (String guess, int editDistance, List<String> transforms) {
        // [!] TODO!
        
    	// Takes the guess out of the dictionary
    	distleDictionary.remove(guess);
    	
    	// Getting the length of the string (word) and the guess to match with the goal string of the game.
    	stringLength = guess.length();
    	
    	// if there's a D in the string the length goes down by 1, if there's an I it goes up by 1.
    	for (String change : transforms) {
    		if (change == "D") {
    			stringLength--;
    		}
    		if (change == "I") {
    			stringLength++;
    		}
    	}
    	
    	// Iterating through the dictionary and looping it. It also adds and filters to the strings.
    	Iterator<String> distleIterator = distleDictionary.iterator();
    	while (distleIterator.hasNext()) {
    		String stringWord = distleIterator.next();
    		if (!getTransformationList(guess, stringWord, getEditDistTable(guess, stringWord)).equals(transforms)) {
    			distleIterator.remove();
    		}
    	}
    	
    	// Adding 1 guess turn to the count, changing the game.cd d
    	guessesCount++;
    }
    
}
