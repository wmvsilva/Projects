import javalib.colors.*;
import javalib.funworld.*;
import javalib.worldimages.*;

/**
 * 
 */

/**
 * Represents the quitted state of the Space Invaders game
 * 
 * @author briandesnoyers
 * @version April 10, 2014
 */
public class SIFinal extends World
{

	//Renders a standard background image for this world
    public WorldImage makeImage()
    {
        int pinholex = Global.width / 2;
        int pinholey = Global.height / 2;

        return new RectangleImage(new Posn(pinholex, pinholey), Global.width,
                Global.height, new Black());
    }

    //Sets the world to end immediately and displays a message in memory of the best TA ever.
    public WorldEnd worldEnds()
    {
        return new WorldEnd(true, new OverlayImages(this.makeImage(),
                new TextImage(new Posn(Global.width / 2, Global.height / 2),
                        "CREDITS: DEDICATED TO CHRIS FREELEY (1994-2014)", new White())));
    }
}
