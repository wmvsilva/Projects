import java.util.ArrayList;

import javalib.worldimages.*;

/**
 * 
 */

/**
 * Represents the standard player of the Space Invaders game (without powerups)
 * @author briandesnoyers
 * @version April 10, 2014
 */

class StandardPlayer extends APlayer
{

    StandardPlayer(int x, int lives, int health)
    {
        super(Global.playerWidth, Global.playerHeight, new Posn(x,
                Global.playery), health, lives);
    }

    StandardPlayer(int w, int h, Posn p, int health, int lives)
    {
        super(w, h, p, health, lives);
        this.width = Global.playerWidth;
        this.height = Global.playerHeight;
        this.p.y = Global.playery;
        this.health = Global.DEFAULT_PLAYER_HEALTH;
        this.setLives(Global.defaultPlayerLives);
    }

    //Returns an image representation of this player
    public WorldImage render()
    {
        return new FromFileImage(p, "Player.png");
    }

    //Returns a projectile at this player's position that will damage aliens
    public AProj createProj()
    {
        return new StandardProj(new Posn(this.p.x, this.p.y));
    }

    // Can this player shoot?
    // Overrides method in APlayer
    public boolean canShoot(ArrayList<AProj> projs)
    {
        for (AProj proj : projs)
        {
            if (proj.damageAlien())
            {
                return false;
            }
        }
        return true;
    }

}
