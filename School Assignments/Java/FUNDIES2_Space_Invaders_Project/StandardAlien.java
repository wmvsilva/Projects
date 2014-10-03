import javalib.worldimages.*;

/**
 * 
 */

/**
 * Represents a standard alien for Space Invaders that moves back and forth across the screen
 * @author briandesnoyers
 * @version April 15 2014
 *
 */

public class StandardAlien extends AAlien
{

    int initHealth;

    StandardAlien(Posn p, int health, int score)
    {
        super(Global.defaultAlienWidth, Global.defaultAlienHeight, p, health,
                new Vel(Global.defaultAlienSpeed, 0), score,
                40, 40);
        
        this.initHealth = health;
    }

    //Returns an image of this alien dependent on the original health
    //of this alien
    public WorldImage render()
    {
        switch(this.initHealth)
        {
            case 1: return new FromFileImage(p, "Alien1.png");
            case 2: return new FromFileImage(p, "Alien2.png");
            default: return new FromFileImage(p, "Alien3.png");
        }
    }

    //Creates an alien projectile moving down at this alien's position
    //to damage the player
    public AProj createProj()
    {
        return new EnemyProj(new Posn(this.p.x, this.p.y));
    }

    //EFFECT: Ticks down the timer. When this alien's timer is zero, the alien
    //is moved according to its velocity
    public void move()
    {
    	if (this.timer == 0)
    	{
        super.move();
        this.timer = this.timeUntilMove;
        
    	}else{
    		this.timer = this.timer - 1;
    	}
    }
    
    //EFFECT: Ticks down the timer. When this alien's timer is zero, the alien
    // is moved down a specified amount and the velocity's x-direction is changed.
    public void moveDownAndChangeDir()
    {
    	if (this.timer == 0){
    		
    	
        super.moveDownAndChangeDir();
        this.timeUntilMove = this.timeUntilMove - 1;
        if (this.timeUntilMove <= 0)
        {
        	this.timeUntilMove = 0;
        }
        this.timer = this.timeUntilMove;
    	}else{
    		this.timer = this.timer - 1;
    	}
    }
}
