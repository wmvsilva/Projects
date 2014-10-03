import javalib.worldimages.*;
import javalib.funworld.*;
import tester.*;
import javalib.colors.*;

import java.util.*;

/**
 * 
 */

/**
 * Examples for Space Invaders game
 * 
 * @author briandesnoyers
 * @version April 10, 2014
 */

// * A WorldState (WorldS) has a:
// * - Player
// * - ArrayList<Aliens>
// * - ArrayList<Proj>
// * - ArrayList<Bunker>
// *
// * A Player is a new Player(...)
// *
// * A Player can:
// * - on-key(): KE -> Player
// * - move player or shoot
// * - on-tick(): -> Player
// * - if hit, loose life (mutate ArrayList<Proj>)
// * - if over powerup, change player type
// * - draw-on(Image bkg): -> Image
// * - draws this on bkg
// *
// * An Alien (AAlien) is one of:
// * - new EZAlien(...)
// * - new DFAlien(...)
// * - new UFO(...)
// *
// * An Alien can:
// * - on-tick(): -> Alien
// * - advance downwards(or sideways)
// * - fire lasers is on bottom and based on random comp.
// * - die if shot (mutate ArrayList<Proj>)
// * - draw-on(Image bkg): Image -> Image
// * - draws itself on bkg
// *
// * A Proj (AProj) is one of:
// * - new ALProj(...)
// * - new PLProj(...)
// * - new RAPProj(...)
// * - new MultiProj(...)
// *
// * A Proj can:
// * - on-tick(): -> Proj
// * - advance proj
// * - draw-on(Image bkg): -> Image
// * - draws this proj. on bkg
// *
// * A Bunker is a new Bunker(...)
// *
// * A Bunker can:
// * - draw-on(Image bkg): -> Image
// * - draws this bunker on bkg
// * - filterProjs(): -> ArrayList<Proj>
// * - removes proj. that hits this bunker
// *

public class ExampleSpaceInv
{

    public void testEnemyProj(Tester t)
    {
        EnemyProj p1 = new EnemyProj(new Posn(1, 1));
        EnemyProj p2 = new EnemyProj(new Posn(-5, -5));

        t.checkExpect(p1.damagePlayer(), true);
        t.checkExpect(p1.damageAlien(), false);
        t.checkExpect(p1.damageBunker(), true);
        t.checkExpect(p1.outOfBounds(), false);
        t.checkExpect(p2.outOfBounds(), true);
        p1.move();
        t.checkExpect(p1.p.x, 1);
        t.checkExpect(p1.p.y, 6);
        t.checkExpect(p1.posnOverlap(p1.p), true);
        t.checkExpect(p2.posnOverlap(p1.p), false);
        t.checkExpect(p1.boxOverlap(p1), true);
        t.checkExpect(p2.boxOverlap(p1), false);
    }

    public void testExtraLifePowerup(Tester t)
    {
        ExtraLifePowerup pu1 = new ExtraLifePowerup();
        StandardPlayer play1 = new StandardPlayer(200, 3, 1);

        t.checkExpect(pu1.apply(play1), play1);
        t.checkExpect(pu1.p.y, Global.playery);
    }

    public void testFastShotPlayer(Tester t)
    {
        FastShotPlayer fplay1 = new FastShotPlayer(200, 3, 1);
        ArrayList<AProj> projArray = new ArrayList<AProj>();
        projArray.add(new StandardProj(new Posn(200, 200)));

        t.checkExpect(fplay1.canShoot(projArray), true);
        t.checkExpect(fplay1.count(), 0);
        t.checkExpect(fplay1.shoot(), fplay1);
        t.checkExpect(fplay1.count(), 1);
    }

    public void testFastShotPowerup(Tester t)
    {
        FastShotPowerup fsp1 = new FastShotPowerup();
        StandardPlayer p1 = new StandardPlayer(200, 3, 1);

        t.checkExpect(fsp1.apply(p1), new FastShotPlayer(200, 3, 1));
        t.checkExpect(fsp1.p.x >= 0, true);
        t.checkExpect(fsp1.p.x <= Global.width, true);
    }

    public void testGlobal(Tester t)
    {
        t.checkExpect(Global.drawLifeSpriteHere(new Posn(200, 200)),
                new FromFileImage(new Posn(200, 200), "Life.png"));
    }

    public void testLevelManager(Tester t)
    {
        t.checkExpect(LevelManager.makeAliens(3, 2).size(), 3);
        t.checkExpect(LevelManager.makeAliens(1, 2).get(0).size(), 2);
    }

    public void testIFunctions(Tester t)
    {
        ProjDamageAlien func1 = new ProjDamageAlien();
        ProjDamageBunker func2 = new ProjDamageBunker();
        ProjDamagePlayer func3 = new ProjDamagePlayer();
        StandardProj proj1 = new StandardProj(new Posn(1, 1));
        EnemyProj proj2 = new EnemyProj(new Posn(2, 2));

        t.checkExpect(func1.apply(proj1), true);
        t.checkExpect(func2.apply(proj1), true);
        t.checkExpect(func3.apply(proj1), false);
        t.checkExpect(func1.apply(proj2), false);
        t.checkExpect(func2.apply(proj2), true);
        t.checkExpect(func3.apply(proj2), true);
    }

    public void testSIEnd(Tester t)
    {
        World end1 = new SIEnd();

        t.checkExpect(end1.onKeyEvent("q"), new SIFinal());
        t.checkExpect(end1.onKeyEvent("left"), end1);
    }

