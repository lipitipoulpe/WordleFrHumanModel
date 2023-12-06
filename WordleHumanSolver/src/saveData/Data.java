package saveData;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.databind.ObjectMapper;

public record Data(AtomicBoolean type, AtomicBoolean isEnd,List<String> words,List<String> validateWords,AtomicInteger ntry,AtomicReference<String> goodWord, List<List<List<String>>> tab){
	//type : 0 = human, 1 = modele
	//isEnd //partie finie ou non
	//tab : ntry,nLettre,0 lettre/1 couleur
	public Data(boolean type) {
		this(new AtomicBoolean(type),new AtomicBoolean(false),new ArrayList<>(),new ArrayList<>(),new AtomicInteger(0),new AtomicReference<String>(), new ArrayList<>());
	}
	public Data(Data d, List<List<List<String>>> t) {
		this(d.type,d.isEnd,d.words,d.validateWords,d.ntry,d.goodWord, t);
	}
	public void setGoodWord(String gw) {
		goodWord.set(gw);
	}
	public void addWord(String e) {
		words.add(e);
	}
	public void validateWord(String e) {
		words.add(e+"<==");
		validateWords.add(e);
		ntry.incrementAndGet();
	}
	public void setEnd() {
		isEnd.set(true);
	}
}
