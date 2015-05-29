package elementsGraphiques;

import jeu.Carte;
import jeu.Jeu;
import jeu.PartieException;
import jeu.PartieGagneeException;
import jeu.PartiePerdueException;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;


import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;


/**
 * @author Cyril
 *
 */
public class Personnage extends ElementDeplacable {
	
	private int nbPoints = 0;
	private boolean  isMoving = false, jumping = false, isCollisionOnTop = false;;

	private Direction direction;
	private Direction orientation;
	private int situationAnimation;

	
	private float speed = 10f;
	
	private float vx = 0.0f; // vitesse en x
	private float vy = 0.0f; // vitesse en y

	private float ay = 0.0f; // valeur de l'acc�l�ration
	private float dx = 0.0f; // valeur du d�placement du personnage en X
	private float dy = 0.0f; // valeur du d�placement du personnage en Y
	private double tempsSaut = 0.5f;

	
	/**
	 * La suite de points (x,y) permettant de contruire les hitbox de la gu�pes
	 */
	private static final float[] POSITIONS_HITBOX_GAUCHE = { };
	private static final float[] POSITIONS_HITBOX_DROITE = { };
	private static final float[] POSITIONS_HITBOX_DEPLACEMENT_GAUCHE = { };
	private static final float[] POSITIONS_HITBOX_DEPLACEMENT_DROITE = { };
	private static final float[] POSITIONS_HITBOX_SAUT_GAUCHE = { };
	private static final float[] POSITIONS_HITBOX_SAUT_DROITE = { };
	
	
	/**
	 * @param x La position en x du personnage
	 * @param y La position en y du personnage
	 */
	public Personnage( int x, int y ) {
		super( x, y, 32, 32, new Rectangle(0, 0, 31, 32), "./sprites/perso.png" );	
		this.direction = Direction.IMMOBILE;
		this.orientation = Direction.DROITE;
		this.animations = new Animation[6];
		this.situationAnimation = 1;
	}
	
	
	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		if(direction == Direction.DROITE || direction == Direction.GAUCHE){
			if(direction == Direction.DROITE && this.orientation==Direction.GAUCHE){
				//de gauche � droite
				this.orientation = Direction.DROITE;
				this.situationAnimation++;
			}
			else if(direction == Direction.GAUCHE && this.orientation==Direction.DROITE){
				//de droite � gauche
				this.orientation = Direction.GAUCHE;
				this.situationAnimation--;
			}
		}
		this.direction = direction;
	}

	@Override
	public void initialiser() throws SlickException {
		super.initialiser();
		
		this.sprite = new SpriteSheet( this.cheminSprite, 32, 32 );
		
		this.animations = new Animation[6];
		
		Animation anim;
		//d�claration des 4 premi�res animations : immobile et d�placement
		for(int i=0;i<4;i++){
			anim = new Animation();
			anim.addFrame(this.sprite.getSprite(0, i),250);
			anim.addFrame(this.sprite.getSprite(1, i),250);
			this.animations[i] = anim;
		}
		//5ème animation : sauter � gauche
		anim = new Animation();
		anim.addFrame(this.sprite.getSprite(0, 4), 1000);
		this.animations[4] = anim;
		
		//Animation : sauter � droite
		anim = new Animation();
		anim.addFrame(this.sprite.getSprite(1, 4), 1000);
		this.animations[5] = anim;
		
		this.hitboxs = new Hitbox[6];
		this.hitboxs[0] = new Hitbox( Personnage.POSITIONS_HITBOX_GAUCHE );
		this.hitboxs[1] = new Hitbox( Personnage.POSITIONS_HITBOX_DROITE );
		this.hitboxs[2] = new Hitbox( Personnage.POSITIONS_HITBOX_DEPLACEMENT_GAUCHE );
		this.hitboxs[3] = new Hitbox( Personnage.POSITIONS_HITBOX_DEPLACEMENT_DROITE );
		this.hitboxs[4] = new Hitbox( Personnage.POSITIONS_HITBOX_SAUT_GAUCHE );
		this.hitboxs[5] = new Hitbox( Personnage.POSITIONS_HITBOX_SAUT_DROITE );
		
	}
	
	@Override
	public void update( GameContainer conteneur, int delta, Carte carte ) throws SlickException, PartieException {
		
		Point oldPosition;
		
		this.vx = (float) ( 14 * 0.015 * this.speed );
		this.vy = (float) ( 14 * 0.05 * this.speed );
		
		// acc�ration, � ajouter � dy pour cr�er la gravit� ou retirer � dy pour cr�er le saut
		this.ay = (float) ( this.vy * (14/1000.0) / this.tempsSaut );	
		

		// on autorise le personnage� se d�placer une seule fois
		if( this.direction == Direction.DROITE && this.isMoving ) this.dx = this.vx; 	// d�placement � droite
		else if( direction == Direction.GAUCHE && this.isMoving ) this.dx = - this.vx; 	// d�placement � gauche
		else this.dx = 0;
				
		oldPosition = this.getPosition(); // on sauvegarde l'ancienne position
		this.setPositionX( this.getPositionX() + this.dx ); 
		if( this.estEnCollisionAvecPlateforme(carte) && ! this.isCollisionOnTop ) this.setPositionX( oldPosition.getX() );
		

		// si le personnage est en l'air (pour se d�placer dans les airs)
		if( this.jumping ) this.dy -= this.ay; 	// le personnage est en l'air
		else { //si le joueur est au sol

			if( this.direction == Direction.HAUT && this.isMoving ) {
			
				this.dy = this.vy;
				this.setJumping(true); // le personnage va sauter
				
			}
		}
		
		oldPosition = this.getPosition();
		this.setPositionY( this.getPositionY() - this.dy );
		if( this.estEnCollisionAvecPlateforme(carte) && ! isCollisionOnTop ) { // si on est en collision avec autre chose que le sommet de la plateforme
			this.setPositionY( oldPosition.getY() );
			this.dy = 0;	// on remet l'acc�l�ration � 0
		}
		

		ElementRamassable elementRamassable = carte.getElementRamassableEnCollisionAvecElement(this); 
		if( elementRamassable != null ) {
			this.nbPoints += elementRamassable.getNbPoints();
			carte.supprimerElementRamassable(elementRamassable);
		}
		
		// le personnage touche une porte, le joueur gagne la partie
		if( carte.elementEnCollisionAvecUnePorte(this) ) throw new PartieGagneeException();
		

		// le personnage touche une gu�pe, le joueur perd la partie
		if( carte.elementEnCollisionAvecUnEnnemi(this) ) throw new PartiePerdueException();

		
		// le personnage sort de la fen�tre, le joueur perd la partie
		if( this.getPositionY() > Jeu.HAUTEUR ) throw new PartiePerdueException();
		
	}	
	
	/**
	 * Indique si le personnage est en collision avec une plateforme
	 * @param carte
	 * @return
	 */
	public boolean estEnCollisionAvecPlateforme(Carte carte){
				
		for( ElementFixe plateforme : carte.getElementsFixes() ) {	
			if( this.estEnCollisionAvec(plateforme) ){
				
				// collision en haut
				if( this.getPositionY() - plateforme.getPositionY() < 0.0f ) {
					this.setPositionY(plateforme.getPositionY() - this.getHauteur());
					isCollisionOnTop = true;
					this.setJumping(false);
				}else{ // collision sur les autres c�t�s
					System.out.println("Collision sur le c�t�");
					this.setJumping(true);
					isCollisionOnTop = false;
					this.setMoving(false);
				}					
					
				return true;		
			}
				
		}

		this.setJumping(true); // le personnage est en l'air
		
		return false;
	}
	
	public float getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}


	public boolean isMoving() {
		return isMoving;
	}

	public void setMoving(boolean isMoving) {
		if(this.isMoving == false && isMoving == true && this.jumping == false){
			//le perso avance
			this.situationAnimation += 2;
		}else if(this.isMoving == true && isMoving == false && this.jumping == false){
			//le perso s'arr�te
			this.situationAnimation -= 2;
		}
		this.isMoving = isMoving;
	}
	
	public boolean isJumping() {
		return jumping;
	}


	public void setJumping(boolean jumping) {
		if(this.jumping == false && jumping == true && this.isMoving == false){
			//le perso saute
			if(this.situationAnimation+4 <= 5) this.situationAnimation += 4;
			else this.situationAnimation += 2;
		}else if(this.jumping == true && jumping == false && this.isMoving == false){
			//le perso retombe
			this.situationAnimation -= 4;
		}
		else if(this.jumping == true && jumping == false && this.isMoving == true){
			//le perso retombe en bougeant
			this.situationAnimation -= 2;
		}
		else if(this.jumping == false && jumping == true && this.isMoving == true){
			//le perso saute en bougeant
			this.situationAnimation += 2;
		}
		this.jumping = jumping;
	}


	@Override
	public void afficher(GameContainer conteneur, Graphics graphique) throws SlickException {
		
		super.afficher( conteneur, graphique );
		
		graphique.drawAnimation( this.animations[this.situationAnimation], this.getPositionX(), this.getPositionY() );		
	}

	public int getNbPoints() {
		return this.nbPoints;
	}


}
