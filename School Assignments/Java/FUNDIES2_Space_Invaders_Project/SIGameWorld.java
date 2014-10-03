import java.util.ArrayList;
import java.util.Random;

import javalib.colors.*;
import javalib.funworld.*;
import javalib.worldimages.*;

/**
 * 
 */

/**
 * The main game for Space Invaders
 * 
 * @author briandesnoyers
 * @version April 10, 2014
 */
public class SIGameWorld extends World
{

    APlayer player;
    ArrayList<ArrayList<AAlien>> aliens;
    ArrayList<AUfo> ufos;
    ArrayList<AProj> projs;
    ArrayList<ABunker> bunkers;
    ArrayList<APowerup> powerups;
    int score;
    int level;

    SIGameWorld(APlayer p, ArrayList<ArrayList<AAlien>> a, ArrayList<AUfo> u,
            ArrayList<AProj> pr, ArrayList<ABunker> b,
            ArrayList<APowerup> powerups, int s, int l)
    {
        this.player = p;
        this.aliens = a;
        this.ufos = u;
        this.projs = pr;
        this.bunkers = b;
        this.powerups = powerups;
        this.score = s;
        this.level = l;
    }

    // -------------------------------------------------------------------------
    // Drawing Functions

    // Returns the image that will be the background of the game
    public WorldImage background()
    {
        int pinholex = Global.width / 2;
        int pinholey = Global.height / 2;

        return new RectangleImage(new Posn(pinholex, pinholey), Global.width,
                Global.height, new Black());
    }

    // Returns the players and life bar drawn onto a given background
    public WorldImage drawPlayerAndLivesOnto(WorldImage background)
    {
        int playerLives = this.player.getLives();
        WorldImage result = new OverlayImages(this.player.drawOn(background),
                new TextImage(new Posn(Global.lifex, Global.lifey), "LIVES: ",
                        new White()));
        int currentPosition = Global.lifex + 2 * Global.spaceBetweenLifeIcons;

        for (int i = playerLives; i > 0; i = i - 1)
        {
            result = new OverlayImages(result,
                    Global.drawLifeSpriteHere(new Posn(currentPosition,
                            Global.lifey)));
            currentPosition = currentPosition + Global.spaceBetweenLifeIcons;
        }

        return result;

    }

    // Returns the image of all aliens drawn onto the given background
    public WorldImage drawAliensOnto(WorldImage background)
    {
        WorldImage result = background;

        for (ArrayList<AAlien> alienColumn : this.aliens)
        {
            for (AAlien alien : alienColumn)
                result = alien.drawOn(result);
        }

        return result;
    }

    // Returns the given image but with all of this worlds ufos drawn onto it
    public WorldImage drawUfosOnto(WorldImage background)
    {
        WorldImage result = background;

        for (AUfo ufo : this.ufos)
        {
            result = ufo.drawOn(result);
        }
        return result;
    }

    // Returns the given image but with all of this world's powerups drawn onto
    // it
    public WorldImage drawPowerupsOnto(WorldImage background)
    {
        WorldImage result = background;

        for (APowerup pu : this.powerups)
        {
            result = pu.drawOn(result);
        }
        return result;
    }

    // Returns the image of all projectiles drawn onto a given background
    public WorldImage drawProjsOnto(WorldImage background)
    {
        WorldImage result = background;

        for (AProj proj : this.projs)
        {
            result = proj.drawOn(result);
        }
        return result;
    }

    // Returns the image of all bunkers drawn onto a given background
    public WorldImage drawBunkersOnto(WorldImage background)
    {
        WorldImage result = background;

        for (ABunker bunker : this.bunkers)
        {
            result = bunker.drawOn(result);
        }
        return result;
    }

    // Returns an image with the current score drawn with specified coordinates
    // onto a given background
    public WorldImage drawScoreOnto(WorldImage background)
    {
        return new OverlayImages(background, new TextImage(new Posn(
                Global.scorex, Global.scorey), "SCORE: " + this.score,
                new White()));
    }

