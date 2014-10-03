import javalib.worldimages.*;

/**
 * 
 */

/**
 * Represents an abstract powerup
 * 
 * @author briandesnoyers
 * @version April 16, 2014
 */
public abstract class APowerup extends AActor
{
    
    public APowerup()
    {
        super(Global.POWER_UP_WIDTH, Global.POWER_UP_HEIGHT, new Posn(
                ((int) (Math.random() * (Global.width - 100))) + 50,
                Global.playery));
    }

    // Returns a rendered image of this actor
    public WorldImage render()
    {
        throw new RuntimeException("render called on APowerup");
    }
    
    // Applies this powerup to player old
    public APlayer apply(APlayer old)
    {
        return old;
    }
}
