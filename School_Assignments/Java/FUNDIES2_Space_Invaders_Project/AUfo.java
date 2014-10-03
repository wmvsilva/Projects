import java.util.ArrayList;

import javalib.worldimages.Posn;
import javalib.worldimages.WorldImage;

/**
 * 
 */

/**
 * Represents an abstract UFO enemy in the space invaders game.
 * 
 * @author William
 * @version April 14 2014
 *
 */
public class AUfo extends AShootableMovingActor
{

    int score;

    AUfo(int w, int h, Posn p, int health, Vel v, int score)
    {
        super(w, h, p, health, v);
        this.score = score;
    }

    // Returns a rendered image of this ufo
    public WorldImage render()
    {
        throw new RuntimeException("render- Cannot render abstract alien");
    }

    // Returns how many points this ufo is worth when destroyed.
    public int score()
    {
        return this.score;
    }

    // EFFECT: Damages this ufo based on how much the given proj damages.
    public void damage(AProj proj)
    {
        this.health = this.health - proj.damage;
    }

    // Returns a proj from the given projArray that can damage this ufo.
    public boolean overlaps(ArrayList<AProj> projArray)
    {
        return this.overlapWFunction(projArray, new ProjDamageAlien());
    }

    // Returns a proj from the given ProjArray that can damage this ufo.
    // EFFECT: Removes the returned proj from the given projArray.
    public AProj removeProjAndReturn(ArrayList<AProj> projArray)
    {
        return this.removeProjAndReturnWFunction(projArray,
                new ProjDamageAlien());
    }

    // EFFECT: Changes the velocity of this ufo to the given v.
    public void changeVel(Vel v)
    {
        this.vel = v;
    }

    // Is this UFO to the left of this point?
    public boolean leftOf(int x)
    {
        return this.p.x < x;
    }
}
