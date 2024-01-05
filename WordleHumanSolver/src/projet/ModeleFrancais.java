
package projet;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ModeleFrancais extends Wordle{	


	private char[] lettresVertes;
	private String[] lettresJaunes;
	private boolean[] lettresEstAutorisee;
	private static char[] voyelles = new char[] {'A','E','I','O','U','Y'};
	private static final char GRIS='G';
	private static final char JAUNE='J';
	private static final char VERT='V';
	private int iteration;
	private Random rng;
	private int seedNouveauMot;
	List<String> dic ;
	char[][][] entry;

	//paramètres
	private static int nbVoyellesMotDepart = 3;
	private static int[] seuilDetolerenceDuTour = new int[] {3,5,2,2,2};
	private static int[] tolerenceDuTour = new int[] {1,20,20,20,20};
	//au tour deux les conditions sont pondérées
	private static int[] ConditionsNecessaireDuTour = new int[] {2,11,4,4,4};

	public ModeleFrancais() {
		lettresVertes= new char[5];
		lettresJaunes= new String[5];
		for (int i=0; i<lettresJaunes.length; i++) {
			lettresJaunes[i]=""+'\0';
		}
		lettresEstAutorisee= new boolean['Z'+1];
		Arrays.fill(lettresEstAutorisee, true);
		iteration=0;
		rng = new Random();
		seedNouveauMot = rng.nextInt(readDictionary().size());
		dic = readDictionary();
	}




	public String obtainValidUserWord(int index) {

		if(index>0){mettreInfoLettreAJour(index);}



		seedNouveauMot = rng.nextInt(readDictionary().size());
		iteration=0;

		String meilleurMot="";
		int conditionsMeuilleurMot=0;
		String test;
		int conditionsRemplies;
		int toleree=0;

		do {
			test=nouveauMot();
			conditionsRemplies=0;
			System.out.println("itération numéro " +iteration+"  mot considéré = "+test);
			switch (index+1) {
			case 1:
				if(toutDifferent(test)) {
					conditionsRemplies++;
					System.out.print(" TT les lettres dif, ");
					}
				if(nbVoyelleSuffisant(test)) {
					System.out.print(" NB voyelle suffisent, ");
					conditionsRemplies++;}
				break;
			case 2:
				if(toutDifferent(test)) {
					conditionsRemplies+=2;
				System.out.print(" TT lettres dif, ");
				}
				if(nbVoyelleSuffisant(test)) {
					System.out.print(" NB voyelle suffisent, ");
					conditionsRemplies+=3;}
				if(contientPasDeVert(test)) {
					System.out.print(" pas de vert, ");
					conditionsRemplies+=2;}
				if(pasDeJaunesRedondantes(test)) {
					System.out.print(" pas de jaune redondantes, ");
					conditionsRemplies+=2;}
				if(pasDeLettresInterdites(test)) {
					System.out.print(" pas de lettres interdites, ");
					conditionsRemplies+=2;}
				break;
			case 5:

				System. exit(0);
				
			default :
				if(pasDeJaunesRedondantes(test)) {
					System.out.print(" pas de jaune redondantes, ");
					conditionsRemplies++;}
				if(pasDeLettresInterdites(test)) {
					System.out.print(" pas de lettres interdites, ");
					conditionsRemplies++;}
				if(TTLesJaunes(test)) {
					System.out.print(" TT les jaunes, ");
					conditionsRemplies++;}
				if(TTLesVertes(test)) {
					System.out.print(" TT les vertes, ");
					conditionsRemplies++;}
				
				break;
			}
			if (conditionsRemplies>conditionsMeuilleurMot) {
				meilleurMot=test;
				conditionsMeuilleurMot=conditionsRemplies;
			}
			if (conditionsRemplies>seuilDetolerenceDuTour[index]) {
				toleree++;
			}
			
			
			System.out.println("\n a rempli "+conditionsRemplies+" conditions au tour "+(index+1)+" qui demandait "+ConditionsNecessaireDuTour[index]+" conditions \n");

		}while (conditionsRemplies<ConditionsNecessaireDuTour[index]&&toleree<tolerenceDuTour[index]);

		for (char c: test.toCharArray())
			Main.getMain().getIHM().addletter(c);
		return meilleurMot;
	}




	/*
	 * renvoie le prochain mot du dictionnaire
	 */
	private String nouveauMot() throws IndexOutOfBoundsException{		
		if(iteration>dic.size()) {
			throw new IndexOutOfBoundsException();
		}
		iteration++;
		return dic.get((seedNouveauMot+iteration-1)%dic.size());
	}



	private boolean contient(String contenant, char contenu) {
		for (int i=0; i<contenant.length();i++) {
			if(contenant.charAt(i)==contenu) {
				return true;
			}
		}
		return false;
	}



	private boolean toutDifferent(String s) {
		String present="";
		for(int i=0; i<s.length();i++) {
			if(!contient(present,s.charAt(i))) {
				present+=s.charAt(i);
			}else {
				return false;

			}
		}
		return true;
	}

	private void mettreInfoLettreAJour(int index) {

		entry = Main.getMain().getIHM().tabGues;
		for (int i=0; i<5; i++) {
			System.out.println(new String(entry[index-1][i]));
			switch (entry[index-1][i][1]) {
			case GRIS:
				lettresEstAutorisee[entry[index-1][i][0]]=false;
				break;
			case JAUNE:
				lettresJaunes[i]+=entry[index-1][i][0];
				break;
			case VERT:
				lettresVertes[i]=entry[index-1][i][0];
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
	}

	private boolean nbVoyelleSuffisant(String test) {
		int nbVoyelle=0;
		for(int i=0; i<voyelles.length;i++) {
			if(contient(test,voyelles[i])) { 

				nbVoyelle++;
			}
		} 
		return nbVoyelle>=nbVoyellesMotDepart;
	}


	private boolean pasDeLettresInterdites(String test) {
		boolean pasDeLettresInterdites=true;
		for(int i=0;i<test.length(); i++) {
			if(!lettresEstAutorisee[test.charAt(i)]) {
				pasDeLettresInterdites=false;
				break;

			}
		}
		return pasDeLettresInterdites;
	}

	private boolean pasDeJaunesRedondantes(String test) {
		boolean pasDeJaunesRedondantes=true;
		for(int i=0;i<test.length(); i++) {

			if(contient(lettresJaunes[i],test.charAt(i))){
				pasDeJaunesRedondantes=false;
				break;
			}
		}
		return pasDeJaunesRedondantes;
	}

	private boolean contientPasDeVert(String test) {
		boolean contientVert=false;
		for(char c : lettresVertes) {
			contientVert=contient(test,c);
			if(contientVert) break;				
		}
		return !contientVert;
	}

	private boolean TTLesVertes(String test) {
		boolean vertesALaBonnePlace=true;
		for(int i=0; i<lettresVertes.length; i++) {
			if(lettresVertes[i]!='\0'&&test.charAt(i)!=lettresVertes[i]) {
				vertesALaBonnePlace=false;
				break;
			}
		}
		return vertesALaBonnePlace;
	}

	private boolean TTLesJaunes(String test) {
		boolean TTLesJaunes=true;
		for(int i=0; i<lettresJaunes.length; i++) {
			for(int ii=1; ii<lettresJaunes[i].length(); ii++) {
				if(!contient(test,lettresJaunes[i].charAt(ii))) {					
					TTLesJaunes=false;
					break;
				}
			}
		}
		return TTLesJaunes;
	}




}
