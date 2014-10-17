#include <vector>
#include <iostream>
#include <SFML/Graphics.hpp>
#include <SFML/Audio.hpp>

/*
 * William Silva
 * main.cpp
 *
 * DVD Project
 * This is a very simple example of usage of the SFML
 * (Safe Fast Media Library). It consists of the DVD
 * logo bouncing about the screen. In addition, there
 * is minor user interaction as explained below.
 *
 * Controls:
 * Left-mouse click - change DVD logo movement direction to
 *                    where the user clicked
 *
 */

/// Value of pi
const double PI = 3.14159;

/// Manager for the target underneath the mouse cursor
/// on the screen
class TargetSprite
{
    ///the SFML circle object representing the target
    sf::CircleShape tar;

public:

    /// Constructor for TargetSprite
    /// Builds a small red target at (0,0).
    TargetSprite()
    {
        tar.setRadius(5);
        tar.setFillColor(sf::Color::Red);
        tar.setPosition(0,0);
        tar.setOrigin(5 / 2, 5 / 2);
    }

    /// @return the tar field of this
    sf::CircleShape& getSprite()
    {
        return tar;
    }

    /// sets the position of the sprite field to the given x and y
    void mousePosition(int mouseX, int mouseY)
    {
        tar.setPosition(mouseX, mouseY);
    }

    /// @return the x position of the sprite field
    int getX()
    {
        return tar.getPosition().x;
    }

    /// @return the y position of the sprite field
    int getY()
    {
        return tar.getPosition().y;
    }

};

/// Manager for the DVD logo sprite that moves across the screen
class DVDSprite
{
    /// the DVD logo sprite
    sf::Sprite sprite;
    /// the angle at which the DVD logo is going (in radians)
    float angle = 3*PI/4;
    /// the speed at which the DVD logo is moving (in pixels per tick)
    float speed = 3;

public:

    /**
     * Constructor for DVDSprite
     * @param texture the picture used on the sprite
     * @param startX the starting X position of the sprite
     * @param startY the starting Y position of the sprite
     */
    DVDSprite(sf::Texture &texture, int startX, int startY)
    {
        sprite.setTexture(texture);
        sprite.setColor(sf::Color::White);
        sprite.setPosition(startX, startY);
        int sprite_x = sprite.getGlobalBounds().width;
        int sprite_y = sprite.getGlobalBounds().height;
        //Sprite origin is set to its center
        sprite.setOrigin(sprite_x / 2, sprite_y / 2);
    }

    /// @return the dvd sprite field of this
    sf::Sprite& getSprite()
    {
        return sprite;
    }

    /// @return reference to angle field of this
    float& getAngle()
    {
        return angle;
    }

    /// moves the DVD sprite according to the current angle and speed
    void move()
    {
        sprite.move(std::cos(angle) * speed,
                    -std::sin(angle) * speed);
    }

    /**
     * bounces the DVD logo off the sides of the walls by updating the
     * angle. If there is a bounce, the color of the sprite is changed
     * @param windowX the width of the window
     * @param windowY the height of the window
     */
    void bounce(double windowX, double windowY)
    {
        double sprite_x = sprite.getLocalBounds().width;
        double sprite_y = sprite.getLocalBounds().height;
        if (sprite.getPosition().x - (sprite_x / 2) < 0)
            {
                angle = PI - angle;
                sprite.move(0.1, 0);
                sprite.setColor(sf::Color::Green);
            }
            if (sprite.getPosition().x + (sprite_x / 2) >
                windowX)
            {
                angle = PI - angle;
                sprite.move(-0.1, 0);
                sprite.setColor(sf::Color::Red);
            }
            if (sprite.getPosition().y - (sprite_y / 2) < 0)
            {
                angle = -angle;
                sprite.move(0, 0.1);
                sprite.setColor(sf::Color::Blue);
            }
            if (sprite.getPosition().y + (sprite_y / 2) >
                windowY)
            {
                angle = -angle;
                sprite.move(0, -0.1);
                sprite.setColor(sf::Color::Magenta);
            }
    }

