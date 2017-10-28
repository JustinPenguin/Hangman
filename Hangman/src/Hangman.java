/*
 * Names: Justin Peng and Stephen Hamlin
 * Section Leader: Katherine Erdman
 * This program lets you play hangman, asks if you want to play again, and calculates statistics at the end of the session such as win rate, games played, games won,rrer and best run.
 */

import acm.program.*;
import acm.util.*;
import java.io.*;    // for File
import java.util.*;  // for Scanner

public class Hangman extends HangmanProgram {
	public void run() {
		int gamesCount = 1;
		int gamesWon = 0;
		int best = 0; 
		// Sets the secret word as the randomly selected word from the dictionary
		intro();
		String dictionary = readLine("Dictionary filename? ");
		String secretWord = getRandomWord(dictionary);
		int guessesLeft = playOneGame(secretWord);
		if(guessesLeft > best){
			best = guessesLeft;
		}
		if(guessesLeft > 0){
			gamesWon++;
		}
		playAgain(gamesCount, gamesWon, best, guessesLeft, dictionary, secretWord);
		// Asks the user if they want to play again
		
	}
	
	/*
	 * Writes the introduction words of the game
	 * precondition: The screen is blank
	 * postcondition: The intro messages are written
	 */
	private void intro() {
		println("CS 106A Hangman!");
		println("I will think of a random word.");
		println("You'll try to guess its letters.");
		println("Every time you guess a letter");
		println("that isn't in my word, a new body");
		println("part of the hanging man appears.");
		println("Guess correctly to avoid the gallows!");
		println();
	}
	
	/*
	 * This is the bulk of the program
	 * It keeps track of the words guessed and guesses left and ensures that the user has inputted the correct type of information
	 * In addition to this, it takes the information from the other methods to have a secret word that is compared to the inputted letter and performs
	 * actions according to if they match or don't
	 * This method takes care of when to end the program based off of if the number of guesses left has reached 0 or if the secret word was guessed
	 * precondition: The round of hangman hasn't started yet. This could be after saying yes to restarting or launching the program
	 * postcondition: A single round of hangman is over
	 */
	private int playOneGame(String secretWord) {
		String hint = generateFirstHint(secretWord);
		String guessedLetters = "";
		int guessesLeft = 8;
		displayHangman(guessesLeft);
		displayGameConditions(hint, guessedLetters, guessesLeft);
		String guess = readLine("Your guess? ");
		char guessLetter = guess.charAt(0);
		while(hint.contains("-") && guessesLeft > 0){
			if(guess.length() == 1 && Character.isLetter(guessLetter)){
				char firstLetter = readGuess(guess);
				guess = "" + firstLetter;
				// Makes it so that you can't guess the same letter twice
				if(guessedLetters.contains(guess)){
					println("You already guessed that letter.");
				}else{
					// Adds the guessed letter to the guessed letters list and prints out "Correct!"
					guessedLetters = guessedLetters + guess;
					guessesLeft = correctOrNot(secretWord, guess, guessesLeft);
					canvas.clear();
					displayHangman(guessesLeft);
					hint = createHint(secretWord, guessedLetters);
					displayGameConditions(hint, guessedLetters, guessesLeft);
				}
			}else{
				// For if there wasn't a single letter typed by the user
				println("Type a single letter from A-Z.");
			}
			// To know when the game is over 
			guess = gameOver(hint, secretWord, guess, guessesLeft);
			guessLetter = guess.charAt(0);
		}
		return guessesLeft;
	}
	
	
	/*
	 * Goes through all of the words of the secret word and compraes them to the letter inputted by the user
	 * If the letter inputted matches with any of the letters in the string, the recreated hint string will have the 
	 * letter instead of the dash
	 * precondition: The user has inputted a letter to guess
	 * postcondition: The string with the dashes is rewritten and the letters in the secret word that matched the letter 
	 * inputted by the user replaces the dashes in their respective places in the secret word
	 */
	private String createHint(String secretWord, String guessedLetters) {
		String hint = "";
		// Recreates the string in a for loop with dashes until the correct letter matches up with a secret word letter
		for(int i = 0; i < secretWord.length(); i++){
			String secretLetter = secretWord.charAt(i) + "";
			if(guessedLetters.contains(secretLetter)){
				hint = hint + secretLetter;
			}else{					
				hint = hint + "-";
			}
		}	
		return hint;
	}
	/*
	 * Creates and displays a dash for each letter in the secret word 
	 * precondition: The user interface for hangman is being set up
	 * postcondition: There's a dash for every letter in the secret word
	 */
	private String generateFirstHint(String secretWord){
		String firstHint = "";
		for(int i=0; i < secretWord.length(); i++){
			firstHint = firstHint + "-";
		}
		return firstHint;
	}
	/*
	 * Uppercases the first letter of the user inputted string.
	 * precondition: The inputted letter is lowercase
	 * postcondition: The inputted letter is uppercase
	 */
	private char readGuess(String guessedLetters) {
		char guess = guessedLetters.charAt(0);
		guess = Character.toUpperCase(guess);
		return guess;
	}
	
