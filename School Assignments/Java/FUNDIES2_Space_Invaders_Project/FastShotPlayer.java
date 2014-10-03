import java.util.ArrayList;

import javalib.worldimages.*;

/**
 * 
 */

/**
 * Represents a Space Invaders player that can shoot unlimited projs.
 * 
 * @author briandesnoyers
 * @version April 15, 2014
 */
public class FastShotPlayer extends StandardPlayer
{
    private int count;

    // Constructor
    public FastShotPlayer(int x, int lives, int health)
    {
        super(x, lives, health);
        this.count = 0;
    }

    // Can this player shoot?
    // Overrides method in StandardPlayer
    public boolean canShoot(ArrayList<AProj> projs)
    {
        return true;
    }

    // Returns an image representation of this player
    // Overrides render in Standard Player
    public WorldImage render()
    {
        return new OverlayImages(new FromFileImage(p, "Player.png"),
                new FromFileImage(new Posn(p.x, p.y + 20), "SmallPowerup.png"));
    }
    
    //Accessor for the count field of this player
    public int count()
    {
    	return this.count;
    }

    // EFFECT: Gives back this player after shooting
    // Returns this player.
    public APlayer shoot()
    {
        if (this.count < Global.POWER_UP_DUR)
        {
            count++;
            return this;
        } else
        {
            return new StandardPlayer(this.p.x, this.getLives(), this.health);
        }
    }

}
