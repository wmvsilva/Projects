import java.util.ArrayList;

import javalib.worldimages.Posn;
import javalib.worldimages.WorldImage;

/**
 * 
 */

/**
 * Represents an abstract Alien in the Space Invaders game
 * 
 * @author briandesnoyers
 * @version April 10, 2014
 */

public abstract class AAlien extends AShootableMovingActor
{

    int score;
    int timer;
    int timeUntilMove;

    AAlien(int w, int h, Posn p, int health, Vel v, int score, int t, int tum)
    {
        super(w, h, p, health, v);
        this.score = score;
        this.timer = t;
        this.timeUntilMove = tum;
    }

    // Returns a rendered image of this alien
    public WorldImage render()
    {
        throw new RuntimeException("render- Cannot render abstract alien");
    }

    // Returns the score that this alien is worth when killed
    public int score()
    {
        return this.score;
    }

    // Is this Alien to the left of this point?
    public boolean leftOf(int x)
    {
        return this.p.x < x;
    }

    // Is this Alien to the right of this point?
    public boolean rightOf(int x)
    {
        return this.p.x > x;
    }

    // Is the alien under this point?
    public boolean underThis(int y)
    {
        return this.p.y >= y;
    }

    // EFFECT: Modifies the health of this alien by subtracting how much damage
    // the given proj does
    public void damage(AProj proj)
    {
        this.health = this.health - proj.damage;
    }

    // Do any projs that hurt aliens overlap with this alien?
    public boolean overlaps(ArrayList<AProj> projArray)
    {
        return this.overlapWFunction(projArray, new ProjDamageAlien());
    }

    // Creates a projectile at this point that can harm the player
    public AProj createProj()
    {
        throw new RuntimeException(
                "createProj- Abstract alien cannot create proj");
    }

    // Returns a projectile from the given ArrayList that can harm an alien
    // EFFECT: Removes the proj that it is returning from the given ArrayList
    public AProj removeProjAndReturn(ArrayList<AProj> projArray)
    {
        return this.removeProjAndReturnWFunction(projArray,
                new ProjDamageAlien());
    }

    // EFFECT: Moves this alien down a set amount and reverses the x-component
    // of the velocity
    public void moveDownAndChangeDir()
    {
        this.p.y = this.p.y + Global.defaultAlienDown;
        this.vel.dx = this.vel.dx * -1;
    }
}
