import javalib.worldimages.FromFileImage;
import javalib.worldimages.WorldImage;

/**
 * 
 */

/**
 * Represents an extra life powerup in the Space Invaders game
 * 
 * @author briandesnoyers
 * @version April 16, 2014
 */
public class ExtraLifePowerup extends APowerup
{
    
    // Returns a rendered image of this actor
    public WorldImage render()
    {
        return new FromFileImage(this.p, "ExtraLife.png");
    }

    // Applies this powerup to player old
    public APlayer apply(APlayer old)
    {
        old.addLife();
        return old;
    }
}
