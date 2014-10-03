import javalib.worldimages.FromFileImage;
import javalib.worldimages.Posn;
import javalib.worldimages.WorldImage;

/**
 * 
 */

/**
 * Represents a standard bunker for the Space Invaders game that protects the player
 * @author briandesnoyers
 *
 */

public class StandardBunker extends ABunker
{

    // Constructor
    public StandardBunker(Posn p)
    {
        super(p);
    }

    // Override render
    // Draw's this bunker
    public WorldImage render()
    {
        if (this.health < 3)
        {
            return new FromFileImage(this.p, "Bunker3.png");
        } else if (this.health < 5)
        {
            return new FromFileImage(this.p, "Bunker2.png");
        } else
        {
            return new FromFileImage(this.p, "Bunker1.png");
        }
    }

}
