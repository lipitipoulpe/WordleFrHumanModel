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
/*    public void printInstructions() {
        System.out.println("The game has chosen a 5-letter word for you to guess.");
        System.out.println("You have 6 tries. In each guess, the game will confirm which letters the chosen word and the guessed word have in common:");
        System.out.println("- Letters highlighted in " + ANSI_GREEN_BACKGROUND + "green" + ANSI_RESET + " are in the correct place.");
        System.out.println("- Letters highlighted in " + ANSI_YELLOW_BACKGROUND + "yellow" + ANSI_RESET + " appear in the chosen word but in a different spot.");
        System.out.println("- Letters highlighted in " + ANSI_GREY_BACKGROUND + "grey" + ANSI_RESET + " do not appear in the chosen word.");
    }
*/
    // verify the validity of the user word by length and check against available options
    public String obtainValidUserWord(int index) {
    	//TODO check listWord contain gues 
        return Main.getMain().getIHM().getGues();
    }
}