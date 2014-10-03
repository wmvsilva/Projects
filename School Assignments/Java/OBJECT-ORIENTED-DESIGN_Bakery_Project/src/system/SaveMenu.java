package system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * Class represents a menu system for saving the current state of the bakery as
 * text files containing information about bakery items, orders, and customers.
 * 
 * @author William Silva, silva.w@husky.neu.edu
 * @version 17 June 2014
 * 
 */
class SaveMenu {

    /**
     * Method represents a submenu for saving which allows the user to pick how
     * they wish to save the current state of the bakery before returning to the
     * main menu.
     */
    static void saveSystemMenu() {
        Main.resetScanner();
        Util.println("How would you like to save the system?");
        Util.println("1. Save the available bakery items.");
        Util.println("2. Save the list of orders and customers");
        Util.println("3. Do not save. Return to Main Menu.");
        if (Main.scanNoNextInt()) {
            Util.printerr("enter an integer 1-2");
            saveSystemMenu();
        }
        int input = Main.scan.nextInt();
        Main.resetScanner();

        if (input == 1 | input == 2) {
            Util.println("Please enter name of the file to save to:");
            Util.println("Note that it will be your_input.txt");
            if (Main.scanNoNext()) {
                Util.printerr("Please enter a file name");
                saveSystemMenu();
            }
            String fileName = Main.scan.next() + ".txt";
            File saveFile = new File(fileName);
            if (input == 1) {
                saveBakeryItems(saveFile);
            }
            else { // input was 2
                saveOrderList(saveFile);
            }
        }
        else if (input == 3) {
            Main.mainMenu();
        }
        else {
            Util.printerr("enter an integer 1-2");
        }

        saveSystemMenu();
    }

    /**
     * Method saves the state of the bakery item data in a file.
     * 
     * @param f
     *            the file to save the bakery item data to.
     */
    static void saveBakeryItems(File f) {
        PrintStream output;
        try {
            output = new PrintStream(f);
        }
        catch (FileNotFoundException e) {
            Util.println("File not found exception! Please try again.");
            saveSystemMenu();
            // To ensure method ends early, return is added.
            return;
        }
        String tab = "\t";
        // Make sure this stays with tabs
        output.println("BakeryItemID" + tab + "BakeryItemName" 
                + tab + "Category" + tab + "Price");
        ArrayList<BakeryItem> iterateItems = Main.bakery.iterateBakeryItems();
        for (BakeryItem i : iterateItems) {
            output.println(i.getID() + tab + i.getName() + tab
                    + i.getCategory() + tab + i.getPrice());
        }
        Util.println("Bakery items file successfully saved as " + f.getName());
        output.close();
        saveSystemMenu();
    }

    /**
     * Method saves the given orders in this database to the given text file
     * 
     * @param f
     *            the file to save the given orders to
     */
    static void saveOrderList(File f) {
        PrintStream output;
        try {
            output = new PrintStream(f);
        }
        catch (FileNotFoundException e) {
            Util.println("File not found exception! Please try again.");
            saveSystemMenu();
            // To ensure method ends early, return is added.
            return;
        }
        String tab = "\t";
        output.println(
                "CustomerID" + tab
                + "LastName" + tab
                + "Address" + tab
                + "City" + tab
                + "State" + tab
                + "ZipCode" + tab
                + "OrderID" + tab
                + "Paid?" + tab
                + "OrderDate" + tab
                + "PickupDate" + tab
                + "BakeryItemID" + tab
                + "BakeryItemName" + tab
                + "BakeryItemCategory" + tab
                + "Quantity" + tab
                + "Price" + tab
                + "Total" + tab
                + "DiscountUsedOnOrder" + tab
                + "TotalDue" + tab
                + "AvailableDiscount" + tab
                + "CurrentLoyalty");
        ArrayList<Customer> iterateCust = Main.bakery.iterateCustomers();
        for (Customer c : iterateCust) {
            if (c.noOrders()) {
                output.println(saveCustomerHelper(c));
            }
            else {
                ArrayList<Order> iterateOrd = c.iterateOrders();
                for (Order o : iterateOrd) {
                    for (ItemQuantityPair pair : o.getItemList()) {
                        BakeryItem b = pair.getItem();

                        output.println(saveCustomerHelper(c) + tab + o.getID()
                                + tab + o.getHasPaidString() + tab
                                + o.getOrderDateString() + tab
                                + o.getPickUpDateString() + tab + b.getID()
                                + tab + b.getName() + tab + b.getCategory()
                                + tab + pair.getQuantity() + tab + b.getPrice()
                                + tab + o.getPriceBeforeDiscount() + tab
                                + o.getDiscountUsed() + tab + o.calcTotalDue()
                                + tab + c.getAvailableDiscount() + tab
                                + c.getCurrentLoyalty());
                    }
                }
            }
        }
        output.close();
    }

    /**
     * Method produces the string needed to save a customer to a text file
     * 
     * @param c
     *            the customer to produce the text representation of
     * @return a string representing how the customer looks in an Orders text
     *         file
     */
    static String saveCustomerHelper(Customer c) {
        String tab = "\t";
        return c.getID() + tab + c.getLastName() + tab + c.getAddress() + tab
                + c.getCity() + tab + c.getState() + tab + c.getZipCode();
    }
}