    /**
     * based on the given mouse click position, updates the angle of
     * the sprite to go in the direction of the mouse click
     * @param mouseX the mouse's x-coordinate
     * @param mouseY the mouse's y-coordinate
     */
    void mouseInput(int mouseX, int mouseY)
    {
        int spriteX = sprite.getPosition().x;
        int spriteY = sprite.getPosition().y;

        int diffX = mouseX - spriteX;
        int diffY = mouseY - spriteY;
        if (diffX == 0)
        {
            if (diffY > 0)
                angle = 3*PI/2;
            else if (diffY < 0)
                angle = PI/2;
        }
        else
        {
            double newAngle = std::atan(diffY/diffX);
            if (diffX < 0 && diffY < 0)
                newAngle = newAngle + PI;
            else if (diffX < 0)
                newAngle = newAngle + PI;
            else if (diffY < 0)
                newAngle = newAngle + 2*PI;

            angle = -newAngle;
        }
    }
};

//Main function
int main()
{
    //Create the window
    sf::RenderWindow window(sf::VideoMode(800, 600, 32),
                             "DVD");
    window.setVerticalSyncEnabled(true);

    //Initialize the clock
    sf::Clock clock;
    float lastTime = clock.getElapsedTime().asSeconds();

    //Initialize variables to manage ticks and frames

    ///Ticks per second
    int ticksPerSecond = 60;
    /// Number of times to update
    float delta = 0;
    /// How many times the game has updated in this second
    int updates = 0;
    /// How many times frames have been drawn in this second
    int frames = 0;

    //DVD texture
    sf::Texture dvdLogo;
    dvdLogo.loadFromFile("LOGO-DVD-VIDEO-psd70005.png");
    dvdLogo.setSmooth(true);

    //DVD sprite to display
    DVDSprite spriteManager(dvdLogo, window.getSize().x / 2, window.getSize().y / 2);
    sf::Sprite &dvdSprite = spriteManager.getSprite();

    //Target to display
    TargetSprite targetManager;
    sf::CircleShape &tarSprite = targetManager.getSprite();

    //Main loop
    while (window.isOpen())
    {
        //Determine if there is a need to update based on the time passed
        // since the last loop
        float now = clock.getElapsedTime().asSeconds();
        delta += (now - lastTime) * ticksPerSecond;
        lastTime = now;

        //Manage window events
        sf::Event event;
        while (window.pollEvent(event))
        {
            //Close the window if the user has exited
            if (event.type == sf::Event::Closed)
            {
                window.close();
            }
        }

        // Manage user input
        // Manage mouse left clicks
        if (sf::Mouse::isButtonPressed(sf::Mouse::Left))
        {
            spriteManager.mouseInput(sf::Mouse::getPosition(window).x,
                                     sf::Mouse::getPosition(window).y);
        }
        //Manage mouse on screen
        if (sf::Mouse::getPosition(window).x > 0 &&
            sf::Mouse::getPosition(window).x < window.getSize().x &&
            sf::Mouse::getPosition(window).y > 0 &&
            sf::Mouse::getPosition(window).y < window.getSize().y)
        {
            targetManager.mousePosition(sf::Mouse::getPosition(window).x,
                                        sf::Mouse::getPosition(window).y);
        }


        //TICK STAGE
        //Update the game and tick as many times as necessary
        while (delta >= 1)
        {
            updates++;

            //Move logo
            spriteManager.move();
            //Bounce logo
            spriteManager.bounce(window.getSize().x, window.getSize().y);

            //Move to center if space bar
            if (sf::Keyboard::isKeyPressed(sf::Keyboard::Space))
                {
                    dvdSprite.setPosition(400,400);
                }
            delta--;
        }

        //RENDER STAGE
        //Render the window
        window.clear(sf::Color::Black);
        //Render the DVD logo
        window.draw(dvdSprite);
        //Render the target
        window.draw(tarSprite);
        //Display everything
        window.display();
        frames++;

        //Restart clock if above a second
        if (clock.getElapsedTime().asSeconds() >= 1)
        {
            clock.restart();

            //For debugging purposes
            #ifndef NDEBUG
            std::cout << updates << " Ticks, Fps " << frames << std::endl;
            std::cout << delta << std::endl;
            #endif // NDEBUG

            lastTime = clock.getElapsedTime().asSeconds();
            updates = 0;
            frames = 0;
        }
    }
}
