package app;


/** 
 * Classe contenant le point d'entr�e du jeu. <br/>
 * Carte par d�faut : "default.map" <br/>
 * Fichier log Slick2d : "TheCaterpillarQuest.log" <br/>
 * Fichier des scores : "scores.save"
 * @author Maxime Pineau
 */
public class Application {

	/** 
	 * Point d'entr�e du jeu. Lance la fen�tre de jeu.
	 * Le joueur devra entrer son nom en premier param�tre, puis le chemin vers le fichier de la carte s'il le d�sir. <br/>
	 * - args[0] contiendra le nom / pseudo du joueur. <br/>
	 * - arg[1] pourra contenir le chemin du fichier. <br/>
	 * Redirige �galement les logs dans un fichier de log. <br/>
	 * � la fin de la partie, les scores des 5 meilleurs joueurs s'afficheront, ainsi que le score du joueur actuel si il a termin� la partie. <br/>
	 * Les 5 meilleurs scores seront sauvegard�s dans le fichier "scores.save".
	 * 
	 * @param args arguments du programme.
	 * @author Maxime Pineau
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		/*
		 * R�cup�ration des param�tres
		 */
		if( args.length < 1 ) {
			System.err.println(" Erreur, il manque le premier param�tre.");
			System.err.println(" Vous devez entrer les param�tres comme ceci : ");
			System.err.println(" 	executable <nomDuJoueur> ");
			System.err.println(" 	executable <nomDuJoueur> <cheminFichierCarte>");
	        System.exit(-1);
		}
		
		String nomJoueur = args[0];
		System.out.println(nomJoueur);
		
		String cheminFichierMap = "";	// chemin par d�faut
		if( args.length >= 2 ) cheminFichierMap = args[1];
		
		// verification sur l'existance du fichier � faire
		/*
		File fichierCarte = new File(cheminFichierMap);
		if( ! fichierCarte.exists() ) {
			System.err.println(" Erreur, la carte n'existe pas.");
	        System.exit(-2);
		}
		*/
		
		
		/*
		 * Lancement de la fen�tre de jeu ici
		 */
		
		
		
		/*
		 * Gestion des scores
		 */
		
		// chargement de le liste des scores
		GestionScores scores = null;
		try {
			scores = GestionScores.chargerScores("scores.save");
		} catch ( GestionScoresException e) {
			scores = new GestionScores();
		}
		
		//scores.ajouterScore( new Score("test", 2, 8000) );
				
		scores.afficherScores();
		
		// sauvegarde de la liste des scores
		try {
			scores.sauvegarderScores("scores.save");
			
		} catch (GestionScoresException e) {
			System.err.println( "Erreur lors de la sauvegarde des scores : " + e.getMessage() );
		}
		
	}

}
