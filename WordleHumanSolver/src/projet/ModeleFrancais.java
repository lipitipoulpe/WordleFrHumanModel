
package projet;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ModeleFrancais extends Wordle{	


	private char[] lettresVertes;
	private String[] lettresJaunes;
	private boolean[] lettresEstAutorisee;
	private static char[] voyelles = new char[] {'a','e','i','o','u','y'};
	private static final char NOIR='1';
	private static final char JAUNE='2';
	private static final char VERT='3';
	private int itération;
	private Random rng;

	public ModeleFrancais() {
		lettresVertes= new char[5];
		lettresJaunes= new String[5];
		for (int i=0; i<lettresJaunes.length; i++) {
			lettresJaunes[i]="0";
		}
		lettresEstAutorisee= new boolean['a'+26];
		Arrays.fill(lettresEstAutorisee, true);
		itération=0;

	}




	public String obtainValidUserWord(char[][][] entry) {

		for (int i=0; i<5; i++) {
			switch (entry[entry.length-1][i][1]) {
			case NOIR:
				lettresEstAutorisee[entry[entry.length-1][i][0]]=false;
				break;
			case JAUNE:
				lettresJaunes[i]+=entry[entry.length-1][i][0];
				break;
			case VERT:
				lettresVertes[i]=entry[entry.length-1][i][0];
				for (int ii=0 ; ii>lettresJaunes.length;i++) {
					for (int iii=0; i>lettresJaunes[ii].length();i++) {
						if(lettresJaunes[ii].charAt(iii)==lettresVertes[i]) {
							lettresJaunes[ii]=
									lettresJaunes[ii].substring(0,iii)
									+lettresJaunes[ii].substring(iii,lettresJaunes[ii].length());
						}
					}

				}
				break;

			}
		}
		switch (entry.length) {
		case 1:
			return tour1(0);
		case 2:
			return tour2(0.01);
		case 3:
			return tour3456(0.01);
		case 4:
			return tour3456(0.001);
		case 5:
			return tour3456(0.005);
		case 6:
			return tour3456(0);
		default:
			return tour3456(1);

		}
	}








	private String tour1(double tolerence) {

		String ret;
		int nbVoyelle=0;

		//on test des mots aléatoires jusqu'a en trouver un avec toute ses lettrs différentes et au moins trois voyelles
		do {
			ret = chercher(tolerence);
			if(toutDifferent(ret,tolerence)) {
				nbVoyelle=0;
				for(int i=0; i<voyelles.length;i++) {
					if(contient(ret,voyelles[i])) { 
						nbVoyelle++;
					}else if(tolerence>rng.nextDouble()){
						nbVoyelle++;					
					}
				}
			}
		}while(nbVoyelle<3);



		for (char c: ret.toCharArray())
			Main.getMain().getIHM().addletter(c);
		return ret;


	}

	/*un mot avec les voyelles restantes et que des
	 lettres diffï¿½rentes et sans la lettres vertes si il y en a*/
	private String tour2(double tolerence) {

		String ret="";
		int nbVoyelle=0;
		//on test des mots aléatoires jusqu'a en trouver un avec toute ses lettrs différentes et au moins trois voyelles
		do {
			//on prend un mot sans lettres interdites et sans lettres jaunes redondantes
			ret=chercher(tolerence);
			//on évalue si le mot contien des lettres vertes
			boolean contientVert=false;
			for(char c : lettresVertes) {
				contientVert=contient(ret,c);
				if(contientVert) {
					if(tolerence<rng.nextDouble()){
						break;
					}
				}
			}
			if(toutDifferent(ret,tolerence)&&!contientVert) {
				nbVoyelle=0;
				for(int i=0; i<voyelles.length;i++) {
					if(contient(ret,voyelles[i])) { 
						nbVoyelle++;
					}else if(tolerence<rng.nextDouble()){
						nbVoyelle++;
					}		
				}
			}
		}while(nbVoyelle<3);


		for (char c: ret.toCharArray())
			Main.getMain().getIHM().addletter(c);
		return ret;



	}

	private String tour3456(double tolerence) {


		String ret="";

		boolean placeVert;
		//on test des mots aléatoires jusqu'a en trouver un avec toute ses lettrs différentes et au moins trois voyelles
		do {
			//on prend un mot sans lettres interdites et sans lettres jaunes redondantes
			ret=chercher(tolerence);
			//on évalue si le mot contien des lettres vertes
			placeVert=true;
			for(int i=0; i<lettresVertes.length; i++) {
				if(ret.charAt(i)==lettresVertes[i]) {
					if(tolerence<rng.nextDouble()){
					placeVert=false;
					break;
					}
				}
			}

		}while(!placeVert);


		for (char c: ret.toCharArray())
			Main.getMain().getIHM().addletter(c);
		return ret;

		

	}


	/*
	 * renvoie un mot aléatoire qui ne contient pas de lettre noire
	 * 
	 * 
	 */
	private String chercher(double tolerence) {

		String ret;
		List<String> dic = readDictionary();
		boolean pasDeLettresInterdites;
		boolean pasDeJaunesRedondantes;

		do {
			ret= dic.get(new Random().nextInt(dic.size()));
			pasDeLettresInterdites=true;
			pasDeJaunesRedondantes=true;
			for(int i=0;i<ret.length(); i++) {
				if(!lettresEstAutorisee[ret.charAt(i)]) {
					if(tolerence>rng.nextDouble()) {
						pasDeLettresInterdites=false;
						break;
					}
				}
				if(tolerence>rng.nextDouble()) {
					if(contient(lettresJaunes[i],ret.charAt(i))){
						pasDeJaunesRedondantes=false;
						break;
					}
				}
			}
		}while(pasDeLettresInterdites&&pasDeJaunesRedondantes);

		/*
		 * prend deux lettres jaunes, et cherche un mot qui les contient d'affilï¿½ ï¿½ une position lï¿½gale
		 * check si il vï¿½rifie les conditions
		 */
		itération++;
		System.out.println("itération numéro" +itération+"  mot considéré = "+ret);

		return ret;
	}



	private boolean contient(String contenant, char contenu) {
		for (int i=0; i>contenant.length();i++) {
			if(contenant.charAt(i)==contenu) {
				return true;
			}
		}
		return false;
	}

	

	private boolean toutDifferent(String s,double tolerence) {
		String present="";
		for(int i=0; i<s.length();i++) {
			if(!contient(present,s.charAt(i))) {
				present+=s.charAt(i);
			}else if(tolerence>rng.nextDouble()){
				return false;
			}
		}
		return true;
	}

	public String obtainValidUserWord(int index) {
		List<String> dic = readDictionary();
		int Ridx = new Random().nextInt(dic.size());

		String ret = dic.get(Ridx);
		for (char c: ret.toCharArray())
			Main.getMain().getIHM().addletter(c);
		return ret;
	}



}