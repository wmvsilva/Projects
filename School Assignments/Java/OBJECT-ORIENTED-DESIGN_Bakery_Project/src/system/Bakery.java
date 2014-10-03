package system;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Class represents the database for a bakery which contains available bakery
 * items and customers with associated orders
 * 
 * @author William Silva, silva.w@husky.neu.edu@neu.edu
 * @version 18 June 2014
 * 
 */
class Bakery {

    /** list containing all of the bakery items available at this bakery */
    private ArrayList<BakeryItem> bakeryItems;

    /** list containing all customers (with any orders) at this bakery */
    private ArrayList<Customer> customers;

    /**
     * Constructor for Bakery which creates a bakery with no data
     */
    Bakery() {
        this.bakeryItems = new ArrayList<BakeryItem>();
        this.customers = new ArrayList<Customer>();
    }

    /**
     * Method takes in a file of a specific format for bakery items and adds all
     * the bakery items from that file.
     * 
     * @param bakeryItemsFile
     *            the file to load the bakery items from
     * @throws FileNotFoundException
     *             exception thrown if the file this is supposed to load is not
     *             found.
     */
    void initializeBakeryItems(File bakeryItemsFile) throws 
    FileNotFoundException, IllegalArgumentException {

        Scanner fileInput = new Scanner(bakeryItemsFile);
        if (!(fileInput.hasNextLine())) {
            badFileError();
        }

        String tab = "\t";

        if (!(fileInput.nextLine()
                .equalsIgnoreCase("BakeryItemID" + tab + "BakeryItemName" 
                        + tab + "Category" + tab + "Price"))) {
            badFileError();
        }
        fileInput.useDelimiter(Pattern.compile("(\\n)|(\\t)|(\\r\\n)"));

        while (fileInput.hasNext()) {
            if (!(fileInput.hasNextInt())) {
                badFileError();
            }
            int itemID = fileInput.nextInt();

            if (!(fileInput.hasNext())) {
                badFileError();
            }
            String name = fileInput.next();

            if (!(fileInput.hasNext())) {
                badFileError();
            }
            String category = fileInput.next();

            if (!(fileInput.hasNextDouble())) {
                badFileError();
            }
            double price = fileInput.nextDouble();

            bakeryItems.add(new BakeryItem(itemID, name, category, price));
        }

        fileInput.close();
    }

    /**
     * Method throws an exception that is appropriate for when a given file is
     * not what this program expects to be able to load from.
     */
    static void badFileError() {
        throw new IllegalArgumentException("File is of incorrect format.");
    }

