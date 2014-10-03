package system;

import java.util.ArrayList;

/**
 * Class represents a menu interface which the user may navigate to perform
 * actions on the orders of the bakery including the addition of new orders and
 * the updating of old orders.
 * 
 * @author William Silva, silva.w@husky.neu.edu
 * @version 17 June 2014
 * 
 */
class OrderMenu {

    /**
     * Method displays the submenu for performing actions with bakery orders.
     * The user may pick one of these actions and interact with the orders.
     */
    static void orderMenu() {
        Main.resetScanner();
        Util.println("Order Menu- Please select an option:");
        Util.println("1. Add new order.");
        Util.println("2. View/update orders.");
        Util.println("3. Main Menu.");

        if (!(Main.scan.hasNextInt())) {
            Util.println("Invalid input! Please enter in 1, 2, or 3");
            orderMenu();
        }

        int input = Main.scan.nextInt();

        switch (input)
        {
            case 1:
                addNewOrderOption();
                break;

            case 2:
                viewUpdateOrderOption();
                break;

            case 3:
                Main.mainMenu();
                break;

            default:
                Util.println("Invalid input! Please enter in 1, 2, or 3.");
                orderMenu();
        }
    }

    /**
     * Method displays the submenu for adding a new order to the bakery
     * database. The user describes the order they want and it is added to the
     * database.
     */
    static void addNewOrderOption() {

        if (Main.bakery.noBakeryItems()) {
            Util.println("There are no bakery items! Cannot place an order!");
            orderMenu();
        }

        // Selecting the bakery item to order
        Util.println("Please select what you'd like to order: ");
        ArrayList<ItemQuantityPair> itemsToOrder = pickMultipleBakeryItems();

        // Defining the order date
        CalendarDate orderDate = null;
        try {
            orderDate = makeDate("order");
        }
        catch (Exception e) {
            Util.println(e.getMessage());
            Util.println("Please enter in a valid date!");
            addNewOrderOption();
        }

        // Defining the pick up date
        CalendarDate pickUpDate = null;
        try {
            pickUpDate = makeDate("pick up");
        }
        catch (Exception e) {
            Util.println(e.getMessage());
            Util.println("Please enter in a valid date!");
            addNewOrderOption();
        }

        // Has the customer paid yet?
        Main.resetScanner();
        Util.println("Has the customer paid yet? (y/n)");
        if (!(Main.scan.hasNext())) {
            Util.println("Invalid input! Please enter in either y or n");
            addNewOrderOption();
        }

        String inputPaid = Main.scan.next();
        boolean hasPaid = false;
        if (inputPaid.equalsIgnoreCase("y")) {
            hasPaid = true;
        }
        else if (inputPaid.equalsIgnoreCase("n")) {
            hasPaid = false;
        }
        else {
            Util.println("Invalid input! Please enter in either y or n");
            addNewOrderOption();
        }

        // Choosing the customer
        Main.resetScanner();
        Util.println("Who is this order for?");
        Util.println("1. New customer");
        Util.println("2. Existing customer.");

        if (!(Main.scan.hasNextInt())) {
            Util.println("Invalid input! Please enter in 1 or 2.");
            addNewOrderOption();
        }

        int input = Main.scan.nextInt();

        switch (input)
        {
            case 1:
                int newCustomerID = CustomerMenu.addCustomerOptionHelper();
                int orderID = Main.bakery.addOrder(newCustomerID, hasPaid,
                        orderDate, pickUpDate, itemsToOrder, 0.0);
                Util.println("Order added with ID" + orderID);
                Main.bakery.displayOrder(orderID);
                orderMenu();
                break;

            case 2:
                Customer chosenCustomer = CustomerMenu.pickACustomer();
                double discount = chosenCustomer.getAvailableDiscount();
                Util.println("Customer has an available discount of $"
                        + discount);
                double chosenDiscount = pickDiscountFor(chosenCustomer);
                if (chosenDiscount > ItemQuantityPair.calculatePrice(
                        itemsToOrder)) {
                    Util.println("You can't have a discount greater "
                            + "than the price!");
                    Util.println("Please try again!");
                    orderMenu();
                }
                int orderIDCase2 = Main.bakery.addOrder(chosenCustomer.getID(),
                        hasPaid, orderDate, pickUpDate, itemsToOrder,
                        chosenDiscount);
                Util.println("Order added with ID" + orderIDCase2);
                Main.bakery.displayOrder(orderIDCase2);
                orderMenu();
                break;

            default:
                Util.println("Invalid input! Please try again.");
                addNewOrderOption();
                break;
        }
    }

