import javalib.worldimages.*;

/**
 * 
 */

/**
 * Global variables and utility methods for the Space Invaders game
 * 
 * @author briandesnoyers
 * @version April 10, 2014
 */

public class Global
{
    public static final int width = 600;
    public static final int height = 800;

    public static final int scorex = 60;
    public static final int scorey = 20;

    public static final int lifex = width - 100;
    public static final int lifey = 20;
    public static final int spaceBetweenLifeIcons = 20;

    public static final int alienDeathLiney = height - 150;

    public static final int playerWidth = 50;
    public static final int playerHeight = 30;
    public static final int playery = height - playerHeight;
    public static final int playerMovementSpeed = 5;
    public static final int defaultPlayerDamage = 10;
    public static final int DEFAULT_PLAYER_HEALTH = 1;
    public static final int defaultPlayerLives = 3;
    
    public static final int defaultProjWidth = 5;
    public static final int defaultProjHeight = 5;

    public static WorldImage drawLifeSpriteHere(Posn p)
    {
        return new FromFileImage(p, "Life.png");
    }


    public static int defaultAlienWidth = 40;
    public static int defaultAlienHeight = 30;
    public static int defaultAlienSpeed = 40;
    public static int alienLeftBorder = 20;
    public static int alienRightBorder = width - 20;
    public static int defaultAlienDown = 10;

    public static final int SAUCER_WIDTH = 60;
    public static final int SAUCER_HEIGHT = 30;
    public static final int SAUCER_START_X = width;
    public static final int SAUCER_START_Y = 64;
    public static final int SAUCER_SPEED = -3;
    public static final int SAUCER_PROB = 1000;
    
    public static final int BUNKER_HEALTH = 6;
    public static final int BUNKER_SIDE = 15;
    
    public static final int POWER_UP_DUR = 30;
    public static final int POWER_UP_WIDTH = 40;
    public static final int POWER_UP_HEIGHT = 20;
    public static final int POWER_UP_PROB = 1000;
    
}
