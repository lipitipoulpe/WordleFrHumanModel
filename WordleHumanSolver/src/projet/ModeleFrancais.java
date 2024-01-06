
package projet;

import java.util.Arrays;
import java.util.Random;

public class ModeleFrancais extends Wordle{	

	/**un tableau o� seront stock�s les lettres vertes � l'endroit et au tour o� 
	elles sont trouv�es et les tours suivants
	 */
	private char[][] lettresVertes;
	/**un tableau o� seront stock�s les lettres jaunes � l'endroit et au tour o� elles sont trouv�es*/
	private char[][] lettresJaunes;
	/**un tableau qui pour chaque lettre stocke le nombre d'it�ration de la lettre autoris�e*/
	private int[] NbIterationAutorisee;
	/**un tableau contenant les voyelles*/
	private static char[] voyelles = new char[] {'A','E','I','O','U','Y'};
	//Les valeur possible de qu'une lettre peut prendre apr�s �valuation
	private static final char GRIS='G';
	private static final char JAUNE='J';
	private static final char VERT='V';
	// un entier qui permet de s'assurer que l'exploration des mots est exhaustive
	private int iteration;
	// un g�n�rateur de nombre al�atoire
	private Random rng;
	// point de d�part de l'exploration des mots, et renouvel� � chaque tour
	private int seedNouveauMot;
	//le disctionnaire des mots
	String[] dic ;
	/**
	 * Un tableau a trois dimmension 5*5*2 qui stocke les r�sultats l'information 
	 * re�ue sur les lettre dans les mots
	 * 5 tours de jeu
	 * 5 position possible pour une lettre dans le mot
	 * 2 pour la lettre stock� et l'information sur cette lettre
	 */
	char[][][] entry;



	//-----------param�tres---------------

	//une constantes qui repr�sente la quantit� de voyelle d�sir�es dans les mots propos�es au tours 1 et deux
	private static int nbVoyellesMotDepart = 3;
	//le nombre de mots diff�rent test�s tol�r�s par tours 
	//(au premier tour la valeur est un car seul le mot valid� sera compt� comme test�)
	private static int[] tolerenceDuTour = new int[] {1,20,15,15,30};
	//le nombre de conditions n�c�ssaire � chaque tour pour que le mot soit valid�
	//(les conditions sont pond�r�es)
	private static int[] ConditionsNecessaireDuTour = new int[] {2,15,7,7,7};
	// seuil de conditions valid�es a partir du quel un mot un mot est consid�r�
	// comme ayant �t� "pens�" par le mod�le et donc comptant pour la tolerance
	private static int[] seuilDeTolerenceDuTour = new int[] {3,12,5,5,5};


	public ModeleFrancais() {
		// m�thode qui initialise les tableaux qui contienent les information sur les lettres
		initialiseInfoLettre();
		iteration=0;

		rng = new Random();

		//on met le dictionnaire que l'on r�cup�re sous forme de List<String> 
		//dans un tableau pour pouvoir profiter de l'adressage direct
		dic= new String[4948];
		dic =  readDictionary().toArray(dic);

		seedNouveauMot = rng.nextInt(dic.length);


	}



