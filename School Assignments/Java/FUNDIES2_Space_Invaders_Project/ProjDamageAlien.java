/**
 * 
 */

/**
 * Function that checks to see if a proj. damaged an alien
 * @author briandesnoyers
 * @version April 10, 2014
 */
public class ProjDamageAlien implements IFunction<AProj, Boolean>
{

	//Does the given proj damage Aliens?
    public Boolean apply(AProj proj)
    {
        return proj.damageAlien();
    }
}