    /**
     * Method takes in a text file containing bakery orders in a specific format
     * and loads the data to this bakery.
     * 
     * @param bakeryOrdersFile
     *            the file to load the bakery items from
     * @throws FileNotFoundException
     *             exception that is thrown when the file is not located in the
     *             current directory
     * @throws IllegalArgumentException
     *             exception that is thrown when the file is not the correct
     *             format that the program expects
     */
    void initializeBakeryOrders(File bakeryOrdersFile) throws 
    FileNotFoundException, IllegalArgumentException {

        Scanner fileInput = new Scanner(bakeryOrdersFile);
        if (!(fileInput.hasNextLine())) {
            badFileError();
        }

        String tab = "\t";
        if (!(fileInput.nextLine()
                .equalsIgnoreCase(
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
                        + "CurrentLoyalty")))
        {
            badFileError();
        }

        fileInput.useDelimiter(Pattern.compile("(\\n)|(\\t)|(\\r\\n)"));

        while (fileInput.hasNext()) {

            errorIfNoNextInt(fileInput);
            int customerID = fileInput.nextInt();

            errorIfNoNext(fileInput);
            String lastName = fileInput.next();

            errorIfNoNext(fileInput);
            String address = fileInput.next();

            errorIfNoNext(fileInput);
            String city = fileInput.next();
            errorIfNoNext(fileInput);
            String state = fileInput.next();

            errorIfNoNextInt(fileInput);
            int zipCode = fileInput.nextInt();

            // If there is just a customer here, add just the customer.
            // Otherwise, add the order also.
            if (fileInput.hasNext(Pattern.compile("\\n"))) {
                // Check to see if this customer is already in database.
                if (this.knownCustomer(customerID)) {
                    Customer c = this.getCustomerByID(customerID);
                    c.updateLastName(lastName);
                    c.updateAddress(address);
                    c.updateCity(city);
                    c.updateState(state);
                    c.updateState(state);
                }
                else { // If not in database, add to database.
                    customers.add(new Customer(customerID, lastName, address,
                            city, state, zipCode));
                }
            }
            else {
                errorIfNoNextInt(fileInput);
                int orderID = fileInput.nextInt();

                errorIfNoNext(fileInput);
                String hasPaidInput = fileInput.next();
                boolean hasPaid = false;
                if (hasPaidInput.equalsIgnoreCase("yes")) {
                    hasPaid = true;
                }
                else if (hasPaidInput.equalsIgnoreCase("no")) {
                    hasPaid = false;
                }
                else {
                    badFileError();
                }

                errorIfNoNext(fileInput);
                String orderDateInput = fileInput.next();
                CalendarDate orderDate = new CalendarDate(orderDateInput);

                errorIfNoNext(fileInput);
                String pickUpDateInput = fileInput.next();
                CalendarDate pickUpDate = new CalendarDate(pickUpDateInput);

                errorIfNoNextInt(fileInput);
                int bakeryItemID = fileInput.nextInt();

                errorIfNoNext(fileInput);
                // Unused because BakeryItem can be determined from ID
                @SuppressWarnings("unused")
                String bakeryItemName = fileInput.next();

                errorIfNoNext(fileInput);
                // Unused because BakeryItem can be determined from ID
                @SuppressWarnings("unused")
                String bakeryItemCategory = fileInput.next();

                errorIfNoNextInt(fileInput);
                int quantity = fileInput.nextInt();
                if (quantity <= 0) {
                    badFileError();
                }

                // Price for item
                errorIfNoNextDouble(fileInput);
                double itemPrice = fileInput.nextDouble();
                if (itemPrice <= 0) {
                    badFileError();
                }

                BakeryItem bakeryItem;
                // Computing the bakery item!
                if (this.containsBakeryItem(bakeryItemID)) {
                    bakeryItem = this.getBakeryItem(bakeryItemID);
                }
                else {
                    throw new IllegalArgumentException("Bakery Item not "
                            + "found.");
                }

                // Total price before discount
                errorIfNoNextDouble(fileInput);
                double total = fileInput.nextDouble();
                if (total <= 0) {
                    badFileError();
                }

                // Discount used on order
                errorIfNoNextDouble(fileInput);
                double discountUsedOnOrder = fileInput.nextDouble();
                if (discountUsedOnOrder > 0) {
                    badFileError();
                }

                // Total Due
                errorIfNoNextDouble(fileInput);
                double totalDue = fileInput.nextDouble();
                if (totalDue < 0) {
                    badFileError();
                }
                // AvailableDiscount
                errorIfNoNextDouble(fileInput);
                double availableDiscount = fileInput.nextDouble();
                if (availableDiscount < 0) {
                    badFileError();
                }
                
                // CurrentLoyalty
                errorIfNoNextDouble(fileInput);
                double currentLoyalty = fileInput.nextDouble();
                if (currentLoyalty < 0) {
                    badFileError();
                }
                ArrayList<ItemQuantityPair> itemList = 
                        new ArrayList<ItemQuantityPair>();
                itemList.add(new ItemQuantityPair(bakeryItem, quantity));

                Order order = new Order(orderID, hasPaid, orderDate,
                        pickUpDate, itemList, total, discountUsedOnOrder);

                if (this.knownCustomer(customerID)) {
                    Customer c = this.getCustomerByID(customerID);
                    c.updateLastName(lastName);
                    c.updateAddress(address);
                    c.updateCity(city);
                    c.updateState(state);
                    c.updateZipCode(zipCode);
                    c.updateAvailableDiscount(availableDiscount);
                    c.updateCurrentLoyalty(currentLoyalty);
                    c.addOrder(order);
                }
                else {
                    this.addCustomerWithID(customerID, lastName, address, city,
                            state, zipCode);
                    Customer c = this.getCustomerByID(customerID);
                    c.addOrder(order);
                }

            }
        }
    }