    /**
     * Method provides a console interface to the user and allows them to pick a
     * series of items and associated quantities to produce an arraylist
     * containing the given information.
     * 
     * @return an arraylist containing data pairs of what the user decided to
     *         pick for bakery items and quantities of each bakery item.
     */
    static ArrayList<ItemQuantityPair> pickMultipleBakeryItems() {
        ArrayList<ItemQuantityPair> result = new ArrayList<ItemQuantityPair>();
        BakeryItem chosenItem = BakeryItemMenu.pickABakeryItem();
        Main.resetScanner();
        Util.println("How many units of " + chosenItem.getName()
                + " would you like to order?");
        Util.println("Please enter a quantity:");
        if (Main.scanNoNextInt()) {
            Util.printerr("enter a valid quantity!");
            pickMultipleBakeryItems();
        }
        int quantity = Main.scan.nextInt();
        if (quantity <= 0) {
            Util.printerr("enter a quantity above zero!");
            pickMultipleBakeryItems();
        }
        result.add(new ItemQuantityPair(chosenItem, quantity));

        Main.resetScanner();
        Util.println("Would you like to add more bakery items to your order?");
        Util.println("1. Add more items");
        Util.println("2. Order complete as is");
        if (Main.scanNoNextInt()) {
            Util.printerr("enter a valid integer option");
            pickMultipleBakeryItems();
        }
        int input = Main.scan.nextInt();

        switch (input)
        {
            case 1:
                Util.println("Adding more bakery items to order-");
                result.addAll(pickMultipleBakeryItems());
                return result;

            case 2:
                return result;

            default:
                Util.printerr("enter a valid integer input 1-2");
                return pickMultipleBakeryItems();
        }
    }

    /**
     * Method has the user chose a date and returns a CalendarDate depending on
     * what the user desires.
     * 
     * @param type
     *            how the date will be described to the user when asking what
     *            date they want.
     * @return the date the user specifies
     * @throws RuntimeException
     *             exception is thrown if the date isn't a real date.
     */
    static CalendarDate makeDate(String type) throws RuntimeException {
        Main.resetScanner();
        Util.println("Please enter the " + type + " date: ");
        Util.println("Month:");
        if (!(Main.scan.hasNextInt())) {
            Util.println("Invalid input! Please enter in an integer!");
            addNewOrderOption();
        }
        int month = Main.scan.nextInt();
        Main.resetScanner();
        Util.println("Day:");
        if (!(Main.scan.hasNextInt())) {
            Util.println("Invalid input! Please enter in an integer!");
            addNewOrderOption();
        }
        int day = Main.scan.nextInt();
        Main.resetScanner();
        Util.println("Year (in xx format):");
        if (!(Main.scan.hasNextInt())) {
            Util.println("Invalid input! Please enter in an integer!");
            addNewOrderOption();
        }
        int year = Main.scan.nextInt();

        return new CalendarDate(month, day, year);
    }

    /**
     * Method displays prompts to the user and has the user decide how much
     * discount the given customer will use.
     * 
     * @param customer
     *            the customer to use discount of.
     * @return the discount chosen for the customer.
     */
    static double pickDiscountFor(Customer customer) {
        Main.resetScanner();
        Util.println("Would the customer like to use any discount? (y/n)");
        if (Main.scanNoNext()) {
            Util.println("Invalid input! Please try again!");
            return pickDiscountFor(customer);
        }

        String input = Main.scan.next();

        if (input.equalsIgnoreCase("y")) {
            Util.println("Please enter a dollar amount above 0 "
                    + "and less than or"
                    + "equal to " + customer.getAvailableDiscount());
            Main.resetScanner();
            if (!(Main.scan.hasNextDouble())) {
                Util.println("Invalid input! Please enter again!");
                return pickDiscountFor(customer);
            }
            return Main.scan.nextDouble() * -1;
        }
        else if (input.equalsIgnoreCase("n")) {
            return 0.0;
        }
        else {
            Util.println("Invalid input! Please enter again!");
            return pickDiscountFor(customer);
        }
    }

