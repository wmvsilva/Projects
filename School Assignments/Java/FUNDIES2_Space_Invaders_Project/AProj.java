import javalib.colors.*;
import javalib.worldimages.*;

/**
 * 
 */

/**
 * Represnts an abstract proj.
 * 
 * @author briandesnoyers
 * @version April 10, 2014
 */

public abstract class AProj extends AMovingActor
{

    int damage;

    AProj(int w, int h, Posn p, Vel v, int d)
    {
        super(w, h, p, v);
        this.damage = d;
    }

    // Renders this proj. as an Image
    public WorldImage render()
    {
        return new RectangleImage(this.p, Global.defaultProjWidth,
                Global.defaultProjHeight, new White());
    }

    // Returns an image with this projectile rendered onto the given background
    public WorldImage drawOn(WorldImage background)
    {
        return new OverlayImages(background, this.render());
    }

    // Is this projectile not within the bounds of the game screen?
    public boolean outOfBounds()
    {
        return this.p.y < 0 || this.p.y > Global.height || this.p.x < 0
                || this.p.x > Global.width;
    }

    // Does this projectile damage players?
    public boolean damagePlayer()
    {
        return false;
    }

    // Does this projectile damage aliens?
    public boolean damageAlien()
    {
        return false;
    }

    // Does this projectile damage bunkers?
    public boolean damageBunker()
    {
        return true;
    }
}
