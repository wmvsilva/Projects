import javalib.worldimages.*;
import javalib.colors.*;

/**
 * 
 */

/**
 * Represent's a player's non-enhanced projectile
 * 
 * @author briandesnoyers
 * @version April 10, 2014
 */
public class StandardProj extends AProj
{

    StandardProj(Posn p)
    {
        super(Global.defaultProjWidth, Global.defaultProjHeight, p, new Vel(0,
                -14), 1);
    }

    public boolean damageAlien()
    {
        return true;
    }

    // Renders this proj. as an Image
    // Overrides white proj. in AProj
    public WorldImage render()
    {
        return new RectangleImage(this.p, this.width, this.height, new Green());
    }

}
