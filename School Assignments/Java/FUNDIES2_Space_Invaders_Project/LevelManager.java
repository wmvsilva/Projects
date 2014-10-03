import java.util.ArrayList;

import javalib.funworld.World;
import javalib.worldimages.Posn;

/**
 * 
 */

/**
 * A Utility class used to produce levels of the Space Invaders game
 * 
 * @author briandesnoyers
 * @version April 10, 2014
 */

public class LevelManager
{

    // Produces a default world for the specified given level
    public static World produceLevel(int l)
    {
        return produceLevel(l, new StandardPlayer(390, 3,
                Global.DEFAULT_PLAYER_HEALTH), makeBunkers(), 0);
    }

    // Produces a world for the specified level l containing the given player,
    // bunkers, and score.
    public static World produceLevel(int l, APlayer player,
            ArrayList<ABunker> bunkers, int score)
    {
        // Create empty list of projectiles
        ArrayList<AProj> projs = new ArrayList<AProj>();

        // Set up list of Columns of AAliens
        ArrayList<ArrayList<AAlien>> aliens;

        // Create empty list of ufos
        ArrayList<AUfo> ufos = new ArrayList<AUfo>();

        // Create empty list of powerups
        ArrayList<APowerup> powerups = new ArrayList<APowerup>();

        switch (l)
        {
            case 1: // Set up the aliens (3 columns, 2 rows)
            aliens = makeAliens(3, 2);
                break;

            case 2: // Set up the aliens (5 columns, 4 rows)
            aliens = makeAliens(5, 4);
                break;

            case 3: // Set up the aliens (9 columns, 8 rows)
            aliens = makeAliens(9, 8);
                break;

            case 4: // Player "beat" the game!!!
            return new SIEnd();

            default:
            throw new RuntimeException("Level does not exist");
        }

        return new SIGameWorld(player, aliens, ufos, projs, bunkers, powerups,
                score, l);
    }

    // Returns the appropriate next level for the given world
    public static World produceLevel(SIGameWorld old)
    {
        return produceLevel(old.level + 1, old.player, old.bunkers, old.score);
    }

    // Produces an a row of columns of aliens that contains c columns and r rows
    public static ArrayList<ArrayList<AAlien>> makeAliens(int c, int r)
    {
        int y = 100;
        int x = 0;
        ArrayList<ArrayList<AAlien>> aliens = new ArrayList<ArrayList<AAlien>>();

        for (int i = 0; i < c; i++)
        {
            ArrayList<AAlien> alienColumn = new ArrayList<AAlien>();
            for (int j = 0; j < r; j++)
            {
                alienColumn.add(new StandardAlien(new Posn(x, y), ((int) (Math
                        .random() * 3)) + 1, 3));
                y += 40;
            }
            aliens.add(alienColumn);
            y = 100;
            x += 50;
        }

        return aliens;
    }

    // Produces a pre-made set of bunkers in positions similar to
    // the original Space Invaders game
    public static ArrayList<ABunker> makeBunkers()
    {
        // Set up new bunkers (fixes old ones if they're broken)
        ArrayList<ABunker> bunkers = new ArrayList<ABunker>();
        bunkers.add(new StandardBunker(new Posn(72, 700)));
        bunkers.add(new StandardBunker(new Posn(72, 715)));
        bunkers.add(new StandardBunker(new Posn(72, 730)));
        bunkers.add(new StandardBunker(new Posn(87, 700)));
        bunkers.add(new StandardBunker(new Posn(102, 700)));
        bunkers.add(new StandardBunker(new Posn(117, 700)));
        bunkers.add(new StandardBunker(new Posn(117, 715)));
        bunkers.add(new StandardBunker(new Posn(117, 730)));

        bunkers.add(new StandardBunker(new Posn(204, 700)));
        bunkers.add(new StandardBunker(new Posn(204, 715)));
        bunkers.add(new StandardBunker(new Posn(204, 730)));
        bunkers.add(new StandardBunker(new Posn(219, 700)));
        bunkers.add(new StandardBunker(new Posn(234, 700)));
        bunkers.add(new StandardBunker(new Posn(249, 700)));
        bunkers.add(new StandardBunker(new Posn(249, 715)));
        bunkers.add(new StandardBunker(new Posn(249, 730)));

        bunkers.add(new StandardBunker(new Posn(336, 700)));
        bunkers.add(new StandardBunker(new Posn(336, 715)));
        bunkers.add(new StandardBunker(new Posn(336, 730)));
        bunkers.add(new StandardBunker(new Posn(351, 700)));
        bunkers.add(new StandardBunker(new Posn(366, 700)));
        bunkers.add(new StandardBunker(new Posn(381, 700)));
        bunkers.add(new StandardBunker(new Posn(381, 715)));
        bunkers.add(new StandardBunker(new Posn(381, 730)));

        bunkers.add(new StandardBunker(new Posn(468, 700)));
        bunkers.add(new StandardBunker(new Posn(468, 715)));
        bunkers.add(new StandardBunker(new Posn(468, 730)));
        bunkers.add(new StandardBunker(new Posn(483, 700)));
        bunkers.add(new StandardBunker(new Posn(498, 700)));
        bunkers.add(new StandardBunker(new Posn(513, 700)));
        bunkers.add(new StandardBunker(new Posn(513, 715)));
        bunkers.add(new StandardBunker(new Posn(513, 730)));

        return bunkers;
    }

}