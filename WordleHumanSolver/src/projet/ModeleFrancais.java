
package projet;

import java.util.Arrays;
import java.util.List;

public class ModeleFrancais extends Wordle{	
	
	
	private char[] lettresVertes;
	private char[] lettresJaunes;
	private boolean[] lettresEstAutorisee;
	private static char[] voyelles = new char[] {'a','e','i','o','u','y'};
	private static final char NOIR='1';
	private static final char JAUNE='2';
	private static final char VERT='3';
	
	public ModeleFrancais() {
		lettresVertes= new char[5];
		lettresJaunes= new char[5];
		lettresEstAutorisee= new boolean['a'+26];
		Arrays.fill(lettresEstAutorisee, true);
		
	}
	
	

	
	public String obtainValidUserWord(char[][][] entry) {
		//TODO
		for (int i=0; i<5; i++) {
			switch (entry[entry.length-1][i][1]) {
			case NOIR:
				lettresEstAutorisee[entry[entry.length-1][i][0]]=false;
				break;
			case JAUNE:
				int j;
				for( j=0; lettresJaunes[j]!='\0';j++) {			
				}
				lettresJaunes[j]=entry[entry.length-1][i][0];
				break;
			case VERT:
				lettresVertes[i]=entry[entry.length-1][i][0];
			}
			
		}
		switch (entry.length) {
		case 1:
			return tour1();
		case 2:
			return tour2();
		case 3:
			return tour3();
		default:
			return tour456();
			
		}
	}


	private String tour1(/*trouver les bons param�tres*/) {
		//TODO
		//Chercher un mot avec 3+ voyelles et que des lettres diff�rentes (consid�rer un seuil de popularit� ?)

		return chercher(null);

	}
	
	private String tour2(/*trouver les bons param�tres*/) {
		//TODO
		
		//nombre de voyelles vertes ou jaunes
		int nbVoyelle=0;

		if(nbVoyelle<2) {
			/*un mot avec les voyelles restantes et que des
			 lettres diff�rentes et sans la lettres vertes si il y en a*/


		}else if(nbVoyelle==2){
			/*essayer de placer d�autres voyelles et de tester 
			des nouvelles consonnes*/

		}else {
		/*un nouveau mot en ne pla�ant pas les lettres vertes 
		pour explorer plus profond�ment*/
		}
		
		/*il est possible de trouver des mots avec des lettres interdites ou de 
		 * la mauvaise taille avec une petite probabilit� 
		 * (repr�senter quand on a du mal � trouver un mot)
		*/
		return chercher(null);

	}

	private String tour3(/*trouver les bons param�tres*/) {
		//TODO

		
		/* Chercher un nouveau mot en essayant de tester des nouvelles consonnes
		 *  et de placer les lettres au bon endroits (utiliser les structures 
		 *  r�currentes de mots pour s�aider)
		 */
		 
		/*il est possible de trouver des mots avec des lettres interdites ou de 
		 * la mauvaise taille avec une petite probabilit� 
		 * (repr�senter quand on a du mal � trouver un mot)
		*/
		return chercher(null);

	}

	private String tour456(/*trouver les bons param�tres*/) {
		//TODO

		/*
		 * Chercher un nouveau mot en essayant de placer les lettres au bon 
		 * endroits (utiliser les structures r�currentes de mots pour s�aider)

		 */
		return chercher(null);

	}

	private String chercher(char[] condition) {
		//TODO

		/*
		 * prend deux lettres jaunes, et cherche un mot qui les contient d'affil� � une position l�gale
		 * check si il v�rifie les conditions
		 */
		
		return null;
	}



	@Override
	public String obtainValidUserWord(int index) {
		// TODO Auto-generated method stub
		return null;
	}




	/*
	 * Au d�but de chaque tour 
	 * elimForbidenLetters()
	 * ! possibilit� de mettres des mots avec des lettres interdites avec une petite probabilit�
	 * 
	 */
}