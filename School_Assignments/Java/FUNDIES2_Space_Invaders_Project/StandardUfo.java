import javalib.worldimages.Posn;
import javalib.worldimages.*;

/**
 * 
 */

/**
 * Represents a UFO alien for the Space Invaders game which occassionally flies
 * across the screen
 * 
 * @author William
 *
 */

public class StandardUfo extends AUfo
{

    StandardUfo(Posn p, Vel v)
    {
        super(40, 30, p, 1, v, 200);
    }

    // Returns an image representation of this UFO
    public WorldImage render()
    {
        return new FromFileImage(this.p, "Saucer.png");
    }

}
