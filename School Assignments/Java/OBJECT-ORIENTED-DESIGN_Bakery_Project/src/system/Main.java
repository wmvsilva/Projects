package system;

import java.util.Scanner;

/**
 * The main class for the bakery system. This has a main method which interacts
 * with the user via the command line and performs operations with the bakery
 * database this holds.
 * 
 * @author William Silva, silva.w@husky.neu.edu
 * @version 14 June 2014
 * 
 */
public class Main {

    /**
     * The bakery database containing all customer, order, and bakery item
     * information. This class will communicate with this module in order to
     * update and show information to the user running the main method.
     */
    static Bakery bakery;

    /** the scanner to read input from the command line with */
    static Scanner scan = new Scanner(System.in);

    /**
     * Main method which runs the bakery system.
     * 
     * @param args
     *            command line arguments which do not effect the program
     */
    public static void main(String[] args) {

        // Initializes the bakery with no data.
        bakery = new Bakery();

        // Initializes the bakery by adding data if the user wishes to.
        InitializeMenu.initializeMenu();

        // Produces the main menu of the system.
        mainMenu();

        // Closes the scanner
        scan.close();

        // Exits the program
        System.exit(0);
    }

    /*
     * **********************************************************************
     * Main Menu
     * **********************************************************************
     */

    /**
     * Method displays the main menu for the bakery system and asks for a user
     * prompt. The main menu will then go to whatever submenu the user
     * specifies.
     */
    static void mainMenu() {
        resetScanner();
        Util.println("Main Menu- Please select a category.");
        Util.println("1. Customers");
        Util.println("2. Orders");
        Util.println("3. Bakery Items");
        Util.println("4. Save System");
        Util.println("5. Exit");
        if (!(scan.hasNextInt())) {
            Util.println("Invalid input! Please enter in either 1, 2, 3,"
                    + " or 4.");
            mainMenu();
        }

        int input = scan.nextInt();

        switch (input)
        {
            case 1:
                CustomerMenu.customerMenu();
                break;

            case 2:
                OrderMenu.orderMenu();
                break;

            case 3:
                BakeryItemMenu.bakeryItemMenu();
                break;

            case 4:
                SaveMenu.saveSystemMenu();
                break;

            case 5:
                System.exit(0);
                break;

            default:
                Util.println("Invalid input! Please enter in either "
                        + "1, 2, 3, or 4.");
                mainMenu();
                break;
        }
    }

    /*
     * **********************************************************************
     * Scanner Utilities
     * **********************************************************************
     */

    /**
     * Method resets the scanner by removing any extra input the user may have
     * put in when entering data.
     */
    static void resetScanner() {
        scan = new Scanner(System.in);
    }

    /**
     * Method determines if the system input has no next input
     * 
     * @return boolean stating if the scanner for console input has no next
     *         input.
     */
    static boolean scanNoNext() {
        return !(scan.hasNext());
    }

    /**
     * Method determines if the system input has no next integer input
     * 
     * @return boolean stating if the scanner for console input has no next
     *         integer input.
     */
    static boolean scanNoNextInt() {
        return !(scan.hasNextInt());
    }

    /**
     * Method determines if the system input has no next double input
     * 
     * @return boolean stating if the scanner for console input has no next
     *         double input.
     */
    static boolean scanNoNextDouble() {
        return !(scan.hasNextDouble());
    }
}
