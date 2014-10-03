import javalib.worldimages.Posn;

/**
 * 
 */

/**
 * Represents a shootable moving actor
 * @author briandesnoyers
 * @version April 10, 2014
 */

public abstract class AShootableMovingActor extends AShootableActor
{

    Vel vel;

    AShootableMovingActor(int w, int h, Posn p, int health, Vel v)
    {
        super(w, h, p, health);
        this.vel = v;
    }

    //EFFECT: Moves this actor accordingly based on its current velocity
    public void move()
    {
        this.p.x = this.p.x + this.vel.dx;
        this.p.y = this.p.y + this.vel.dy;
    }
}