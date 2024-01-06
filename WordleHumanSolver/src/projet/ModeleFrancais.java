
package projet;

import java.util.Arrays;
import java.util.Random;

public class ModeleFrancais extends Wordle{	

	/**un tableau où seront stockés les lettres vertes à l'endroit et au tour où 
	elles sont trouvées et les tours suivants
	 */
	private char[][] lettresVertes;
	/**un tableau où seront stockés les lettres jaunes à l'endroit et au tour où elles sont trouvées*/
	private char[][] lettresJaunes;
	/**un tableau qui pour chaque lettre stocke le nombre d'itération de la lettre autorisée*/
	private int[] NbIterationAutorisee;
	/**un tableau contenant les voyelles*/
	private static char[] voyelles = new char[] {'A','E','I','O','U','Y'};
	//Les valeur possible de qu'une lettre peut prendre aprés évaluation
	private static final char GRIS='G';
	private static final char JAUNE='J';
	private static final char VERT='V';
	// un entier qui permet de s'assurer que l'exploration des mots est exhaustive
	private int iteration;
	// un générateur de nombre aléatoire
	private Random rng;
	// point de départ de l'exploration des mots, et renouvelé à chaque tour
	private int seedNouveauMot;
	//le disctionnaire des mots
	String[] dic ;
	/**
	 * Un tableau a trois dimmension 5*5*2 qui stocke les résultats l'information 
	 * reçue sur les lettre dans les mots
	 * 5 tours de jeu
	 * 5 position possible pour une lettre dans le mot
	 * 2 pour la lettre stocké et l'information sur cette lettre
	 */
	char[][][] entry;



	//-----------paramètres---------------

	//une constantes qui représente la quantité de voyelle désirées dans les mots proposées au tours 1 et deux
	private static int nbVoyellesMotDepart = 3;
	//le nombre de mots différent testés tolérés par tours 
	//(au premier tour la valeur est un car seul le mot validé sera compté comme testé)
	private static int[] tolerenceDuTour = new int[] {1,20,15,15,30};
	//le nombre de conditions nécéssaire à chaque tour pour que le mot soit validé
	//(les conditions sont pondérées)
	private static int[] ConditionsNecessaireDuTour = new int[] {2,15,7,7,7};
	// seuil de conditions validées a partir du quel un mot un mot est considéré
	// comme ayant été "pensé" par le modèle et donc comptant pour la tolerance
	private static int[] seuilDeTolerenceDuTour = new int[] {3,12,5,5,5};


	public ModeleFrancais() {
		// méthode qui initialise les tableaux qui contienent les information sur les lettres
		initialiseInfoLettre();
		iteration=0;

		rng = new Random();

		//on met le dictionnaire que l'on récupère sous forme de List<String> 
		//dans un tableau pour pouvoir profiter de l'adressage direct
		dic= new String[4948];
		dic =  readDictionary().toArray(dic);

		seedNouveauMot = rng.nextInt(dic.length);


	}



