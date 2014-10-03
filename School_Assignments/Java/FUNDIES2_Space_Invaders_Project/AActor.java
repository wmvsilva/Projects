import javalib.worldimages.*;

/**
 * 
 */

/**
 * Represents an abstract Actor in Space Invaders game
 * 
 * @author briandesnoyers
 * @version April 10, 2014
 */

public abstract class AActor
{

    int width;
    int height;
    Posn p;

    public AActor(int w, int h, Posn p)
    {
        this.width = w;
        this.height = h;
        this.p = p;
    }

    // Returns the rendered actor overlayed onto the given background
    public WorldImage drawOn(WorldImage background)
    {
        return new OverlayImages(background, this.render());
    }

    // Returns a rendered image of this actor
    public WorldImage render()
    {
        throw new RuntimeException("render- AActor should not render.");
    }

    // Is the given posn within the boundaries of this actor?
    public boolean posnOverlap(Posn o)
    {
        return Math.abs(this.p.x - o.x) <= ((double) this.width) / 2
                && Math.abs(this.p.y - o.y) <= ((double) this.height) / 2;
    }

    // Is the given actor overlapping with this actor at all?
    public boolean boxOverlap(AActor that)
    {
        return Math.abs(this.p.x - that.p.x) <= (this.width + that.width)
                && Math.abs(this.p.x - that.p.x) <= (this.height + that.height);
    }
}
