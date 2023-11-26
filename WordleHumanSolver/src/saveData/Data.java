package saveData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.databind.ObjectMapper;

public record Data(AtomicBoolean type, AtomicBoolean isEnd,List<String> words,List<String> validateWords,AtomicInteger ntry,AtomicReference<String> goodWord){
	//type : 0 = human, 1 = modele
	//isEnd //partie finie ou non

	public Data(boolean type) {
		this(new AtomicBoolean(type),new AtomicBoolean(false),new ArrayList<>(),new ArrayList<>(),new AtomicInteger(0),new AtomicReference<String>());
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
	public void save() {
        ObjectMapper mapper = new ObjectMapper();
    	String name = Date.from(Instant.now()).toString().replaceAll(" ", "_").replaceAll(":","_")+".json";
		File f = new File("datas\\"+name);
        try {
			mapper.writeValue(f, this);
			System.out.println("Serialized data is saved in /data/"+name);
        } catch (IOException e) {
            System.out.println("Impossible de sauvegarder le fichier " + f.getAbsolutePath());
        }
    }
}
