import java.util.ArrayList;
import javalib.worldimages.Posn;

/**
 * 
 */

/**
 * Represents an abstract shootable actor
 * 
 * @author briandesnoyers
 * @version April 10, 2014
 */
public abstract class AShootableActor extends AActor
{

    int health;

    AShootableActor(int w, int h, Posn p, int health)
    {
        super(w, h, p);
        this.health = health;
    }

    //Has this shootable actor been destroyed?
    public boolean dead()
    {
        return this.health <= 0;
    }

    //EFFECT: Damages this actor according to how much the given projectile
    //damages.
    public void damage(AProj proj)
    {
        this.health = this.health - proj.damage;
    }

    //Are there any projectiles in the given projArray that can damage this actor according
    //to the given IFunction?
    public boolean overlapWFunction(ArrayList<AProj> projArray,
            IFunction<AProj, Boolean> determineIfDamage)
    {
        for (AProj proj : projArray)
        {
            if (determineIfDamage.apply(proj) && this.posnOverlap(proj.p))
            {
                return true;
            }
        }
        
        return false;
    }

    //Returns the proj from the given projArray that can damage this actor according
    //to the given IFunction.
    //EFFECT: Removes the returned proj from the given projArray.
    public AProj removeProjAndReturnWFunction(ArrayList<AProj> projArray,
            IFunction<AProj, Boolean> determineIfDamage)
    {
        for (int i = 0; i < projArray.size(); i = i + 1)
        {
            AProj proj = projArray.get(i);

            if (determineIfDamage.apply(proj) && this.posnOverlap(proj.p))
            {
                return projArray.remove(i);
            }
        }

        throw new RuntimeException("removeProjAndReturn- Proj not found.");

    }
}