    // Returns an image representing everything in the game world
    public WorldImage makeImage()
    {
        return this.drawPlayerAndLivesOnto(this.drawScoreOnto(this
                .drawProjsOnto(this.drawUfosOnto(this.drawAliensOnto(this
                        .drawBunkersOnto(this.drawPowerupsOnto(this
                                .background())))))));
    }

    // -------------------------------------------------------------------------
    // OnTick

    // Returns the next state of the game world
    // Projectiles are moved, enemies are injured by projectiles, empty columns
    // of aliens are removed from the list, if all the aliens are dead, it goes
    // to the next level,
    // players and bunkers are injured by projectiles, aliens move and shoot
    public World onTick()
    {

        /**
         * OnTick must do... -Move projectiles and delete if out of bounds. DONE
         * -Remove projectiles and enemies that projectiles killed. Also must
         * determine if player was hit and if they lose a life. DONE -Bunkers
         * are also hit by projs. DONE -Move aliens and have them shoot. DONE
         * -If no aliens left, next level.
         */

        this.moveProjs();
        this.addPowerup();
        this.enemyProjInteraction();
        this.cleanUpEmptyColumns();
        if (shouldChangeLevel())
        {
            return this.nextLevel();
        }
        this.ufoProjInteraction();
        this.playerProjInteraction();
        this.bunkerProjInteraction();
        this.playerPowerupInteraction();

        this.aliensMove();
        this.createPotentialUfo();
        this.ufosMove();
        this.aliensShoot();

        return this;
    }

    // Adds powerup at random interval
    public void addPowerup()
    {
        if (((int) (Math.random() * Global.POWER_UP_PROB)) == 0)
        {
            if (((int) (Math.random() * 2)) == 0)
            {
                this.powerups.add(new FastShotPowerup());
            } else
            {
                this.powerups.add(new ExtraLifePowerup());
            }
        }
    }

    // Moves projs and deletes those that are out of bounds
    // EFFECT: Filters out projs that are out of bounds.
    public void moveProjs()
    {

        ArrayList<AProj> result = new ArrayList<AProj>();

        for (AProj p : this.projs)
        {
            p.move();
            if (!p.outOfBounds())
            {
                result.add(p);
            }
        }

        this.projs = result;
    }

    // Damages aliens that have been hit with projs and removes them if their
    // health is below
    // zero.
    // EFFECT: Removes projs that have hit enemies. Kills enemies / modifies
    // their health.
    // Also adds to score if enemy was hit.
    public void enemyProjInteraction()
    {

        ArrayList<AProj> projArray = this.projs;
        ArrayList<ArrayList<AAlien>> alienArrayResult = new ArrayList<ArrayList<AAlien>>();

        for (ArrayList<AAlien> alienColumn : this.aliens)
        {
            ArrayList<AAlien> alienColumnResult = new ArrayList<AAlien>();
            for (AAlien alien : alienColumn)
            {
                // Does a good proj overlap with this alien?
                if (alien.overlaps(projArray))
                {
                    // Removes the proj from the array and returns it
                    // and then damages the alien with it
                    alien.damage(alien.removeProjAndReturn(projArray));
                    // if alien isn't dead, add it to the result
                    if (!alien.dead())
                    {
                        alienColumnResult.add(alien);
                        // Otherwise, alien is dead so add to score but do not
                        // keep alien
                    } else
                    {
                        this.score = this.score + alien.score();
                    }
                } else
                {
                    alienColumnResult.add(alien);
                }
            }
            alienArrayResult.add(alienColumnResult);
        }

        this.aliens = alienArrayResult;
        this.projs = projArray;
    }

