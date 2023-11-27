package projet;

import java.util.*;

import projet.IHM.IhmWordle;


public abstract class Wordle {
	protected String chosenWord;
	protected List<String> chosenWordList;
	protected String result;
	protected String youWonMessage;
	protected String youLostMessage;
	private IhmWordle ihm;

	// Declaring the background colors
	public static final char GRIS = 'G';
	public static final char VERT = 'V';
	public static final char JAUNE = 'J';
	public static final char DEFAULT = ' ';

	// Constructor
	public Wordle() {
		ihm = Main.getMain().getIHM();
	}

	// METHODS
	// Read the dictionary and assemble the dictionary arrayList from which to choose the random chosen word
	public List<String> readDictionary() {
		List<String> wordList = new ArrayList<>();
		for(WordInfo w : Main.getMain().getWords())
		{
			if(w.freqfilms2()>0.05f)wordList.add(w.word().toUpperCase()); 
		}
		return wordList;
	}

	// Get a random word from the dictionary arraylist
	public String getRandomWord(List<String> wordList) {
		return wordList.get(new Random().nextInt(wordList.size()));
	}

	public String removeAccents(String str) {
		return str.replaceAll("é", "e").replace("è", "e").replace("ê", "e").replace("ë", "e").replace("à", "a").replace("ï", "i").replace("î", "i").replace("ô", "o");
	}

	// ask the user for a word, check for validity
	public abstract String obtainValidUserWord (int index);

	public void loopThroughSixGuesses() {
		for (int j = 0; j < 6;) {
			ihm.setTry(j);
			Main.getMain().getIHM().validate();
			Main.getMain().getIHM().repaint();
			String userWord = obtainValidUserWord(j);
			
			// check for green/yellow/grey letters
			for (int i = 0; i < 5; i++) {
				Map<Character,Integer> fLettre = new HashMap<Character, Integer>();
				if (userWord.charAt(i) == chosenWord.charAt(i)) {
					ihm.tabGues[j][i][1] = VERT;
					fLettre.put(userWord.charAt(i), fLettre.getOrDefault(userWord.charAt(i), 0)+1);
				} else if (freqOfChar(chosenWord,userWord.charAt(i))>fLettre.getOrDefault(userWord.charAt(i), 0)){
					fLettre.put(userWord.charAt(i), fLettre.getOrDefault(userWord.charAt(i), 0)+1);
					ihm.tabGues[j][i][1] = JAUNE;
				} else {
					ihm.tabGues[j][i][1] = GRIS;
				}
			}
			j++;
			Main.getMain().getIHM().validate();
			Main.getMain().getIHM().repaint();
			if(checkEqual(userWord,chosenWord)) {
				ihm.end(true, chosenWord);
				break;
			}
			if (j == 5) {
				ihm.end(false, chosenWord);
			}
		}
	}


	private boolean checkEqual(String u, String c) {
		if(u.length()!=c.length())return false;
		for (int i = 0; i < u.length(); i++) {
			if(u.charAt(i)!=c.charAt(i))
				return false;
		}
		return true;
	}
	protected boolean checkContains(List<String> l, String w) {
		for(String s : l) {
			if(s.equals(w))
				return true;
		}
		return false;
	}

	private int freqOfChar(String chosenWordWithoutAccents2, char charAt) {
		int ret = 0;
		for(char c : chosenWordWithoutAccents2.toCharArray()) {
			if(c==charAt)ret++;
		}
		return ret;
	}

	// play method that calls on all other methods.
	public void play () {
		// Selecting a random word from the dictionary
		chosenWordList = this.readDictionary();
		chosenWord = getRandomWord(chosenWordList);
		ihm.data.setGoodWord(chosenWord);
		this.loopThroughSixGuesses();
	}





}