	/**
	 * la méthode que l'application appele pour demander au modèle le prochain mot
	 * @param le tour courant
	 * @return le mot trouvée
	 */
	public String obtainValidUserWord(int index) {


		//si ce n'est pas le premier tour on met à jour les infomrations dont 
		//on dispose sur les lettre (gris, jaune, vert)
		if(index>0){
			mettreInfoLettreAJour(index-1);
			}else {
				initialiseInfoLettre();
			}


		//avant de cherhcer un nouveau mot on rafraichit le point de départ dans 
		//le dictionnaire et on remet d'indexe de récherche (iteration) à 0
		seedNouveauMot = rng.nextInt(readDictionary().size());
		iteration=0;

		//variables pour stocker le meilleur mot considéré du tour et le nombre de conditions qu'il à validé
		String meilleurMot="";
		int conditionsMeuilleurMot=0;

		//variables pour stocker le mot en train d'être évalué et le nombre de variables qu'il rempli
		String test;
		int conditionsRemplies;
		//variable pour stocker le nombre de mots qui ont été "considérés" par le
		//modèle au bout d'une certaine quantitée on arrete de chercher de nouveaux mots
		int toleree=0;
		String strindDeDebug="";
		do {
			//on cherhce un nouveau mot et on rafraichie le nombre de conditions remplies
			test=nouveauMot();
			conditionsRemplies=0;
			strindDeDebug="itération numéro " +iteration+"  mot considéré = "+test;
			//on évalue à quel tour (index+1) on se situe  et quelles conditions à appliquer en fonction
			switch (index+1) {
			// au tour 1 on veut un mot avec toute ses lettres différentes et trois voyelles 
			case 1:
				if(toutDifferent(test)) {
					conditionsRemplies++;
					strindDeDebug+=" TT les lettres dif, ";
				}
				if(nbVoyelleSuffisant(test)) {
					strindDeDebug+=" NB voyelle suffisent, ";
					conditionsRemplies++;}
				break;
				/*
				 * au tour 2 on veut un mot avec toute ses lettres différentes et 
				 * trois voyelles qui ne contient pas de lettrs vertes, pas de 
				 * jaunes à des places où on les a déjà evalués et pas de lettres 
				 * interdites
				 */
			case 2:
				if(toutDifferent(test)){
					strindDeDebug+=" TT lettres dif, ";
					conditionsRemplies+=1;}
				if(nbVoyelleSuffisant(test)){
					strindDeDebug+=" NB voyelle suffisent, ";
					conditionsRemplies+=2;}
				if(contientPasDeVert(test,index-1)){
					strindDeDebug+=" pas de vert, ";
					conditionsRemplies+=4;}
				if(pasDeLettresInterdites(test)){
					strindDeDebug+=" pas de lettres interdites, ";
					conditionsRemplies+=4;}
				if(pasDeJaunesRedondantes(test,index-1)){
					strindDeDebug+=" pas de jaune redondantes, ";
					conditionsRemplies+=4;}
				break;
				/*
				 * au tour 3, 4 et 5 on veut un mot sans jaunes à des places où 
				 * on les a déjà evalués, pas de lettres interdites, toute les 
				 * lettres jaunes et toutes les lettres vertes à leur places
				 */
			default :
				if(pasDeJaunesRedondantes(test,index-1)) {
					strindDeDebug+=" pas de jaune redondantes, ";
					conditionsRemplies+=2;}
				if(pasDeLettresInterdites(test)) {
					strindDeDebug+=" pas de lettres interdites, ";
					conditionsRemplies+=1;}
				if(TTLesJaunes(test,index-1)) {
					strindDeDebug+=" TT les jaunes, ";
					conditionsRemplies+=2;}
				if(TTLesVertes(test,index-1)) {
					strindDeDebug+=" TT les vertes, ";
					conditionsRemplies+=2;}

				break;
			}
			//si le mot courant rempli plus de conditions que le meilleur mot, 
			//il devient le meilleur mot
			if (conditionsRemplies>conditionsMeuilleurMot) {
				meilleurMot=test;
				conditionsMeuilleurMot=conditionsRemplies;
			}
			//si un mot rempli plus de conditions que le seuil de tolerence 
			//alors il est compté comme étant "parvenu à l'esprit" du modèle
			if (conditionsRemplies>seuilDeTolerenceDuTour[index]) {
				toleree++;
				strindDeDebug+=" a rempli "+conditionsRemplies
						+" conditions au tour "+(index+1)
						+" qui demandait "+ConditionsNecessaireDuTour[index]
								+" conditions ";
				System.out.println(strindDeDebug);
			}



			//on continue de chercher des mot tant qu'un ne rempli pas toute les 
			//condition où que le modèle est considéré suffisement d'option et "en ai marre"
		}while (conditionsRemplies<ConditionsNecessaireDuTour[index]&&toleree<tolerenceDuTour[index]);

		// on écrit le meilleur mot dans l'interface
		for (char c: meilleurMot.toCharArray())
			Main.getMain().getIHM().addletter(c);

		//on renvoie le meilleur mot
		return meilleurMot;
	}


	/**
	 * méthode qui initialise les tableaux qui contienent les information sur les lettres
	 */
	private void initialiseInfoLettre() {
		lettresVertes= new char[5][5];

		lettresJaunes= new char[5][5];

		//Les cases avant 'A' ne seront jamais utilisée, mais un tableau plus 
		//grand est plus pratique pour chercher des lettres dirrectement
		NbIterationAutorisee= new int['Z'+1];
		Arrays.fill(NbIterationAutorisee,0,'A', -1);
		Arrays.fill(NbIterationAutorisee,'A',NbIterationAutorisee.length, 5);

	}



	/**
	 * une méthode qui renvoie le prochain mot du dictionnaire
	 */
	private String nouveauMot() {	
		iteration++;
		return dic[(seedNouveauMot+iteration-1)%dic.length];
	}


	/**
	 * une méthode qui évalue si une string contient un char
	 */
	private boolean contient(String contenant, char contenu) {
		for (int i=0; i<contenant.length();i++) {
			if(contenant.charAt(i)==contenu) {
				return true;
			}
		}
		return false;
	}


	/**
	 * une méthode qui évalue si toutes les lettres d'un mot sont différentes
	 */
	private boolean toutDifferent(String test) {
		//variable qui stocke les lettres présente jusque là dans le mot
		String present="";
		for(int i=0; i<test.length();i++) {
			// si charAt(i) et dans present alors cette lettre est un doublon
			if(!contient(present,test.charAt(i))) {
				present+=test.charAt(i);
			}else {
				return false;

			}
		}
		return true;
	}