    // Removes the ArrayLists<AAlien> that are empty in the alien field
    // EFFECT:Removes columns from the alien list that have no aliens in them.
    public void cleanUpEmptyColumns()
    {
        ArrayList<ArrayList<AAlien>> result = new ArrayList<ArrayList<AAlien>>();

        for (int i = 0; i < this.aliens.size(); i = i + 1)
        {
            if (this.aliens.get(i).size() > 0)
            {
                result.add(this.aliens.get(i));
            }
        }

        this.aliens = result;
    }

    // EFFECT: Removes Projs that are on ufos and damages the ufo
    public void ufoProjInteraction()
    {

        ArrayList<AProj> projArray = this.projs;
        ArrayList<AUfo> ufoArrayResult = new ArrayList<AUfo>();

        for (AUfo ufo : this.ufos)
        {
            // Does this bunker overlap with any proj?
            if (ufo.overlaps(projArray))
            {
                ufo.damage(ufo.removeProjAndReturn(projArray));
                if (!ufo.dead())
                {
                    ufoArrayResult.add(ufo);
                } else
                {
                    this.score = this.score + ufo.score;
                }
            } else
            {
                ufoArrayResult.add(ufo);
            }
        }

        this.ufos = ufoArrayResult;

    }

    // EFFECT: Removes Projs that are on a player and damages the player
    public void playerProjInteraction()
    {

        // If player overlaps with bad Proj
        if (player.overlap(this.projs))
        {
            // Damage player (minus health/life) with proj
            player.damage(player.removeProjAndReturn(this.projs));
        }

    }

    // EFFECT: Removes Powerups that are on a player and applies overlapping
    // powerups to player
    public void playerPowerupInteraction()
    {
        ArrayList<APowerup> result = new ArrayList<APowerup>();
        for (APowerup pu : this.powerups)
        {
            if (player.boxOverlap(pu))
            {
                this.player = pu.apply(this.player);
            } else
            {
                result.add(pu);
            }
        }
        this.powerups = result;
    }

    // EFFECT: Removes projs that are on bunkers and damages those bunkers
    // according to how much each proj damages
    public void bunkerProjInteraction()
    {
        ArrayList<AProj> projArray = this.projs;
        ArrayList<ABunker> bunkerArrayResult = new ArrayList<ABunker>();

        for (ABunker bunker : this.bunkers)
        {
            // Does this bunker overlap with any proj?
            if (bunker.overlap(projArray))
            {
                bunker.damage(bunker.removeProjAndReturn(projArray));
                if (!bunker.dead())
                {
                    bunkerArrayResult.add(bunker);
                }
            } else
            {
                bunkerArrayResult.add(bunker);
            }
        }
        this.bunkers = bunkerArrayResult;
    }

    // EFFECT: Moves aliens across the screen and down, slowly moving them to
    // the bottom
    public void aliensMove()
    {

        AAlien leftMostAlien = this.aliens.get(0).get(0);
        int xspeed = leftMostAlien.vel.dx;
        int nextX = xspeed + leftMostAlien.p.x;

        // If going left
        if (xspeed < 0)
        {
            if (leftMostAlien.leftOf(Global.alienLeftBorder) || nextX < 0)
            {
                for (ArrayList<AAlien> alienColumn : this.aliens)
                {
                    for (AAlien alien : alienColumn)
                    {
                        alien.moveDownAndChangeDir();
                    }
                }
            } else
            {
                for (ArrayList<AAlien> alienColumn : this.aliens)
                {
                    for (AAlien alien : alienColumn)
                    {
                        alien.move();
                    }
                }
            }
        }

        // If going right
        if (xspeed > 0)
        {
            AAlien rightMostAlien = this.aliens.get(this.aliens.size() - 1)
                    .get(0);
            int nextOtherX = xspeed + rightMostAlien.p.x;

            if (rightMostAlien.rightOf(Global.alienRightBorder)
                    || nextOtherX > Global.width)
            {
                for (ArrayList<AAlien> alienColumn : this.aliens)
                {
                    for (AAlien alien : alienColumn)
                    {
                        alien.moveDownAndChangeDir();
                    }
                }
            } else
            {
                for (ArrayList<AAlien> alienColumn : this.aliens)
                {
                    for (AAlien alien : alienColumn)
                    {
                        alien.move();
                    }
                }
            }
        }

        // If not moving(???)
        if (xspeed == 0)
        {
            throw new RuntimeException(
                    "aliensMove- Aliens should always have movement");
        }
    }

