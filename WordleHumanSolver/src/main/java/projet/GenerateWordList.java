package projet;

import java.util.List;
import java.util.Random;

public class GenerateWordList {
	public static void main(String[] args) {
		Main.loadWords();
		List<String> words = Wordle.readDictionary();
		for (int i = 0; i < 10; i++) {
			System.out.println(words.remove(new Random().nextInt(words.size())));
		}
	}
}