	/**
	 * méthode qui met à jour les informations sur les lettres 
	 * @param l'index du tour courant
	 */
	private void mettreInfoLettreAJour(int index) {

		//On rafraichi entry
		entry = Main.getMain().getIHM().tabGues;
		char lettreCourante;
		//on évalue pour chaque lettre dans le dernier mot
		for (int i=0; i<5; i++) {
			lettreCourante=entry[index][i][0];
			//on copie les valeurs du tour précédent dans le tour courant
			if (index>0) {				
					lettresVertes[index][i]=lettresVertes[index-1][i];
			}
			//on regarge la couleur associée à la lettre
			switch (entry[index][i][1]) {
			case JAUNE:				
				//on ajoute la lettre aux lettres jaunes à la position et tour courant
				lettresJaunes[index][i]=lettreCourante;
				break;
			case VERT:
				//puis on ajoute la lettre dans les lettres vertes
				lettresVertes[index][i]=lettreCourante;
				break;
			case GRIS:
				//on met la valeur de la lettre au nombre d'itération autorisée 
				int NBiterationMax=0;
				int NBiterationDansLeTour;
				//pour chaque tour on évalue le nombre d'itération de la lettre 
				for (int ii=0 ; ii<index+1;ii++) {			
					NBiterationDansLeTour=0;
					for (int iii=0; iii<5;iii++) {
						//si la lettre à la position iii au tour ii est la lettre courante 
						if(lettresVertes[ii][iii]==lettreCourante||(lettresJaunes[ii][iii]==lettreCourante)) {
							//on ajoute une itération de cette lettre
							NBiterationDansLeTour++;
						}
					}
					//si le nombre d'itération dans le tour ii est plus grand alor c'est le nombre d'iteration max
					if(NBiterationDansLeTour>NBiterationMax) {
						NBiterationMax=NBiterationDansLeTour;
					}
				}
				//on fixe le nombre d'itération au nombre d'itéraction max trouvé
				NbIterationAutorisee[entry[index][i][0]]=NBiterationMax;
				break;
			}
		}
	}

	/**
	 * méthode qui vérifie si le nombre de voyelle dans la String passé en 
	 * paramètre est supèrieur ou égale à la constante nbVoyellesMotDepart
	 */
	private boolean nbVoyelleSuffisant(String test) {
		int nbVoyelle=0;
		for(int i=0; i<voyelles.length;i++) {
			if(contient(test,voyelles[i])) { 
				nbVoyelle++;
			}
		} 
		return nbVoyelle>=nbVoyellesMotDepart;
	}


	/**
	 * méthode qui vérifie que aucune des lettres du mot passé en paramètre ne 
	 * soit interdites 
	 */
	private boolean pasDeLettresInterdites(String test) {
		char[] ret= test.toCharArray();
		for(int i=0;i<ret.length; i++) {
			if(NbIterationAutorisee[ret[i]]<NBItération(ret, ret[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * méthode qui renvoie le nombre d'itération d'une lettre dans un tableau de char
	 */
	private int NBItération(char[] test, char c) {
		int NBIteration=0;
		for(char cc : test) {
			if(cc==c) {
				NBIteration++;
			}
		}
		return NBIteration;
	}


	/**
	 * méthode qui vérifie qu'aucune des lettres du mot passé en paramètres soit
	 *  une lettre jaune que l'on aurait déjà évalué à cette position
	 */
	private boolean pasDeJaunesRedondantes(String test, int index) {
		for(int i=0;i<=index; i++) {
		for(int ii=0;ii<test.length(); ii++) {
			if(lettresJaunes[i][ii]==test.charAt(ii)){
				return false;
			}
		}
		}
		return true;
	}

	/**
	 * méthode qui vérifie qu'aucune des lettres du mot passé en paramètres ne 
	 * soit verte
	 */
	private boolean contientPasDeVert(String test,int index) {
		for(char c : lettresVertes[index]) {
			if(contient(test,c)) {
				return false;	
			}
		}
		return true;
	}

	/**
	 * méthode qui vérifie que le mot passé en paramètre contient toute les 
	 * lettres vertes
	 */
	private boolean TTLesVertes(String test,int index) {
		for(int i=0; i<lettresVertes.length; i++) {
			if(lettresVertes[index][i]!='\0'&&test.charAt(i)!=lettresVertes[index][i]) {
				return false;
			}
		}
		return true;
	}


	/**
	 * méthode qui vérifie que le mot passé en paramètre contient toute les 
	 * lettres jaunes
	 */
	private boolean TTLesJaunes(String test, int index) {
		for(int i=0;i<=index; i++) {
		for(int ii=0; ii<lettresJaunes.length; ii++) {
			if(lettresJaunes[i][ii]!='\0'&&!contient(test,lettresJaunes[i][ii])) {					
				return false;
			}
		}
		}
		return true;
	}





}