    /**
     * Displays a menu to the user and has them filter a list of orders. The
     * user will then pick an order to view and perform updates on.
     */
    static void viewUpdateOrderOption() {

        if (Main.bakery.noOrders()) {
            Util.println("No orders to view!");
            orderMenu();
        }
        Main.resetScanner();
        Util.println("How would you like to view the orders?");
        Util.println("1. View all orders");
        Util.println("2. View orders for a specific customer");
        Util.println("3. View orders placed on a specific date");
        Util.println("4. View orders picked up on a specific date");
        Util.println("5. View orders based on bakery item ordered");
        Util.println("6. View unpaid orders");
        Util.println("7. Main menu");

        if (!(Main.scan.hasNextInt())) {
            Util.println("Invalid input! Please enter again!");
            viewUpdateOrderOption();
        }

        int input = Main.scan.nextInt();

        // Display the orders
        switch (input)
        {
            case 1:
                Util.println("Viewing all orders-");
                Main.bakery.displayAllOrders();
                break;

            case 2:
                Main.resetScanner();
                Util.println("Search by customer-");
                Util.println("Please designate a customer you would like to"
                        + " see the "
                        + "orders of");
                Customer chosenCustomer = CustomerMenu.pickACustomer();
                Main.bakery.displayOrdersFor(chosenCustomer);
                break;

            case 3:
                Main.resetScanner();
                Util.println("Search by date placed-");
                CalendarDate datePlaced = makeDate("placed");
                Main.bakery.displayOrdersPlacedOn(datePlaced);
                break;

            case 4:
                Util.println("Search by date picked up-");
                CalendarDate datePickedUp = makeDate("picked up");
                Main.bakery.displayOrdersPickedUpOn(datePickedUp);
                break;

            case 5:
                Main.resetScanner();
                Util.println("Search by bakery item ordered-");
                Main.bakery.displayAllBakeryItems();
                Util.println("Please enter the ID of the item you would like"
                        + " to order");
                if (!(Main.scan.hasNextInt())) {
                    Util.println("Invalid input! Please enter a valid integer"
                            + " ID");
                    viewUpdateOrderOption();
                }
                int inputItemID = Main.scan.nextInt();
                if (Main.bakery.containsBakeryItem(inputItemID)) {
                    Main.bakery.displayOrdersOf(inputItemID);
                }
                else {
                    Util.println("Bakery item not found. Please try again!");
                    viewUpdateOrderOption();
                }
                break;

            case 6:
                Util.println("Viewing unpaid orders");
                Main.bakery.displayOrdersHasPaid(false);
                break;

            case 7:
                Main.mainMenu();
                break;

            default:
                Util.println("Invalid input! Please enter an integer 1-7");
                viewUpdateOrderOption();
                break;
        }

        // Have the user decide what order they want to view
        Main.resetScanner();
        Util.println("Please enter the ID of the order you would like to "
                + "view/update:");
        if (!(Main.scan.hasNextInt())) {
            Util.println("Invalid input! Please try again.");
            viewUpdateOrderOption();
        }

        int inputOrderID = Main.scan.nextInt();
        if (Main.bakery.containsOrder(inputOrderID)) {
            viewUpdateSingleOrder(Main.bakery.getOrderByID(inputOrderID),
                    Main.bakery.customerWhoOrdered(inputOrderID));
        }
        else {
            Util.println("No order with that ID found. Please try again.");
            viewUpdateOrderOption();
        }
    }

    /**
     * Method displays the given order and allows the user to perform updates on
     * it before returning to the submenu for selecting an order to view and/or
     * update.
     * 
     * @param order
     *            the order to view and perform updates on
     * @param c the customer whose information will be displayed with the order
     */
    static void viewUpdateSingleOrder(Order order, Customer c) {
        Main.resetScanner();
        Util.println("Displaying order-");
        order.displayOrder(c);
        Util.println("Would you like to update this order or view another"
                + " order?");
        Util.println("1. Update this order");
        Util.println("2. View another order");
        if (!(Main.scan.hasNextInt())) {
            Util.println("Invalid input. Please try again.");
        }

        int input = Main.scan.nextInt();

        switch (input)
        {
            case 1:
                updateAnOrder(order);
                break;

            case 2:
                viewUpdateOrderOption();
                break;

            default:
                Util.println("Invalid input! Please enter in either 1 or 2");
                viewUpdateSingleOrder(order, c);
                break;
        }
    }

