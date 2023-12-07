package projet.saveData;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import com.fasterxml.jackson.databind.ObjectMapper;

public record DixData(Data[] datas) {
	private static int index = 0; 
	public DixData() {
		 this(new Data[10]);
		 index=0;
	}
	public void addData(Data d) {
		datas[index] = d;
		index++;
	}
	public void save() {
		ObjectMapper mapper = new ObjectMapper();
    	String name = Date.from(Instant.now()).toString().replaceAll(" ", "_").replaceAll(":","_")+".json";
    	File parent = new File("datas10");
		parent.mkdir();
		File f = new File(parent,name);
        try {
			mapper.writeValue(f, this);
			System.out.println("Serialized datas are saved in /datas10/"+name);
        } catch (IOException e) {
            e.printStackTrace();
        	System.out.println("Impossible de sauvegarder le fichier " + f.getAbsolutePath());
        }
    }
	
}
