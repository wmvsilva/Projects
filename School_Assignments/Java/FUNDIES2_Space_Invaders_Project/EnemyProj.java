import javalib.worldimages.*;

/**
 * 
 */

/**
 * Represent's an enemy's proj.
 * 
 * @author briandesnoyers
 * @version April 10, 2014
 */
public class EnemyProj extends AProj
{

    EnemyProj(Posn p)
    {
    	super(2, 4, p, new Vel(0, 5), 1);
    }

    //Override
    //Does this proj damage the player?
    public boolean damagePlayer()
    {
        return true;
    }
}
