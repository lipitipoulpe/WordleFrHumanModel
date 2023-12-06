package projet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import projet.IHM.IhmWordle;
import saveData.Data;

public abstract class Wordle {
	protected String chosenWord;
	protected List<String> chosenWordList;
	protected List<String> wordList;
	public void setChosenWordList(List<String> s) {
		chosenWordList = s;
	}
	private IhmWordle ihm;

	// Declaring the background colors
	public static final Character GRIS = 'G';
	public static final Character VERT = 'V';
	public static final Character JAUNE = 'J';
	public static final Character DEFAULT = ' ';

	// Constructor
	public Wordle() {
		wordList = readDictionary();
		ihm = Main.getMain().getIHM();
	}

	// METHODS
	// Read the dictionary and assemble the dictionary arrayList from which to choose the random chosen word
	public static List<String> readDictionary() {
		List<String> wordList = new ArrayList<>();
		for(WordInfo w : Main.getWordsInfo())
		{
			if(w.freqfilms2()>0.05f)wordList.add(w.word().toUpperCase()); 
		}
		return wordList;
	}

	// Get a random word from the dictionary arraylist
	public String getRandomWord() {
		return chosenWordList.get(new Random().nextInt(chosenWordList.size()));
	}
	private String getRandomWord10() {
		String s = chosenWordList.get(new Random().nextInt(chosenWordList.size()));
		chosenWordList.remove(s);
		return s;
	}

	public String removeAccents(String str) {
		return str.replaceAll("é", "e").replace("è", "e").replace("ê", "e").replace("ë", "e").replace("à", "a").replace("ï", "i").replace("î", "i").replace("ô", "o");
	}

	// ask the user for a word, check for validity
	public abstract String obtainValidUserWord (int index);

	public void loopThroughSixGuesses() {
		ihm.running=true;
		for (int j = 0; j < 5;j++) {
			ihm.setTry(j);
			Main.getMain().getIHM().validate();
			Main.getMain().getIHM().repaint();
			String userWord = obtainValidUserWord(j);
			Main.getMain().getIHM().data.validateWord(userWord);
			// check for green/yellow/grey letters
			Map<Character,Integer> fLettre = new HashMap<Character, Integer>();
			for (int i = 0; i < 5; i++) {
				fLettre.putIfAbsent(userWord.charAt(i), 0);
				if (userWord.charAt(i) == chosenWord.charAt(i)) {
					ihm.tabGues[j][i][1] = VERT;
					fLettre.put(userWord.charAt(i), fLettre.get(userWord.charAt(i))+1);
				}
			}
			for (int i = 0; i < 5; i++) {
				if (ihm.tabGues[j][i][1]!=VERT && freqOfChar(chosenWord,userWord.charAt(i))>fLettre.get(userWord.charAt(i))){
					fLettre.put(userWord.charAt(i), fLettre.get(userWord.charAt(i))+1);
					ihm.tabGues[j][i][1] = JAUNE;
				} else if(ihm.tabGues[j][i][1]!=VERT) {
					ihm.tabGues[j][i][1] = GRIS;
				}
			}
			Main.getMain().getIHM().validate();
			Main.getMain().getIHM().repaint();
			if(checkEqual(userWord,chosenWord)) {
				ihm.end(true, chosenWord);
				break;
			}
			if (j == 4) {
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
		chosenWordList = readDictionary();
		chosenWord = getRandomWord();
		ihm.data.setGoodWord(chosenWord);
		this.loopThroughSixGuesses();
	}
	public void play10(List<String> chosenWords) {
		// Selecting a random word from the dictionary
		chosenWordList = new ArrayList<String>(chosenWords);
		for (int i = 0; i < 10; i++) {
			ihm.clear();
			chosenWord = getRandomWord10();
			ihm.data.setGoodWord(chosenWord);
			this.loopThroughSixGuesses();
			if(ihm.data!=null)ihm.datas.addData(new Data(ihm.data,getAsList(ihm.tabGues)));
		}
		ihm.datas.save();
	}

	//tab : ntry,nLettre,0 lettre/1 couleur
	private List<List<List<String>>> getAsList(char[][][] tabGues) {
		List<List<List<String>>> ret = new ArrayList<>();
		for (int ntry = 0; ntry < 5; ntry++) {
			List<List<String>> sub1 = new ArrayList<>();
			for (int nLettre = 0; nLettre < 5; nLettre++) {
				List<String> sub2 = new ArrayList<>();
				sub2.add(tabGues[ntry][nLettre][0]+"");
				sub2.add(tabGues[ntry][nLettre][1]+"");
				sub1.add(sub2);
			}
			ret.add(sub1);
		}
		return ret;
	}
}