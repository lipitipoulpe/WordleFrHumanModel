package projet;

public class Francais extends Wordle {
    public Francais() {
        super();
        result = "Result: ";
        youWonMessage = "CONGRATULATIONS! YOU WON! :)";
        youLostMessage = "YOU LOST :( THE WORD CHOSEN BY THE GAME IS: ";
    }
    // verify the validity of the user word by length and check against available options
	@Override
	public String obtainValidUserWord(int index) {
		String gues = " ";
		while(true) {
			gues = Main.getMain().getIHM().getGues();
			if(chosenWordList.contains(gues)) break;
			else {
				Main.getMain().getIHM().resetRow(gues);
			}
		}
        return gues;
	}
}