    /**
     * Method prompts user and updates the order fields depending on how they
     * respond.
     * 
     * @param order
     *            the order to update.
     */
    static void updateAnOrder(Order order) {
        Main.resetScanner();
        Util.println("What would you like to update in this order?");
        Util.println("1. ID");
        Util.println("2. Has Been Paid?");
        Util.println("3. Order date");
        Util.println("4. Pick up date");
        Util.println("5. Items ordered/Quantity Ordered");
        Util.println("6. Price Before Discount");
        Util.println("7. Discount used");
        Util.println("8. Nothing. Go back to viewing order.");

        if (!(Main.scan.hasNextInt())) {
            Util.println("Invalid input! Please try again.");
            updateAnOrder(order);
        }

        int input = Main.scan.nextInt();

        switch (input)
        {
            case 1:
                Main.resetScanner();
                Util.println("Updating order ID-");
                Util.println("Please enter the new ID:");
                if (!(Main.scan.hasNextInt())) {
                    Util.println("Invalid input! Please try again.");
                    updateAnOrder(order);
                }
                int newID = Main.scan.nextInt();
                // Check for duplicate IDs already in system
                if (Main.bakery.containsOrder(newID)) {
                    Util.printerr("enter an ID that is not in the database!");
                    updateAnOrder(order);
                }
                order.updateID(newID);
                Util.println("Order ID updated!");
                updateAnOrder(order);
                break;

            case 2:
                Main.resetScanner();
                Util.println("Updating if the order has been paid-");
                Util.println("Has the order been paid? (y/n)");
                if (Main.scanNoNext()) {
                    Util.println("Invalid input! Please try again!");
                    updateAnOrder(order);
                }

                String inputPaid = Main.scan.next();
                if (inputPaid.equalsIgnoreCase("y")) {
                    order.updatePaid(true);
                }
                else if (inputPaid.equalsIgnoreCase("n")) {
                    order.updatePaid(false);
                }
                else {
                    Util.println("Invalid input! Please try again!");
                    updateAnOrder(order);
                }
                Util.println("Order paid status updated!");
                updateAnOrder(order);
                break;

            case 3:
                Util.println("Updating ordered date-");
                CalendarDate newOrderDate = makeDate("new ordered");
                order.updateOrderedDate(newOrderDate);
                Util.println("Order date updated.");
                updateAnOrder(order);
                break;

            case 4:
                Util.println("Updating pick up date-");
                CalendarDate newPickUpDate = makeDate("new pick up");
                order.updatePickUpDate(newPickUpDate);
                Util.println("Pick up date updated.");
                updateAnOrder(order);
                break;

            case 5:
                Util.println("Update item/quantity ordered-");
                updateItemList(order);
                break;

            case 6:
                Main.resetScanner();
                Util.println("Update the total price before discount-");
                if (Main.scanNoNextDouble()) {
                    Util.println("Invalid input! Please enter a price.");
                    updateAnOrder(order);
                }
                double newPrice = Main.scan.nextDouble();
                if (newPrice <= 0) {
                    Util.println("Invalid input! Please enter a positive"
                            + " price!");
                    updateAnOrder(order);
                }
                order.updatePrice(newPrice);
                Util.println("Order price (before discount) updated.");
                updateAnOrder(order);
                break;

            case 7:
                Main.resetScanner();
                Util.println("Update the discount used");
                if (Main.scanNoNextInt()) {
                    Util.println("Invalid input! Please enter an integer!");
                    updateAnOrder(order);
                }
                int newDiscount = Main.scan.nextInt();
                if (newDiscount > order.calcTotalDue()) {
                    Util.printerr("enter a discount less than the price.");
                    updateAnOrder(order);
                }
                order.updateDiscount(newDiscount);
                Util.println("Order discount updated!");
                updateAnOrder(order);
                break;

            case 8:
                viewUpdateSingleOrder(order,
                        Main.bakery.customerWhoOrdered(order.getID()));
                break;

            default:
                Util.printerr("enter an integer 1-9");
                viewUpdateSingleOrder(order,
                        Main.bakery.customerWhoOrdered(order.getID()));
                break;
        }
    }

