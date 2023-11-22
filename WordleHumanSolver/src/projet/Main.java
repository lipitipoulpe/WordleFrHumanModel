package projet;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import projet.IHM.IhmWordle;

public class Main {
	private static Main main;
	public static Main getMain() { return main; }
	private IhmWordle ihm;
	public IhmWordle getIHM() { return ihm; }
	private Thread gameTask;
	public Thread getGameTask() { return gameTask; }
	
    public static void main(String[] args) {
    	new Main();
    }
    public Main() {
		main = this;
		loadWords();
		gameTask = new Thread() {
			@Override
			public void run() {
				ihm.clear();
				new Francais().play();
			}
			@Override
			public void interrupt() {
				ihm.data.save();
				super.interrupt();
			}
		};
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ihm = new IhmWordle();
					ihm.getFrame().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    }
    public final List<WordInfo> words = new ArrayList<>();
	private void loadWords() {
		try (BufferedReader br = new BufferedReader(new FileReader("motsFR.csv"))) {
		    String line = br.readLine();
		    while ((line = br.readLine()) != null) {
		        String[] values = line.split(",");
		        words.add(new WordInfo(values[0],values[1],Float.parseFloat(values[2]),Integer.parseInt(values[3]),Integer.parseInt(values[4])));
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public List<WordInfo> getWords(){
			return words;
	}
}