    /**
     * Method produces an IllegalArguementException if the given input is not
     * able to scan in a double next
     * 
     * @param input
     *            the scanner to input check for
     */
    static void errorIfNoNextDouble(Scanner input) {
        if (!(input.hasNextDouble())) {
            badFileError();
        }
    }

    /**
     * Method produces an IllegalArguementException if the given input is not
     * able to scan in a integer next
     * 
     * @param input
     *            the scanner to input check for
     */
    static void errorIfNoNextInt(Scanner input) {
        if (!(input.hasNextInt())) {
            badFileError();
        }
    }

    /**
     * Method produces an IllegalArguementException if the given input is not
     * able to scan in anything next
     * 
     * @param input
     *            the scanner to input check for
     */
    static void errorIfNoNext(Scanner input) {
        if (!(input.hasNext())) {
            badFileError();
        }
    }

    /**
     * Method adds a new customer with the given fields to the database and
     * generates a unique integer ID for them which is returned.
     * 
     * @param lastName
     *            the last name of the customer
     * @param address
     *            the address of the customer's residence
     * @param city
     *            the customer's city of residence
     * @param state
     *            the customer's state of residence
     * @param zipCode
     *            the customer's zipcode
     * @return the ID of the customer added.
     */
    int addCustomer(String lastName, String address, String city, String state,
            int zipCode) {
        int newCustomerID = this.generateCustomerID();
        customers.add(new Customer(newCustomerID, lastName, address, city,
                state, zipCode));
        return newCustomerID;
    }

    /**
     * Method adds a customer built with the given fields to the customer
     * database
     * 
     * @param customerID
     *            a unique integer ID for the customer
     * @param lastName
     *            the last name of the customer
     * @param address
     *            the address of the customer's residence
     * @param city
     *            the customer's city of residence
     * @param state
     *            the customer's state of residence
     * @param zipCode
     *            the customer's zipcode
     */
    void addCustomerWithID(int customerID, String lastName, String address,
            String city, String state, int zipCode) {
        customers.add(new Customer(customerID, lastName, address, city, state,
                zipCode));
    }

    /**
     * Method produces a unique integer ID for a customer
     * 
     * @return a customer ID that no other customer has.
     */
    int generateCustomerID() {
        int count = 0;
        while (true) {
            if (this.knownCustomer(count)) {
                count++;
            }
            else {
                return count;
            }
        }
    }

