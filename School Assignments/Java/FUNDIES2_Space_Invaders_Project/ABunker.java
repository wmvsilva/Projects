import java.util.ArrayList;

import javalib.worldimages.*;

/**
 * 
 */

/**
 * Represents an abstract bunker
 * 
 * @author briandesnoyers
 * @version April 10, 2014
 */

public abstract class ABunker extends AShootableActor
{

    //Constructor
    public ABunker(Posn p)
    {
        super(Global.BUNKER_SIDE, Global.BUNKER_SIDE, p, Global.BUNKER_HEALTH);
    }

    // Override render
    // Draw's this bunker
    public WorldImage render()
    {
        throw new RuntimeException("Render called on abstract bunker");
    }

    //Is this bunker destroyed?
    public boolean dead()
    {
        return this.health <= 0;
    }

    //EFFECT: Decreases the health of this bunker by subtracting the damage
    //caused by the given projectile.
    public void damage(AProj proj)
    {
        this.health = this.health - proj.damage;
    }

    //Do any projs that hurt bunkers in the given ArrayList overlap with this bunker?
    public boolean overlap(ArrayList<AProj> arrayList)
    {
        return this.overlapWFunction(arrayList, new ProjDamageBunker());
    }

    //Returns a projectile from the ArrayList that overlaps with this bunker
    //and damages bunkers
    //EFFECT: Removes the returned proj from the given ArrayList
    public AProj removeProjAndReturn(ArrayList<AProj> projArray)
    {
        return this.removeProjAndReturnWFunction(projArray,
                new ProjDamageBunker());
    }
}