    // EFFECT: Makes some of the aliens at the bottoms of each column randomly
    // fire a projectile to potentially harm the player
    public void aliensShoot()
    {

        for (ArrayList<AAlien> alienColumn : this.aliens)
        {
            if (new Random().nextInt(100) == 0)
            {
                AProj newProj = alienColumn.get(alienColumn.size() - 1)
                        .createProj();

                this.projs.add(newProj);
            }
        }
    }

    public void createPotentialUfo()
    {
        if (new Random().nextInt(Global.SAUCER_PROB) == 0
                && this.ufos.size() == 0)
        {
            this.ufos.add(new StandardUfo(new Posn(Global.SAUCER_START_X,
                    Global.SAUCER_START_Y), new Vel(Global.SAUCER_SPEED, 0)));
        }
    }

    public void ufosMove()
    {
        if (this.ufos.size() > 0)
        {
            ArrayList<AUfo> result = new ArrayList<AUfo>();
            for (AUfo ufo : this.ufos)
            {
                ufo.move();
                if (!ufo.leftOf(0))
                {
                    result.add(ufo);
                }
            }
            this.ufos = result;
        }
    }

    // Should the level be changed? (Are there zero aliens left on the screen?)
    public boolean shouldChangeLevel()
    {
        for (ArrayList<AAlien> alienColumn : this.aliens)
        {
            if (!(alienColumn.size() == 0))
            {
                return false;
            }
        }

        return true;
    }

    // Produces the next level
    public World nextLevel()
    {
        return LevelManager.produceLevel(this);
    }

    // -------------------------------------------------------------------------
    // OnKeyEvent

    // EFFECT: When the user presses a button, depending on the button,
    // moves the player or creates a player projectile
    public World onKeyEvent(String ke)
    {
        if ((ke.equals("left") && this.player.p.x > 0)
                || (ke.equals("right") && this.player.p.x < Global.width))
        {
            this.player.keyEvent(ke);
        } else if (ke.equals("x") && this.player.canShoot(this.projs))
        {
            this.projs.add(this.player.createProj());
            this.player = this.player.shoot();
        }

        return this;
    }

    // Are there any player projs on the screen?
    public boolean noPlayerProj()
    {
        for (AProj proj : this.projs)
        {
            if (proj.damageAlien())
            {
                return false;
            }
        }

        return true;
    }

    // -------------------------------------------------------------------------
    // GameEnd

    /**
     * Game ends when... -Aliens reach certain y coordinate after advancing
     * -There are no lives left
     */

    // If the aliens have reached the bottom of the screen or the player has
    // no lives, ends the game and displays a game over screen.
    public WorldEnd worldEnds()
    {
        if (this.aliensReachedLine() || this.playerHasNoLives())
        {
            return new WorldEnd(true, this.gameOverScreen("GAME OVER"));
        } else
        {
            return new WorldEnd(false, this.makeImage());
        }
    }

    // Returns an image displaying the words "GAME OVER"
    public WorldImage gameOverScreen(String msg)
    {
        return new OverlayImages(this.background(), new TextImage(new Posn(
                Global.width / 2, Global.height / 2), msg, new White()));
    }

    // Have the aliens reached the specified y coordate yet?
    public boolean aliensReachedLine()
    {

        for (ArrayList<AAlien> alienColumn : this.aliens)
        {
            if (alienColumn.get(alienColumn.size() - 1).underThis(
                    Global.alienDeathLiney))
            {
                return true;
            }
        }

        return false;
    }

    // Does the player have any lives remaining?
    public boolean playerHasNoLives()
    {
        return this.player.getLives() == 0;
    }

}
