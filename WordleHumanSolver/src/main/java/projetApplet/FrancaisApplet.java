package projetApplet;

public class FrancaisApplet extends WordleApplet {
    public FrancaisApplet() {
        super();
    }
    // verify the validity of the user word by length and check against available options
	@Override
	public String obtainValidUserWord(int index) {
		String gues = " ";
		while(true) {
			gues = MainApplet.getMain().getIHM().getGues();
			if(wordList.contains(gues)) break;
			else {
				MainApplet.getMain().getIHM().resetRow(gues);
			}
		}
		
        return gues;
	}
}