package projet;

import java.util.List;

public class Francais extends Wordle {

    // variables

    public Francais() { //constructor
        super(); //use the constructor from parent class
        result = "Result: ";
        youWonMessage = "CONGRATULATIONS! YOU WON! :)";
        youLostMessage = "YOU LOST :( THE WORD CHOSEN BY THE GAME IS: ";
    }

    // METHODS

    // print instructions
    public void printInstructions() {
        System.out.println("The game has chosen a 5-letter word for you to guess.");
        System.out.println("You have 6 tries. In each guess, the game will confirm which letters the chosen word and the guessed word have in common:");
        System.out.println("- Letters highlighted in " + ANSI_GREEN_BACKGROUND + "green" + ANSI_RESET + " are in the correct place.");
        System.out.println("- Letters highlighted in " + ANSI_YELLOW_BACKGROUND + "yellow" + ANSI_RESET + " appear in the chosen word but in a different spot.");
        System.out.println("- Letters highlighted in " + ANSI_GREY_BACKGROUND + "grey" + ANSI_RESET + " do not appear in the chosen word.");
    }

    // ask the user for their first word
    public void askForFirstGuess() {
        System.out.println();
        System.out.println("Please write down your first guess:");
    }

    // verify the validity of the user word by length and check against available options
    public String obtainValidUserWord(List<String> wordList, int index) {
        return Main.getMain().getIHM().getGues();
    }

    // print the alphabet with the associated colour for each letter
    public void printingColouredAlphabet(List<Character> greenLetters, List<Character> yellowLetters, List<Character> greyLetters) {
        char c;

        for (c = 'A'; c <= 'Z'; ++c) {
            if (greenLetters.contains(c)) {
                System.out.print(ANSI_GREEN_BACKGROUND + c + ANSI_RESET + " ");
            } else if (yellowLetters.contains(c)) {
                System.out.print(ANSI_YELLOW_BACKGROUND + c + ANSI_RESET + " ");
            } else if (greyLetters.contains(c)) {
                System.out.print(ANSI_GREY_BACKGROUND + c + ANSI_RESET + " ");
            } else {
                System.out.print(c + " ");
            }
        }

    }

    public void printDefinitionLink (String randomChosenWord) { // prints the link to the dictionary definition of the chosen word
        System.out.println("The word's definition: https://www.merriam-webster.com/dictionary/" + randomChosenWord);
    }


}