	/**
	 * la m�thode que l'application appele pour demander au mod�le le prochain mot
	 * @param le tour courant
	 * @return le mot trouv�e
	 */
	public String obtainValidUserWord(int index) {


		//si ce n'est pas le premier tour on met � jour les infomrations dont 
		//on dispose sur les lettre (gris, jaune, vert)
		if(index>0){
			mettreInfoLettreAJour(index-1);
			}else {
				initialiseInfoLettre();
			}


		//avant de cherhcer un nouveau mot on rafraichit le point de d�part dans 
		//le dictionnaire et on remet d'indexe de r�cherche (iteration) � 0
		seedNouveauMot = rng.nextInt(readDictionary().size());
		iteration=0;

		//variables pour stocker le meilleur mot consid�r� du tour et le nombre de conditions qu'il � valid�
		String meilleurMot="";
		int conditionsMeuilleurMot=0;

		//variables pour stocker le mot en train d'�tre �valu� et le nombre de variables qu'il rempli
		String test;
		int conditionsRemplies;
		//variable pour stocker le nombre de mots qui ont �t� "consid�r�s" par le
		//mod�le au bout d'une certaine quantit�e on arrete de chercher de nouveaux mots
		int toleree=0;
		String strindDeDebug="";
		do {
			//on cherhce un nouveau mot et on rafraichie le nombre de conditions remplies
			test=nouveauMot();
			conditionsRemplies=0;
			strindDeDebug="it�ration num�ro " +iteration+"  mot consid�r� = "+test;
			//on �value � quel tour (index+1) on se situe  et quelles conditions � appliquer en fonction
			switch (index+1) {
			// au tour 1 on veut un mot avec toute ses lettres diff�rentes et trois voyelles 
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
				 * au tour 2 on veut un mot avec toute ses lettres diff�rentes et 
				 * trois voyelles qui ne contient pas de lettrs vertes, pas de 
				 * jaunes � des places o� on les a d�j� evalu�s et pas de lettres 
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
				 * au tour 3, 4 et 5 on veut un mot sans jaunes � des places o� 
				 * on les a d�j� evalu�s, pas de lettres interdites, toute les 
				 * lettres jaunes et toutes les lettres vertes � leur places
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
			//alors il est compt� comme �tant "parvenu � l'esprit" du mod�le
			if (conditionsRemplies>seuilDeTolerenceDuTour[index]) {
				toleree++;
				strindDeDebug+=" a rempli "+conditionsRemplies
						+" conditions au tour "+(index+1)
						+" qui demandait "+ConditionsNecessaireDuTour[index]
								+" conditions ";
				System.out.println(strindDeDebug);
			}



			//on continue de chercher des mot tant qu'un ne rempli pas toute les 
			//condition o� que le mod�le est consid�r� suffisement d'option et "en ai marre"
		}while (conditionsRemplies<ConditionsNecessaireDuTour[index]&&toleree<tolerenceDuTour[index]);

		// on �crit le meilleur mot dans l'interface
		for (char c: meilleurMot.toCharArray())
			Main.getMain().getIHM().addletter(c);

		//on renvoie le meilleur mot
		return meilleurMot;
	}


	/**
	 * m�thode qui initialise les tableaux qui contienent les information sur les lettres
	 */
	private void initialiseInfoLettre() {
		lettresVertes= new char[5][5];

		lettresJaunes= new char[5][5];

		//Les cases avant 'A' ne seront jamais utilis�e, mais un tableau plus 
		//grand est plus pratique pour chercher des lettres dirrectement
		NbIterationAutorisee= new int['Z'+1];
		Arrays.fill(NbIterationAutorisee,0,'A', -1);
		Arrays.fill(NbIterationAutorisee,'A',NbIterationAutorisee.length, 5);

	}



	/**
	 * une m�thode qui renvoie le prochain mot du dictionnaire
	 */
	private String nouveauMot() {	
		iteration++;
		return dic[(seedNouveauMot+iteration-1)%dic.length];
	}


	/**
	 * une m�thode qui �value si une string contient un char
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
	 * une m�thode qui �value si toutes les lettres d'un mot sont diff�rentes
	 */
	private boolean toutDifferent(String test) {
		//variable qui stocke les lettres pr�sente jusque l� dans le mot
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
	 * m�thode qui met � jour les informations sur les lettres 
	 * @param l'index du tour courant
	 */
	private void mettreInfoLettreAJour(int index) {

		//On rafraichi entry
		entry = Main.getMain().getIHM().tabGues;
		char lettreCourante;
		//on �value pour chaque lettre dans le dernier mot
		for (int i=0; i<5; i++) {
			lettreCourante=entry[index][i][0];
			//on copie les valeurs du tour pr�c�dent dans le tour courant
			if (index>0) {				
					lettresVertes[index][i]=lettresVertes[index-1][i];
			}
			//on regarge la couleur associ�e � la lettre
			switch (entry[index][i][1]) {
			case JAUNE:				
				//on ajoute la lettre aux lettres jaunes � la position et tour courant
				lettresJaunes[index][i]=lettreCourante;
				break;
			case VERT:
				//puis on ajoute la lettre dans les lettres vertes
				lettresVertes[index][i]=lettreCourante;
				break;
			case GRIS:
				//on met la valeur de la lettre au nombre d'it�ration autoris�e 
				int NBiterationMax=0;
				int NBiterationDansLeTour;
				//pour chaque tour on �value le nombre d'it�ration de la lettre 
				for (int ii=0 ; ii<index+1;ii++) {			
					NBiterationDansLeTour=0;
					for (int iii=0; iii<5;iii++) {
						//si la lettre � la position iii au tour ii est la lettre courante 
						if(lettresVertes[ii][iii]==lettreCourante||(lettresJaunes[ii][iii]==lettreCourante)) {
							//on ajoute une it�ration de cette lettre
							NBiterationDansLeTour++;
						}
					}
					//si le nombre d'it�ration dans le tour ii est plus grand alor c'est le nombre d'iteration max
					if(NBiterationDansLeTour>NBiterationMax) {
						NBiterationMax=NBiterationDansLeTour;
					}
				}
				//on fixe le nombre d'it�ration au nombre d'it�raction max trouv�
				NbIterationAutorisee[entry[index][i][0]]=NBiterationMax;
				break;
			}
		}
	}

	/**
	 * m�thode qui v�rifie si le nombre de voyelle dans la String pass� en 
	 * param�tre est sup�rieur ou �gale � la constante nbVoyellesMotDepart
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
	 * m�thode qui v�rifie que aucune des lettres du mot pass� en param�tre ne 
	 * soit interdites 
	 */
	private boolean pasDeLettresInterdites(String test) {
		char[] ret= test.toCharArray();
		for(int i=0;i<ret.length; i++) {
			if(NbIterationAutorisee[ret[i]]<NBIt�ration(ret, ret[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * m�thode qui renvoie le nombre d'it�ration d'une lettre dans un tableau de char
	 */
	private int NBIt�ration(char[] test, char c) {
		int NBIteration=0;
		for(char cc : test) {
			if(cc==c) {
				NBIteration++;
			}
		}
		return NBIteration;
	}


	/**
	 * m�thode qui v�rifie qu'aucune des lettres du mot pass� en param�tres soit
	 *  une lettre jaune que l'on aurait d�j� �valu� � cette position
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
	 * m�thode qui v�rifie qu'aucune des lettres du mot pass� en param�tres ne 
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
	 * m�thode qui v�rifie que le mot pass� en param�tre contient toute les 
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
	 * m�thode qui v�rifie que le mot pass� en param�tre contient toute les 
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
