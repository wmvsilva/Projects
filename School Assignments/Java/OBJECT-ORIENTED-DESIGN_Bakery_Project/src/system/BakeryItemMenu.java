package system;

/**
 * Class represents a user interface for choosing actions to perform on the
 * available bakery items of the main bakery system.
 * 
 * @author William Silva, silva.w@husky.neu.edu
 * @version 18 June 2014
 * 
 */
class BakeryItemMenu {

    /**
     * Method displays a menu for performing operations on the bakery items in
     * the database. The user may select an option or go back to the main menu.
     */
    static void bakeryItemMenu() {
        Main.resetScanner();
        Util.println("Bakery Item Menu-");
        Util.println("1. Add bakery item");
        Util.println("2. View/update bakery items");
        Util.println("3. Main Menu");
        if (Main.scanNoNextInt()) {
            Util.printerr("enter a valid integer option.");
            bakeryItemMenu();
        }
        int input = Main.scan.nextInt();

        switch (input)
        {
            case 1:
                Main.resetScanner();
                Util.println("Adding bakery item-");
                Util.println("Please enter the bakery item name:");
                if (Main.scanNoNext()) {
                    Util.printerr("enter a valid name!");
                    bakeryItemMenu();
                }
                String bakeryItemName = Main.scan.nextLine();

                Main.resetScanner();
                Util.println("Please enter the bakery item category:");
                if (Main.scanNoNext()) {
                    Util.printerr("enter a valid category!");
                    bakeryItemMenu();
                }
                String bakeryItemCategory = Main.scan.nextLine();

                Main.resetScanner();
                Util.println("Please enter the bakery item price:");
                if (Main.scanNoNextDouble()) {
                    Util.printerr("enter a valid price!");
                    bakeryItemMenu();
                }
                double bakeryItemPrice = Main.scan.nextDouble();

                Main.bakery.addBakeryItem(bakeryItemName, bakeryItemCategory,
                        bakeryItemPrice);
                // TODO Display ID after
                Util.println("Bakery item added!");
                bakeryItemMenu();
                break;

            case 2:
                if (Main.bakery.noBakeryItems()) {
                    Util.println("No bakery items to view!");
                    bakeryItemMenu();
                }
                BakeryItem item = pickABakeryItem();
                viewUpdateSingleBakeryItem(item);
                break;

            case 3:
                Main.mainMenu();
                break;

            default:
                Util.printerr("enter an option 1-2");
                bakeryItemMenu();
        }
    }

    /**
     * Method displays a user interface for interacting with a single bakery
     * item that allows the user to view/update the given bakery item.
     * 
     * @param item
     *            the given item to perform user operations on
     */
    static void viewUpdateSingleBakeryItem(BakeryItem item) {
        Main.resetScanner();
        Util.println("Viewing bakery item-");
        item.displayItem();
        Util.println("What would you like to do?");
        Util.println("1. Update the bakery item?");
        Util.println("2. Bakery Item Menu");
        if (Main.scanNoNextInt()) {
            Util.printerr("enter an integer 1-2.");
            viewUpdateSingleBakeryItem(item);
        }

        int input = Main.scan.nextInt();
        switch (input)
        {
            case 1:
                Util.println("Updating bakery item-");
                updateBakeryItem(item);
                break;

            case 2:
                bakeryItemMenu();
                break;

            default:
                Util.printerr("enter an integer 1-2.");
                viewUpdateSingleBakeryItem(item);
                break;
        }
    }

    /**
     * Method provides a user interface for updating fields of the given bakery
     * item with user input.
     * 
     * @param item
     *            the bakery item to update the fields of
     */
    static void updateBakeryItem(BakeryItem item) {
        Main.resetScanner();
        Util.println("Please select what component of the this bakery item to "
                + "update");
        Util.println("1. ID");
        Util.println("2. Name");
        Util.println("3. Category");
        Util.println("4. Price");
        Util.println("5. None. Go back to bakery item menu");
        if (Main.scanNoNextInt()) {
            Util.printerr("enter a valid option 1-4.");
            updateBakeryItem(item);
        }

        int input = Main.scan.nextInt();
        switch (input)
        {
            case 1:
                Main.resetScanner();
                Util.println("Updating ID-");
                Util.println("Please enter an updated integer ID:");
                // TODO No duplicates
                if (Main.scanNoNextInt()) {
                    Util.printerr("enter integers for IDs");
                    updateBakeryItem(item);
                }
                int newID = Main.scan.nextInt();
                item.updateID(newID);
                Util.println("Item ID updated.");
                break;

            case 2:
                Main.resetScanner();
                Util.println("Updating item name-");
                Util.println("Please enter an updated name:");
                if (Main.scanNoNext()) {
                    Util.printerr("enter a string for the name");
                    updateBakeryItem(item);
                }
                String newName = Main.scan.nextLine();
                item.updateName(newName);
                Util.println("Item name updated.");
                break;

            case 3:
                Main.resetScanner();
                Util.println("Updating item category:");
                Util.println("Please enter an updated category:");
                if (Main.scanNoNext()) {
                    Util.printerr("enter a string for the category");
                    updateBakeryItem(item);
                }
                String newCategory = Main.scan.nextLine();
                item.updateCategory(newCategory);
                Util.println("Item category updated.");
                break;

            case 4:
                Main.resetScanner();
                Util.println("Updating Price-");
                Util.println("Please enter an updated price:");
                if (Main.scanNoNextDouble()) {
                    Util.printerr("enter a dollar amount");
                    updateBakeryItem(item);
                }
                double newPrice = Main.scan.nextDouble();
                item.updatePrice(newPrice);
                Util.println("Item Price updated.");
                break;

            case 5:
                bakeryItemMenu();
                break;

            default:
                Util.printerr("enter an integer option 1-5");
                break;

        }
        updateBakeryItem(item);
    }

    /**
     * Method has the user go through prompts to select a bakery item from the
     * list of available bakery items.
     * 
     * @return the bakery item that the user has chosen from the available
     *         bakery items
     */
    static BakeryItem pickABakeryItem() {
        Main.resetScanner();
        Util.println("Select a bakery item from the list-");
        Main.bakery.displayAllBakeryItems();
        Util.println("Please enter the ID of what you'd like to select:");
        if (!(Main.scan.hasNextInt())) {
            Util.println("Invalid input! Please enter in an ID.");
            return pickABakeryItem();
        }

        int inputItemID = Main.scan.nextInt();

        if (!(Main.bakery.containsBakeryItem(inputItemID))) {
            Util.println("Bakery item ID not found. Please try again.");
            return pickABakeryItem();
        }

        return Main.bakery.getBakeryItem(inputItemID);
    }
}