	/*
	 * Creates a scanner that goes through all of the different files, prints them out, closes the scanner, 
	 * and displays an error message if there's a problem with the file
	 * precondition: The files are unopened as the game starts
	 * postcondition: The files are read and opened
	 */
	private void displayHangman(int guessCount) {
		try{
			// The guess count allows the code to be shorter than separately reading every file
			Scanner hangmanDrawing = new Scanner(new File ("res//display" + guessCount + ".txt"));
			safePrintFile(hangmanDrawing);
			hangmanDrawing.close();
		}catch(IOException ex){
			println("Error reading the file: " + ex);
		}
	}
	/*
	 * Checks to see if the files have a next line before proceeding and printing the files
	 * for the displayHangman method
	 * precondition: The files are being opened in the displayHangman method
	 * postcondition: The next line of the file is checked and printed 
	 */
	private void safePrintFile(Scanner hangman){
		while(hangman.hasNextLine()){
			String line = hangman.nextLine();
			canvas.println(line);
		}
	}
	/*
	 * Finds the win percent by dividing the games won by games played multiplied by 100
	 * Displays information about the games played, games won, win percent, the game with the least 
	 * amount of guesses used, and an outro message.
	 * precondition: The user hit no to playing again
	 * postcondition: The game statistics games played, games won, win percent, and best game are displayed
	 */
	private void stats(int gamesCount, int gamesWon, int best) {
		double winPercent = (double)(gamesWon/gamesCount)*100;
		println("Overall Statistics:");
		println("Games played: " + gamesCount);
		println("Games won: " + gamesWon);
		println("Win percent: " + winPercent + "%");
		println("Best game: " + best + " guess(es) remaining");
		println("Thanks for playing!");
	}
	
	/*
	 * Sees how many words are in the dictionary file and uses a random number generator with the upper bound
	 * of how many words there are to select a word in the file. 
	 * It also prevents file errors
	 * precondition: The secret word is undetermined at the beginning of the round
	 * postcondition: There's a secret word to be guessed at the beginning of the round
	 */
	private String getRandomWord(String filename) {
		String randomWord = "";
		int wordCount = 0;
		while(wordCount == 0){
			try{
				Scanner dictionary = new Scanner(new File (filename));
				// Finds the amount of words in the dictionary
				wordCount = dictionary.nextInt();
				int randomNumber = RandomGenerator.getInstance().nextInt(wordCount);
				// Picks the amount of words into the dictionary based off of the random number generator
				for(int i = 0; i < randomNumber; i++){
					randomWord = dictionary.next();
				}
				dictionary.close();
			}catch(IOException ex){
				println("Error reading the file: " + ex);
				filename = readLine("Enter a file name: ");
			}
		}
		return randomWord;
	}
	/*
	 * Asks the user if they want to play again, sets a random word from the dictionary as the random word, and keeps track of the game statistics
	 * precondition: A round of hangman ends
	 * postcondition: The hangman session is over and the game statistics are stored
	 */
	private void playAgain(int gamesCount, int gamesWon, int best, int guessesLeft, String dictionary, String secretWord){
		boolean playAgain = readBoolean("Play Again (Y/N)? ", "y", "n");
		while(playAgain){
			secretWord = getRandomWord(dictionary);
			guessesLeft = playOneGame(secretWord);
			if(guessesLeft > best){
				best = guessesLeft;
			}
			if(guessesLeft > 0){
				gamesWon++;
			}
			gamesCount++;
			playAgain = readBoolean("Play Again (Y/N)? ", "y", "n");
		}
		println();
		stats(gamesCount, gamesWon, best);
	}
	/*
	 * Prints out the in game user interface of letters guessed, guesses left, and the slots of the secret word to be guessed
	 * precondition: The game is being set up
	 * postcondition: The user interface is visible
	 */
	private void displayGameConditions(String hint, String guessedLetters, int guessesLeft){
		println("Secret word : " + hint);
		println("Your guesses: " + guessedLetters);
		println("Guesses left: " + guessesLeft);
	}
	/*
	 * Tells the user if their inputted letter is in the string and reduces the guesses left if it's not
	 * precondition: A letter is guessed by the user
	 * postcondition: The user is informed if the letter is in the secret word or not
	 */
	private int correctOrNot(String secretWord, String guess, int guessesLeft){
		if(secretWord.contains(guess)){
			println("Correct!");
		}else{
			println("Incorrect.");
			guessesLeft--;
		}
		return guessesLeft;
	}
	/*
	 * Tells the user the result of their round of hangman
	 * precondition: The game ends
	 * postcondition: The user is informed if they won or lost the game and are informed of the secret word
	 */
	private String gameOver(String hint, String secretWord, String guess, int guessesLeft){
		if(hint.contains("-") && guessesLeft > 0) {
			guess = readLine("Your guess? ");
		} else if  (guessesLeft > 0){
			println("Correct!");
			println("You win! My word was " + "\"" + secretWord + "\".");
		}else{
			println("Incorrect.");
			println("You lose. My word was " + "\"" + secretWord + "\".");
		}
		return guess;
	}
}

