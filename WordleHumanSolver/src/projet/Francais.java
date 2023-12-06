package projet;

public class Francais extends Wordle {
    public Francais() {
        super();
    }
    // verify the validity of the user word by length and check against available options
	@Override
	public String obtainValidUserWord(int index) {
		String gues = " ";
		while(true) {
			gues = Main.getMain().getIHM().getGues();
			if(wordList.contains(gues)) break;
			else {
				Main.getMain().getIHM().resetRow(gues);
			}
		}
		
        return gues;
	}
}