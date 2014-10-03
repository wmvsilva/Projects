import javalib.colors.*;
import javalib.funworld.*;
import javalib.worldimages.*;

/**
 * 
 */

/**
 * The win state of the Space Invaders game
 * @author briandesnoyers
 * @version April 10, 2014
 */
public class SIEnd extends World
{

	//Returns an image that represents the background image for this world
    public WorldImage background()
    {
        int pinholex = Global.width / 2;
        int pinholey = Global.height / 2;

        return new RectangleImage(new Posn(pinholex, pinholey), Global.width,
                Global.height, new Black());
    }

    //Renders an image representation of this world that contains a message
    //for the user on what to do next
    public WorldImage makeImage()
    {

        return new OverlayImages(this.background(), new TextImage(new Posn(
                Global.width / 2, Global.height / 2),
                "A WINNER IS YOU. PRESS 'R' TO RESTART OR 'Q' TO EXIT.",
                new White()));
    }

    //Returns a new world depending on what keythe user has pressed.
    //-If "r", the first level is produced.
    //-If "q", the final screen of the game is produced.
    public World onKeyEvent(String ke)
    {
        if (ke.equals("r"))
        {
            return LevelManager.produceLevel(1);
        } else
        {
            if (ke.equals("q"))
            {
                return new SIFinal();
            }
        }
        return this;
    }
}