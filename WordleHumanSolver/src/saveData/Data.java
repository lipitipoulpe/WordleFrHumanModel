package saveData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Data implements java.io.Serializable{
	private boolean type,//0 = human, 1 = modele
					end = false;//partie finie
	private List<String> words = new ArrayList<>();
	private List<String> validateWords = new ArrayList<>();
	private int ntry = 0;

	public Data(Boolean type) {
		this.type = type;
	}
	public void addWord(String e) {
		words.add(e);
	}
	public void validateWord(String e) {
		words.add(e+"<==");
		validateWords.add(e);
		ntry+=1;
	}
	public void end() {
		end = true;
	}
	public void save() {
		try {
			String name = Date.from(Instant.now()).toString().replaceAll(" ", "_").replaceAll(":","_")+".txt";
			File parent = new File("datas");
			if(!parent.exists())
				parent.createNewFile();
			File f = new File(parent,name);
			f.createNewFile();
			FileOutputStream fileOut = new FileOutputStream(f);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(this);
			out.close();
			fileOut.close();
			System.out.println("Serialized data is saved in /data/"+name);
		} catch (IOException i) {
			i.printStackTrace();
		}
	}
}
