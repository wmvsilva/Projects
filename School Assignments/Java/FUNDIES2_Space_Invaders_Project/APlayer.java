import java.util.ArrayList;
import javalib.worldimages.*;

/**
 * 
 */

/**
 * Represents an abstract player (does not extend a moving actor because all
 * movement is linked to key events)
 * 
 * @author briandesnoyers
 * @date April 10, 2014
 */

public abstract class APlayer extends AShootableActor
{

    int lives;

    APlayer(int w, int h, Posn p, int health, int l)
    {
        super(w, h, p, health);
        this.lives = l;
    }
    
    // accessor for lives of player
    public int getLives()
    {
        return this.lives;
    }
    
    //EFFECT: Sets the lives of this player to the given l
    public void setLives(int l)
    {
        this.lives = l;
    }

    //Returns a rendered image of the player
    public WorldImage render()
    {
        throw new RuntimeException("render- Cannot render abstract player");
    }

    //EFFECT: Moves the player left or right depending on if the key event
    //sent is "left" or "right"
    public void keyEvent(String ke)
    {
        if (ke.equals("left"))
        {
            this.p.x = this.p.x - Global.playerMovementSpeed;
        } else
        {
            if (ke.equals("right"))
            {
                this.p.x = this.p.x + Global.playerMovementSpeed;
            } else
            {
                throw new RuntimeException(
                        "keyEvent- Invalid command sent to ship.");
            }
        }
    }

    //Are there any projs in the given arrayList that can damage the player and overlap
    //with the player?
    public boolean overlap(ArrayList<AProj> arrayList)
    {
        return this.overlapWFunction(arrayList, new ProjDamagePlayer());
    }

    //Returns a proj from the given ArrayList that overlaps with and can damage the player.
    //EFFECT: Removes the returned proj from the given ArrayList.
    public AProj removeProjAndReturn(ArrayList<AProj> arrayList)
    {
        return this.removeProjAndReturnWFunction(arrayList,
                new ProjDamagePlayer());
    }

    //Creates a proj at the player's coordinates that will damage Aliens
    public AProj createProj()
    {
        throw new RuntimeException(
                "createProj- Abstract player cannot create projs");
    }

    //EFFECT: Makes this player lose health according to how much the given proj damages.
    //If the player's health goes below zero, it is reset and a life is lost.
    public void damage(AProj proj)
    {
        this.health = this.health - proj.damage;
        if (this.health <= 0)
        {
            this.lives = this.lives - 1;
            this.health = Global.DEFAULT_PLAYER_HEALTH;
        }
    }
    
    // Can this player shoot?
    public boolean canShoot(ArrayList<AProj> projs)
    {
        throw new RuntimeException(
                "canShoot called on Abstract player");
    }
    
    // EFFECT: Gives back this player after shooting
    //         Returns this player.
    public APlayer shoot()
    {
        return this;
    }
    
    // EFFECT: adds one life to this player (if has less than 3)
    public void addLife()
    {
        if(this.lives < 3)
        {
            this.lives++;
        }
    }

}
