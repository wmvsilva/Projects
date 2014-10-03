import javalib.worldimages.*;

/**
 * 
 */

/**
 * @author briandesnoyers
 *
 */
public class FastShotPowerup extends APowerup
{

    // Returns a rendered image of this actor
    public WorldImage render()
    {
        return new FromFileImage(this.p, "Powerup.png");
    }

    // Applies this powerup to player old
    // Makes old a FastShotPlayer
    public APlayer apply(APlayer old)
    {
        return new FastShotPlayer(old.p.x, old.getLives(), old.health);
    }

}