    /**
     * Method determines if this bakery contains a customer with the given last
     * name.
     * 
     * @param lastName
     *            the last name to search customers for
     * @return boolean stating if this bakery has a customer with the given last
     *         name
     */
    boolean hasCustomerWithName(String lastName) {
        for (Customer c : customers) {
            if (c.getLastName().equalsIgnoreCase(lastName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method retrieves a customer from this with the given last name.
     * 
     * @param lastName
     *            the last name of the customer to retrieve
     * @return the customer in this database with the last name (if more than
     *         one has this last name, it will be the least recently added)
     */
    Customer getCustomerByName(String lastName) {
        for (Customer c : customers) {
            if (c.getLastName().equalsIgnoreCase(lastName)) {
                return c;
            }
        }
        throw new RuntimeException("getCustomerByName- customer not found!");
    }

    /**
     * Method determines if this bakery has a customer with the given id
     * 
     * @param id
     *            the customer id to search this bakery for
     * @return boolean stating if this bakery has a customer with the given id
     */
    boolean knownCustomer(int id) {
        for (Customer c : customers) {
            if (c.getID() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method finds a customer in this with the given id and throws an exception
     * if they are not found.
     * 
     * @param id
     *            the customer id of the customer to retrieve
     * @return the customer in this database who has the given id
     */
    Customer getCustomerByID(int id) {
        for (Customer c : customers) {
            if (c.getID() == id) {
                return c;
            }
        }
        throw new RuntimeException("getCustomerByID- customer not found!");
    }

    /**
     * Displays all customers in the console. Shows their customer ID and name.
     */
    void displayAllCustomers() {
        for (Customer c : customers) {
            c.displayCustomer();
            System.out.println();
        }
    }

    /**
     * Does this bakery have no customers in the database?
     * 
     * @return boolean stating if this bakery has no customers
     */
    boolean noCustomers() {
        return customers.size() == 0;
    }

    /**
     * Method retrieves the bakery item with the given id
     * 
     * @param id
     *            the id of the bakery item to retrieve
     * @return the bakery item in this bakery with the given id
     */
    BakeryItem getBakeryItem(int id) {
        for (BakeryItem b : bakeryItems) {
            if (b.getID() == id) {
                return b;
            }
        }
        throw new RuntimeException("getBakeryItem- item with given id "
                + "not found!");
    }

    /**
     * Displays all of the bakery items in console with at least their ID and
     * name.
     */
    void displayAllBakeryItems() {
        for (BakeryItem b : bakeryItems) {
            b.displayItem();
            System.out.println();
        }
    }

    /**
     * Method adds a bakery item with the given fields to the database and
     * generates a unique integer ID for it
     * 
     * @param name
     *            the name of the bakery item
     * @param category
     *            the food category of the bakery item
     * @param price
     *            the dollar price of the bakery item
     * @return the integer bakery item ID that the new bakery item was assigned
     */
    int addBakeryItem(String name, String category, double price) {
        int newBakeryItemID = this.generateBakeryID();
        bakeryItems.add(new BakeryItem(newBakeryItemID, name, 
                category, price));
        return newBakeryItemID;
    }

    /**
     * Method generates a unique integer ID for a bakery item
     * 
     * @return an integer bakery item ID that no other bakery item has.
     */
    int generateBakeryID() {
        int count = 0;
        while (true) {
            if (this.containsBakeryItem(count)) {
                count++;
            }
            else {
                return count;
            }
        }
    }

    /**
     * Are there no bakery items in the database?
     * 
     * @return boolean stating if there are no bakery items in this
     */
    boolean noBakeryItems() {
        return bakeryItems.size() == 0;
    }

    /**
     * Method determines if this has a bakery item with the given id
     * 
     * @param id
     *            the id of the bakery item to check the this for
     * @return boolean stating if this bakery database contains a bakery item
     *         for sale with the given ID.
     */
    boolean containsBakeryItem(int id) {
        for (BakeryItem b : bakeryItems) {
            if (b.getID() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method adds an order to the customer with the given id's records and
     * returns the unique order id for this new order
     * 
     * @param customerID
     *            the id of the customer who has ordered
     * @param hasPaid
     *            boolean stating if the customer has paid yet
     * @param orderDate
     *            the date the order was made
     * @param pickUpDate
     *            the date the order will be picked up
     * @param itemList
     *            the list of bakery item and quantity associatations
     *            representing what the customer is ordered
     * @param d
     *            the discount the customer is using for this orders
     * @return order ID of the order added to the database.
     */
    int addOrder(int customerID, boolean hasPaid, CalendarDate orderDate,
            CalendarDate pickUpDate, ArrayList<ItemQuantityPair> itemList,
            double d) {

        Customer c = this.getCustomerByID(customerID);
        int newOrderID = this.generateOrderID();
        double calculatedTotal = ItemQuantityPair.calculatePrice(itemList);
        Order order = new Order(newOrderID, hasPaid, orderDate, pickUpDate,
                itemList, calculatedTotal, d);
        c.addOrder(order);
        return newOrderID;
    }

    /**
     * Method generates an order ID that has not been used by the database
     * system yet.
     * 
     * @return a unique integer order ID that the system has no used yet
     */
    int generateOrderID() {
        int count = 0;
        while (true) {
            if (this.containsBakeryItem(count)) {
                count++;
            }
            else {
                return count;
            }
        }
    }

    /**
     * Method displays a receipt for the order with the given ID
     * 
     * @param orderID
     *            the id of the order to display
     */
    void displayOrder(int orderID) {
        for (Customer c : customers) {
            for (Order o : c.getOrders()) {
                if (o.getID() == orderID) {
                    o.displayOrder(c);
                    return;
                }
            }
        }
    }

    /**
     * Method determines if this has an order with the given id
     * 
     * @param id
     *            the order id to check for
     * @return boolean stating if the database contains an order with that ID.
     */
    boolean containsOrder(int id) {
        for (Customer c : customers) {
            for (Order o : c.getOrders()) {
                if (o.getID() == id) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Method retrieves the order with the given id from the database
     * 
     * @param id
     *            the order id of the order to produce
     * @return order from this with the given id
     */
    Order getOrderByID(int id) {
        for (Customer c : customers) {
            for (Order o : c.getOrders()) {
                if (o.getID() == id) {
                    return o;
                }
            }
        }
        throw new RuntimeException("Order with ID " + id + " not found!");
    }

    /**
     * Displays the orders that have the paid status of the given b. The display
     * of each order should have at a minimum the order ID.
     * 
     * @param b
     *            the paid status of the orders to display
     */
    void displayOrdersHasPaid(boolean b) {
        for (Customer c : customers) {
            for (Order o : c.getOrders()) {
                if (o.getPaid() == b) {
                    o.displayOrder(c);
                    System.out.println();
                }
            }
        }
    }

    /**
     * Displays all the orders this bakery database holds. In console, at a
     * minimum, the ID of each order is shown.
     */
    void displayAllOrders() {
        for (Customer c : customers) {
            for (Order o : c.getOrders()) {
                o.displayOrder(c);
                System.out.println();
            }
        }
    }

    /**
     * Display all orders for the given customer
     * 
     * @param chosenCustomer
     *            the customer to display all orders of
     */
    void displayOrdersFor(Customer chosenCustomer) {
        int chosenID = chosenCustomer.getID();
        for (Customer c : customers) {
            if (c.getID() == chosenID) {
                for (Order o : c.getOrders()) {
                    o.displayOrder(c);
                }
                return;
            }
        }
    }

    /**
     * Displays all orders placed on a given date
     * 
     * @param datePlaced
     *            the date to check orders to see if they were placed on
     */
    void displayOrdersPlacedOn(CalendarDate datePlaced) {
        for (Customer c : customers) {
            for (Order o : c.getOrders()) {
                if (o.getOrderDate().sameDate(datePlaced)) {
                    o.displayOrder(c);
                }
            }
        }
    }

    /**
     * Method displays orders picked up on a given date
     * 
     * @param datePickedUp
     *            the date that orders will be checked to see if they were
     *            picked up on
     */
    void displayOrdersPickedUpOn(CalendarDate datePickedUp) {
        for (Customer c : customers) {
            for (Order o : c.getOrders()) {
                if (o.getPickedUpDate().sameDate(datePickedUp)) {
                    o.displayOrder(c);
                }
            }
        }
    }

    /**
     * Displays all the orders that have ordered some quantity of the item with
     * the given inputItemID
     * 
     * @param id
     *            the bakery item id to check to see if orders contain
     */
    void displayOrdersOf(int id) {
        for (Customer c : customers) {
            for (Order o : c.getOrders()) {
                if (o.containsBakeryItem(id)) {
                    o.displayOrder(c);
                    System.out.println();
                }
            }
        }
    }

    /**
     * Does this bakery have no orders?
     * 
     * @return boolean stating if no customers have orders
     */
    boolean noOrders() {
        for (Customer c : customers) {
            if (c.getOrders().size() != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * An iterable arraylist that goes through every bakery item contained in
     * this bakery database.
     * 
     * @return an iterable array list containing all available bakery items
     */
    ArrayList<BakeryItem> iterateBakeryItems() {
        return bakeryItems;
    }

    /**
     * Method produces an iterable list that contains all customers
     * 
     * @return arraylist containing all of the customers
     */
    ArrayList<Customer> iterateCustomers() {
        return customers;
    }

    /**
     * Method produces the customer who has an associated order with the given
     * id
     * 
     * @param id
     *            the order id to check customers for
     * @return customer who had the order with the given id
     */
    Customer customerWhoOrdered(int id) {
        for (Customer c : customers) {
            for (Order o : c.getOrders()) {
                if (o.getID() == id) {
                    return c;
                }
            }
        }
        throw new RuntimeException("customerWhoOrdered- Customer not found!");
    }

}
