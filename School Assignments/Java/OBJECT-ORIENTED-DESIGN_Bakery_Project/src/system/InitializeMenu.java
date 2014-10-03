package system;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Method represents a user interface for initializing the bakery database. The
 * user may chose to initialize as an empty database with no files or load from
 * files of a specific format.
 * 
 * @author William Silva, silva.w@husky.neu.edu
 * @version 17 June 2014
 */
class InitializeMenu {

    /**
     * Method has the user go through prompts to initialize the bakery data with
     * either no data or data from files.
     */
    static void initializeMenu() {
        Main.resetScanner();
        // Display the options for the user.
        Util.println("Welcome to the bakery system! Please select an option.");
        Util.println("1. New Bakery");
        Util.println("2. Load from files");
        // Detect if they have entered in an integer.
        if (Main.scanNoNextInt()) {
            Util.println("Invalid input! Please enter in either 1 or 2.");
            initializeMenu();
        }

        int input = Main.scan.nextInt();
        // Detect if they have entered in a proper option.
        if ((input != 1) && (input != 2)) {
            Util.println("Invalid input! Please enter in either 1 or 2.");
            initializeMenu();
        }
        // Initialize with a file.
        if (input == 2) {
            initializeWithFileMenu();
        }
        // If the input is 1, no data has to be added.
    }

    /**
     * Method initializes the bakery system using prompts from the user to
     * obtain files where the system data is stored.
     */
    static void initializeWithFileMenu() {
        Main.resetScanner();
        initializeBakeryItemsMenu();
        initializeBakeryOrdersMenu();
    }

    /**
     * Method initializes the bakery system with bakery item data based on what
     * the user enters as a file.
     */
    static void initializeBakeryItemsMenu() {
        Main.resetScanner();
        Util.println("Please enter in the file you would like to "
                + "initialize the bakery items from:");

        String bakeryItemsFileName = Main.scan.next();
        File bakeryItemsFile = new File(bakeryItemsFileName);

        try {
            Main.bakery.initializeBakeryItems(bakeryItemsFile);
        }
        catch (FileNotFoundException e) {
            Util.println(e.getMessage());
            Util.println("File not found. Please reenter the file" + " name: ");
            Main.bakery = new Bakery();
            initializeBakeryItemsMenu();
        }
        catch (IllegalArgumentException e) {
            Util.println(e.getMessage());
            Util.println("File format incorrect. Please reenter the file"
                    + " name: ");
            Main.bakery = new Bakery();
            initializeBakeryItemsMenu();
        }
    }

    /**
     * Method asks for a file name from the user and initializes the bakery
     * orders for the bakery database from that file.
     */
    static void initializeBakeryOrdersMenu() {
        Main.resetScanner();
        Util.println("Please enter the file name of the file you would like to "
                + "initialize the bakery orders from:");

        String bakeryOrdersFileName = Main.scan.next();
        File bakeryOrdersFile = new File(bakeryOrdersFileName);

        try {
            Main.bakery.initializeBakeryOrders(bakeryOrdersFile);
        }
        catch (FileNotFoundException e) {
            Util.println(e.getMessage());
            Util.println("Could not find file. Please try again.");
            Main.bakery = new Bakery();
            initializeWithFileMenu();
        }
        catch (IllegalArgumentException e) {
            Util.println(e.getMessage());
            Util.println("File of incorrect format. Please try again.");
            Main.bakery = new Bakery();
            initializeWithFileMenu();
        }
    }
}
