package projet;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ModeleFrancais extends Wordle{	
	//char[ntry][nlettre][0] lettre
	//char[ntry][nlettre][1] etat (G = gris, J = jaune, V = vert
	//mots possible : chosenWordList
	@Override
	public String obtainValidUserWord(int index) {
		List<String> dic = readDictionary();
		String ret = dic.get(new Random().nextInt(dic.size()));
		for (char c: ret.toCharArray())
			Main.getMain().getIHM().addletter(c);
		return ret;
	}
}