    /**
     * Method provides a console interface for the user to assist them in
     * picking items and quantities from the order to update.
     * 
     * @param order
     *            the order to update the bakery items and bakery item
     *            quantities of.
     */
    static void updateItemList(Order order) {
        ArrayList<ItemQuantityPair> itemList = order.getItemList();
        Main.resetScanner();
        Util.println("Would you like to update an item or quantity ordered?");
        Util.println("1. Update an item");
        Util.println("2. Update quantity");
        Util.println("3. Go back");
        if (Main.scanNoNextInt()) {
            Util.printerr("enter a valid integer option!");
            updateItemList(order);
        }
        int input = Main.scan.nextInt();

        switch (input)
        {
            case 1:
                Main.resetScanner();
                Util.println("Updating an item-");
                for (ItemQuantityPair pair : itemList) {
                    pair.getItem().displayItem();
                    System.out.println();
                }
                Util.println("Please pick an ID from the list:");
                if (Main.scanNoNextInt()) {
                    Util.printerr("enter a valid ID!");
                    updateItemList(order);
                }
                int inputIDCase1 = Main.scan.nextInt();
                if (!(pairHasItemID(itemList, inputIDCase1))) {
                    Util.printerr("enter an ID of a bakery item!");
                    updateItemList(order);
                }
                ItemQuantityPair pairToUpdate = findPairWithItemID(itemList,
                        inputIDCase1);
                Util.println("Now please select what item you would like to"
                        + " update "
                        + "your selected item to:");
                BakeryItem newItem = BakeryItemMenu.pickABakeryItem();
                pairToUpdate.updateItem(newItem);
                order.fixPrice();
                Util.println("Item successfully updated!");
                Util.println("Please note that the price has also been updated"
                        + " accordingly.");
                updateItemList(order);
                break;

            case 2:
                Main.resetScanner();
                Util.println("Updating a quantity-");
                for (ItemQuantityPair pair : itemList) {
                    pair.getItem().displayItem();
                    System.out.println();
                }
                Util.println("Please pick an ID from the list:");
                if (Main.scanNoNextInt()) {
                    Util.printerr("enter a valid ID!");
                    updateItemList(order);
                }
                int inputIDCase2 = Main.scan.nextInt();
                if (!(pairHasItemID(itemList, inputIDCase2))) {
                    Util.printerr("enter an ID of a bakery item!");
                    updateItemList(order);
                }
                Util.println("Please enter the new quantity of this item:");
                if (Main.scanNoNextInt()) {
                    Util.printerr("enter a valid integer quantity!");
                    updateItemList(order);
                }
                int newQuantity = Main.scan.nextInt();
                if (!(pairHasItemID(itemList, inputIDCase2))) {
                    Util.printerr("enter an ID of an item that is there!");
                    updateItemList(order);
                }
                // Updating quantity now
                ItemQuantityPair pairToUpdate2 = findPairWithItemID(itemList,
                        inputIDCase2);
                pairToUpdate2.updateQuantity(newQuantity);
                order.fixPrice();
                Util.println("Quantity successfully updated.");
                Util.println("Note that the price of the order has also been"
                        + " updated.");
                updateItemList(order);
                break;

            case 3:
                updateAnOrder(order);
                break;

            default:
                Util.printerr("enter a valid integer option!");
                updateItemList(order);
                break;
        }
    }

    /**
     * Method produces the ItemQuantityPair from the arraylist that has the
     * given ID.
     * 
     * @param itemList
     *            the list to go through to find the pair with the id
     * @param id
     *            the id of the pair that will be found
     * @return the ItemQuantityPair in the given arraylist of pairs that has the
     *         given id
     */
    static ItemQuantityPair findPairWithItemID(
            ArrayList<ItemQuantityPair> itemList, int id) {
        for (ItemQuantityPair pair : itemList) {
            if (pair.getItem().getID() == id) {
                return pair;
            }
        }
        throw new RuntimeException("findPairWithItemID- did not find pair!");
    }

    /**
     * Method determines if there exists a ItemQuantityPair in the given
     * arraylist that has the given id
     * 
     * @param itemList
     *            the array list of pairs to search through to see if it
     *            contains a pair with the given id
     * @param id
     *            the id to check the pairs of itemList for
     * @return boolean stating if there exists a ItemQuantityPair with in the
     *         given itemList with the given id
     */
    static boolean pairHasItemID(ArrayList<ItemQuantityPair> itemList,
            int id) {
        for (ItemQuantityPair pair : itemList) {
            if (pair.getItem().getID() == id) {
                return true;
            }
        }
        return false;
    }
}