    public void testSIGameWorld(Tester t)
    {
        StandardPlayer player = new StandardPlayer(200, 1, 1);
        ArrayList<AProj> projs = new ArrayList<AProj>();
        projs.add(new StandardProj(new Posn(200, 200)));
        ArrayList<ArrayList<AAlien>> aliens = new ArrayList<ArrayList<AAlien>>();
        aliens.add(new ArrayList<AAlien>());
        aliens.get(0).add((new StandardAlien(new Posn(1, 1), 1, 1)));
        ArrayList<AUfo> ufos = new ArrayList<AUfo>();
        ufos.add(new StandardUfo(new Posn(100, 10), new Vel(10, 0)));
        ArrayList<APowerup> powerups = new ArrayList<APowerup>();
        ArrayList<ABunker> bunkers = LevelManager.makeBunkers();

        SIGameWorld world1 = new SIGameWorld(player, aliens, ufos, projs,
                bunkers, powerups, 0, 1);

        // Test draw functions
        t.checkExpect(world1.drawAliensOnto(world1.background()),
                new OverlayImages(world1.background(), new FromFileImage(
                        new Posn(1, 1), "Alien1.png")));
        t.checkExpect(world1.drawProjsOnto(world1.background()),
                new OverlayImages(world1.background(), new RectangleImage(
                        new Posn(200, 200), 5, 5, new Green())));
        t.checkExpect(world1.drawUfosOnto(world1.background()),
                new OverlayImages(world1.background(), new FromFileImage(
                        new Posn(100, 10), "Saucer.png")));

        // Test tick functions
        t.checkExpect(world1.projs.get(0).p, new Posn(200, 200));
        world1.moveProjs();
        t.checkExpect(world1.projs.get(0).p, new Posn(200, 186));
        world1.aliens.get(0).add(new StandardAlien(new Posn(200, 186), 1, 1));
        world1.enemyProjInteraction();
        t.checkExpect(world1.aliens.get(0).size(), 1);
        t.checkExpect(world1.projs.size(), 0);
        world1.aliens.get(0).remove(0);
        world1.cleanUpEmptyColumns();
        t.checkExpect(world1.aliens.size(), 0);
        t.checkExpect(world1.score, 1);

        world1.aliens.add(new ArrayList<AAlien>());
        world1.aliens.get(0).add((new StandardAlien(new Posn(50, 50), 1, 1)));
        world1.aliensMove();
        t.checkExpect(world1.aliens.get(0).get(0).p, new Posn(50, 50));

        world1.ufosMove();
        t.checkExpect(world1.ufos.get(0).p, new Posn(110, 10));
        world1.projs.add(new StandardProj(new Posn(110, 10)));
        world1.ufoProjInteraction();
        t.checkExpect(world1.ufos.size(), 0);
        t.checkExpect(world1.score, 201);

        // On Key Events
        t.checkExpect(world1.onKeyEvent("z"), world1);
        SIGameWorld world2 = world1;
        world2.player.p.x -= Global.playerMovementSpeed;
        t.checkExpect(world1.onKeyEvent("left"), world2);
        world2.player.p.x += 2 * Global.playerMovementSpeed;
        t.checkExpect(world1.onKeyEvent("right"), world2);
        world2 = world1;
        world2.projs.add(new StandardProj(world1.player.p));
        t.checkExpect(world1.onKeyEvent("x"), world2);

        // World End tests
        t.checkExpect(world1.playerHasNoLives(), false);
        t.checkExpect(world1.aliensReachedLine(), false);
        world1.player.lives = 0;
        t.checkExpect(world1.playerHasNoLives(), true);
        world1.aliens.get(0).add(
                new StandardAlien(new Posn(200, Global.alienDeathLiney), 1, 1));
        t.checkExpect(world1.aliensReachedLine(), true);
    }

    public void testStandardAlien(Tester t)
    {
        StandardAlien alien1 = new StandardAlien(new Posn(200, 200), 1, 1);
        alien1.timer = 0;

        t.checkExpect(alien1.render(), new FromFileImage(new Posn(200, 200),
                "Alien1.png"));
        t.checkExpect(alien1.createProj(), new EnemyProj(new Posn(200, 200)));
        alien1.move();
        t.checkExpect(alien1.p, new Posn(240, 200));
    }

    public void testStandardBunker(Tester t)
    {
        StandardBunker bunker1 = new StandardBunker(new Posn(100, 100));

        t.checkExpect(bunker1.render(), new FromFileImage(new Posn(100, 100),
                "Bunker1.png"));
        bunker1.health -= 2;
        t.checkExpect(bunker1.render(), new FromFileImage(new Posn(100, 100),
                "Bunker2.png"));
        bunker1.health -= 2;
        t.checkExpect(bunker1.render(), new FromFileImage(new Posn(100, 100),
                "Bunker3.png"));
    }

    public void testStandardPlayer(Tester t)
    {
        StandardPlayer play1 = new StandardPlayer(200, 3, 1);
        ArrayList<AProj> projList = new ArrayList<AProj>();

        t.checkExpect(play1.createProj(), new StandardProj(new Posn(200,
                Global.playery)));
        t.checkExpect(play1.canShoot(projList), true);
        projList.add(new StandardProj(new Posn(1, 1)));
        t.checkExpect(play1.canShoot(projList), false);
    }

    World FirstWorld = LevelManager.produceLevel(1);
    boolean runGame = FirstWorld.bigBang(Global.width, Global.height, 0.025);

}
