package system;

import java.util.ArrayList;

/**
 * Represents a customer in the bakery database who may or may not have orders
 * 
 * @author Nam Luu, luunhat.n@husky.neu.edu
 * @author William Silva, silva.w@husky.neu.edu
 * @version 16 June 2014
 */
class Customer {

    /** ID of this customer */
    private int id;
    /** last name of this customer */
    private String lastName;
    /** address of this customer */
    private String address;
    /** city that the customer lives in */
    private String city;
    /** state that the customer lives in */
    private String state;
    /** zip code of this customer */
    private int zipCode;
    /** the available discount of this customer */
    private double availableDiscount;
    /** the current loyalty points of this customer */
    private double currentLoyalty;
    /** all the orders of this customer */
    private ArrayList<Order> orders;

    /**
     * Constructor for a customer
     * 
     * @param customerID
     *            the unique integer ID of the customer
     * @param lastName
     *            the last name of the customer
     * @param address
     *            the address of where the customer resides
     * @param city
     *            the customer's city of residence
     * @param state
     *            the customer's state of residence
     * @param zipCode
     *            the zip code of the customer's residence
     */
    Customer(int customerID, String lastName, String address, String city,
            String state, int zipCode) {
        this.id = customerID;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.orders = new ArrayList<Order>();
        this.availableDiscount = 0.0;
        this.currentLoyalty = 0.0;
    }

    /**
     * Display the contact information and loyalty status of this customer
     */
    void displayCustomer() {
        System.out.println("Last name: " + lastName + " with ID: " + this.id);
        System.out.println("Address: " + address + ", " + city + ", " + state
                + " " + this.getZipCodeString());
        System.out.println("Loyalty status: " + currentLoyalty);
    }

    /**
     * Updates the ID of this customer
     * 
     * @param nextInt
     *            the new ID of this
     */
    void updateID(int nextInt) {
        id = nextInt;
    }

    /**
     * Updates the last name of this customer
     * 
     * @param next
     *            the new last name of this
     */
    void updateLastName(String next) {
        lastName = next;
    }

    /**
     * Updates the address of this customer
     * 
     * @param nextLine
     *            the new address of this
     */
    void updateAddress(String nextLine) {
        address = nextLine;
    }

    /**
     * Updates the city where the customer lives in
     * 
     * @param next
     *            the new city of this
     */
    void updateCity(String next) {
        city = next;
    }

    /**
     * Updates the state where the customer lives in
     * 
     * @param next
     *            the new city of this
     */
    void updateState(String next) {
        state = next;
    }

    /**
     * Updates the available discounts of this customer
     * 
     * @param nextDouble
     *            the available discount of this
     */
    void updateAvailableDiscount(double nextDouble) {
        availableDiscount = nextDouble;
    }

    /**
     * Updates the current loyalty of this customer
     * 
     * @param nextDouble
     *            the new current loyalty of this
     */
    void updateCurrentLoyalty(double nextDouble) {
        currentLoyalty = nextDouble;
    }

    /**
     * Updates the zip code of this customer
     * 
     * @param newZip
     *            the new zipcode of this
     */
    void updateZipCode(int newZip) {
        this.zipCode = newZip;
    }

    /**
     * Returns the available discount of the customer
     * 
     * @return the available discount of the customer
     */
    double getAvailableDiscount() {
        return availableDiscount;
    }

    /**
     * Returns the id of this customer
     * 
     * @return the ID of this customer
     */
    int getID() {
        return id;
    }

    /**
     * Returns the last name of this customer
     * 
     * @return the last name of this customer
     */
    String getLastName() {
        return lastName;
    }

    /**
     * Returns the address of this customer
     * 
     * @return the address of this customer
     */
    String getAddress() {
        return address;
    }

    /**
     * Return the city of this customer
     * 
     * @return the city of this customer
     */
    String getCity() {
        return city;
    }

    /**
     * Returns the state of this customer
     * 
     * @return the state of this customer
     */
    String getState() {
        return state;
    }

    /**
     * Returns the zip code of this customer
     * 
     * @return the zip code of this customer
     */
    int getZipCode() {
        return zipCode;
    }
    
    /**
     * Returns the string representation of the zipcode
     * 
     * @return the string of this zipcode
     */
    String getZipCodeString() {
        String result = zipCode + "";
        while (result.length() < 5) {
            result = "0" + result;
        }
        return result;
    }

    /**
     * Does this customer have no orders?
     * 
     * @return boolean stating if this customer has no orders
     */
    boolean noOrders() {
        return orders.size() == 0;
    }

    /**
     * Returns the list containing all of the orders of this customer
     * 
     * @return Arraylist containing all of the orders.
     */
    ArrayList<Order> iterateOrders() {
        return orders;
    }

    /**
     * Method adds the order to the record of this customer's orders. If the
     * given order has the same ID as an existing order, the items are combined
     * 
     * @param order
     *            the order to add to this customer's records
     */
    void addOrder(Order order) {
        if (this.hasOrderWithID(order.getID())) {
            this.getOrderWithID(order.getID()).addItemToOrder(
                    order.getItemList().get(0));
        }
        else {
            // Loyalty is updated if its a new order being added!
            this.newOrderLoyalty(order.calcTotalDue());
            orders.add(order);
        }
    }

    /**
     * Updates the available discount and current loyalty depending on the price
     * of what the customer ordered.
     * 
     * @param orderPrice the price of the order that was added
     */
    void newOrderLoyalty(double orderPrice) {
        currentLoyalty = currentLoyalty + orderPrice;
        double discountToAdd = 0.0;
        while (currentLoyalty >= 100) {
            currentLoyalty = currentLoyalty - 100;
            discountToAdd = discountToAdd + 10;
        }
        availableDiscount = availableDiscount + discountToAdd;
    }

    /**
     * Method determines if this customer has an order with the given order id
     * 
     * @param orderID
     *            the order id to check for
     * @return boolean stating if this customer has an order with the given id
     */
    boolean hasOrderWithID(int orderID) {
        for (Order o : orders) {
            if (o.getID() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method returns the order with the given ID in this customer
     * 
     * @param orderID
     *            the ID to search this customer's order records for
     * @return the order of this with the given id
     */
    Order getOrderWithID(int orderID) {
        for (Order o : orders) {
            if (o.getID() == id) {
                return o;
            }
        }
        throw new RuntimeException("getOrderWithID- order not found!");
    }

    /**
     * Method returns the orders field of this
     * 
     * @return the orders of this customer
     */
    ArrayList<Order> getOrders() {
        return this.orders;
    }

    /**
     * Method returns the currentLoyalty field of this
     * 
     * @return the current loyalty of this customer
     */
    double getCurrentLoyalty() {
        return this.currentLoyalty;
    }

}
