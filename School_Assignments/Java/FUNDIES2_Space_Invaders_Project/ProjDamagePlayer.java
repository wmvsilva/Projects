/**
 * 
 */

/**
 * Represents a function that checks if a proj. damaged the player
 * @author briandesnoyers
 * @version April 10, 2014
 */
public class ProjDamagePlayer implements IFunction<AProj, Boolean>
{

    public Boolean apply(AProj proj)
    {
    	//Does the given proj damage players?
        return proj.damagePlayer();
    }
}
