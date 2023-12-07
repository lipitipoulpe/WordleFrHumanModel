package projet.traitementData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;

import projet.Main;
import projet.Wordle;
import projet.saveData.Data;
import projet.saveData.DixData;

public class Traitement {
	private static BufferedWriter brValMotH,brValMotM,brFreqLettreH,brFreqLettreM;
	
	static{
		try {
			brValMotH = new BufferedWriter(new FileWriter("valeurMotHumain.csv"));
			brValMotM = new BufferedWriter(new FileWriter("valeurMotModele.csv"));
			brFreqLettreH = new BufferedWriter(new FileWriter("frequenceLettreHumain.csv"));
			brFreqLettreM = new BufferedWriter(new FileWriter("frequenceLettreModele.csv"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		List<DixData> bigData = new ArrayList<>();
		Map<String,ArrayList<Data>> datas = new HashMap<>();
		ObjectMapper objectMapper = new ObjectMapper();
		bigData.addAll(Arrays.stream(Objects.requireNonNull(new File("datas10").listFiles())).map(file -> {
			try {
				return objectMapper.readValue(file, DixData.class);
			} catch (IOException e) {
				e.printStackTrace();
			} return null;
		}).toList());
		for(DixData bd : bigData)
			for(Data data : bd.datas()) {
				if(!datas.containsKey(data.goodWord().get()))
					datas.put(data.goodWord().get(),new ArrayList<>());
				datas.get(data.goodWord().get()).add(data);				
			}
		try {
			brValMotH.append("mot,nessais,valeurMot");
			brValMotH.newLine();
			brValMotM.append("mot,nessais,valeurMot");
			brValMotM.newLine();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for (String word : Main.chosenWords) {
			int nTestH=0,nTestM=0;
			float[][][][] freqMot = new float[2][5][5][26];
			float[][] valMot = new float[2][5];//type,ntry
			Iterator<Data> it = datas.get(word).iterator();
			while(it.hasNext()) {
				Data data = it.next();
				if(!data.isEnd().get()){
					continue;
				}
				for (int ntry=0;ntry<data.ntry().get();ntry++) {
					valMot[data.type().get()?1:0][ntry]+=valeurMot(data,ntry) ; 
					for(int nLettre=0;nLettre<5;nLettre++)
						if(!data.type().get())
							freqMot[data.type().get()?1:0][ntry][nLettre][data.tab().get(ntry).get(nLettre).get(0).charAt(0)-'A']+=1;									
				}
				if(data.type().get())nTestH++;
				else nTestM++;
			}
			for (int ntry=0;ntry<5;ntry++) {
				valMot[0][ntry]/=nTestM;
				valMot[1][ntry]/=nTestH;
				for(int nLettre=0;nLettre<5;nLettre++) {
					for (int i = 0; i < 26; i++) {
						freqMot[0][ntry][nLettre][i]/=nTestM;
						freqMot[1][ntry][nLettre][i]/=nTestH;
					}
				}
			}
			String[][][][][] topFreqMotString = new String[2][5][5][5][2];//mode,nTry,nLettre,Top5,0 lettre/1 freq
			for (int ntry=0;ntry<5;ntry++) {
				for(int nLettre=0;nLettre<5;nLettre++) {
					topFreqMotString[0][ntry][nLettre] = getTop5(freqMot[0][ntry][nLettre]);
					topFreqMotString[1][ntry][nLettre] = getTop5(freqMot[1][ntry][nLettre]);
				}
			}
			try {
				brFreqLettreH.newLine();
				brFreqLettreH.append(word);
				brFreqLettreH.newLine();
				brFreqLettreH.append("ntry,Top5,l0,f0,l1,f1,l2,f2,l3,f3,l4,f4");
				brFreqLettreH.newLine();
				brFreqLettreM.newLine();
				brFreqLettreM.append(word);
				brFreqLettreM.newLine();
				brFreqLettreM.append("ntry,Top5,l0,f0,l1,f1,l2,f2,l3,f3,l4,f4");
				brFreqLettreM.newLine();
				for (int ntry=0;ntry<5;ntry++){
					for(int top=0;top<5;top++){
						brFreqLettreM.append(ntry+","+top);
						brFreqLettreH.append(ntry+","+top);
						for(int nLettre=0;nLettre<5;nLettre++) {
							String[][][] currentVal = topFreqMotString[0][ntry];//nLettre,Top5,0 lettre/1 freq
							brFreqLettreM.append(","+currentVal[nLettre][top][0]+","+currentVal[nLettre][top][1]);
							currentVal = topFreqMotString[1][ntry];
							brFreqLettreH.append(","+currentVal[nLettre][top][0]+","+currentVal[nLettre][top][1]);
						}
						brFreqLettreM.newLine();
						brFreqLettreH.newLine();
					}
					brFreqLettreM.newLine();
					brFreqLettreH.newLine();
					brValMotM.append(word+","+ntry+","+valMot[0][ntry]);
					brValMotM.newLine();
					brValMotH.append(word+","+ntry+","+valMot[1][ntry]);
					brValMotH.newLine();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		try {
			brValMotH.flush();
			brValMotM.flush();
			brFreqLettreH.flush();
			brFreqLettreM.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String[][] getTop5(float[] freqMot) {
		List<Float> fl = new ArrayList<>(List.of(0f,0f,0f,0f,0f));
		List<Character> chars = new ArrayList<>();
		for (int i = 0; i < 26; i++) {
			for (int j = 0; j < 5; j++) {
				if(fl.get(j)<freqMot[i]) {
					fl.add(j,freqMot[i]);
					chars.add(j,(char)(i+'A'));
					break;
				}
			}
		}
		String[][] ret = new String[5][2];
		for (int i = 0; i < 5; i++) {
			if(i<chars.size()) {
				ret[i][0] = chars.get(i).toString();
				ret[i][1] = fl.get(i).toString();				
			} else {
				ret[i][0] = "_";
				ret[i][1] = "_";
			}

		}
		return ret;
	}

	private static int valeurMot(Data data, int i) {
		int ret = 0;
		for (int j = 0; j < 5; j++) {
			if(data.tab().get(i).get(j).get(1).equals(Wordle.VERT.toString()))
				ret+=5;
			else if(data.tab().get(i).get(j).get(1).equals(Wordle.JAUNE.toString()))
				ret+=2;
		}
		return ret;
	}


}
