/**
 * 
 */

/**
 * Represents a function that checks to see if a proj. damaged a bunker
 * 
 * @author briandesnoyers
 * @version April 10, 2014
 */
class ProjDamageBunker implements IFunction<AProj, Boolean>
{

    // Dpes the given proj damage bunkers?
    public Boolean apply(AProj proj)
    {
        return proj.damageBunker();
    }
}
