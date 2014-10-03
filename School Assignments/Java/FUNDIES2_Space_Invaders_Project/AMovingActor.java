import javalib.worldimages.*;

/**
 * 
 */

/**
 * @author briandesnoyers
 *
 */
public abstract class AMovingActor extends AActor
{

    Vel vel;

    AMovingActor(int w, int h, Posn p, Vel v)
    {
        super(w, h, p);
        this.vel = v;
    }

    //EFFECT: Changes the position of this actor according to the velocity
    public void move()
    {
        this.p.x = this.p.x + this.vel.dx;
        this.p.y = this.p.y + this.vel.dy;
    }
}
