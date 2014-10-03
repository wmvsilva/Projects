package system;

import java.util.ArrayList;

/**
 * Class represents a bakery order by a customer in which they have purchased at
 * least one of the available bakery items.
 * 
 * @author Nam Luu, luunhat.n@husky.neu.edu
 * @author William Silva, silva.w@husky.neu.edu
 * @version 06/17/2014
 */
class Order {

    /** ID of this order */
    private int id;
    /** Represents the paid status of this order */
    private boolean paid;
    /** order date of this order */
    private CalendarDate orderDate;
    /** pickup date of this order */
    private CalendarDate pickUpDate;
    /** items in this order */
    private ArrayList<ItemQuantityPair> itemList;
    /** price before discount */
    private double priceBeforeDiscount;
    /** the discount that the customer has used on this order */
    private double discountUsed;

    /**
     * Constructs a new order with the given ID, paid status, order date, pick
     * up date, bakery item, quantity, total and discount used on order
     * 
     * @param orderID
     *            the given ID
     * @param hasPaid
     *            the paid status
     * @param orderDate
     *            the order date
     * @param pickUpDate
     *            the pick up date
     * @param itemList
     *            the item-quantity pairs that are being ordered
     * @param total
     *            the total
     * @param discountUsedOnOrder
     *            discount used on order
     */
    Order(int orderID, boolean hasPaid, CalendarDate orderDate,
            CalendarDate pickUpDate, ArrayList<ItemQuantityPair> itemList,
            double total, double discountUsedOnOrder) {
        this.id = orderID;
        this.paid = hasPaid;
        this.orderDate = orderDate;
        this.pickUpDate = pickUpDate;
        this.itemList = itemList;
        this.priceBeforeDiscount = total;
        this.discountUsed = discountUsedOnOrder;
    }

    /**
     * Displays, in console, all the information the user of the bakery system
     * may need to know about this particular order.
     * 
     * @param customer the customer who ordered this whose information will
     * be displayed with this order
     */
    void displayOrder(Customer customer) {
        System.out.println("Viewing order with ID: " + this.getID());
        System.out.println("Order Date: " + orderDate.toString());
        System.out.println("Pickup Date: " + pickUpDate.toString());
        System.out.println("Customer: " + customer.getLastName() + " with ID: "
                + customer.getID());
        System.out.println("Items Ordered: ");
        displayAllOrderItems();
        System.out.println("Total: $" + this.getPriceBeforeDiscount());
        System.out.println("Discount used: -$" + this.getDiscountUsed());
        System.out.println("Total due: $" + this.calcTotalDue());

    }

    /**
     * Method displays in console information about all the items in this order
     */
    void displayAllOrderItems() {
        for (ItemQuantityPair pair : itemList) {
            BakeryItem b = pair.getItem();
            System.out.println(pair.priceToString() + " " + b.getName() + " x"
                    + pair.getQuantity() + ", ID: " + b.getID());
        }
    }

    /**
     * Method updates the discount used in this to the given newDiscount
     * 
     * @param newDiscount
     *            the discount to update this to
     */
    void updateDiscount(double newDiscount) {
        this.discountUsed = newDiscount;
    }

    /**
     * Method updates the price before discount
     * 
     * @param newPrice
     *            the new price of this order
     */
    void updatePrice(double newPrice) {
        this.priceBeforeDiscount = newPrice;
    }

    /**
     * Updates the pick up date of this order
     * 
     * @param newPickUpDate
     *            the new pick up date
     */
    void updatePickUpDate(CalendarDate newPickUpDate) {
        this.pickUpDate = newPickUpDate;
    }

    /**
     * Updates the order date of this order
     * 
     * @param newOrderDate
     *            the new order date
     */
    void updateOrderedDate(CalendarDate newOrderDate) {
        this.orderDate = newOrderDate;
    }

    /**
     * Updates the paid status of this order
     * 
     * @param b
     *            the new paid status of this order
     */
    void updatePaid(boolean b) {
        this.paid = b;
    }

    /**
     * Updates the id of this order. Note that it should not be updated to an ID
     * that currently exists with another order.
     * 
     * @param newID
     *            the new ID
     */
    void updateID(int newID) {
        this.id = newID;
    }

    /**
     * Method produces a string representation of the order date
     * 
     * @return a represenation of the order date in the form x'/x'/xx with the '
     *         meaning that x can consist of a single digit or two digits.
     */
    String getOrderDateString() {
        return this.orderDate.toString();
    }

    /**
     * Gets the ID of this order
     * 
     * @return the ID of this order
     */
    int getID() {
        return this.id;
    }

    /**
     * Method returns a string represenation of whether the order has been paid
     * or not
     * 
     * @return "Yes" if the order has been paid or "No" if the order has not
     *         been paid
     */
    String getHasPaidString() {
        if (this.paid) {
            return "Yes";
        }
        else {
            return "No";
        }
    }

    /**
     * Gets the pick up date of this order
     * 
     * @return the pick up date of this order
     */
    String getPickUpDateString() {
        return pickUpDate.toString();
    }

    /**
     * Gets the price before discount
     * 
     * @return the price before discount
     */
    double getPriceBeforeDiscount() {
        return priceBeforeDiscount;
    }

    /**
     * Returns the discount used by the customer for this order
     * 
     * @return the discount used
     */
    double getDiscountUsed() {
        return discountUsed;
    }

    /**
     * Calculates the total due after the discount
     * 
     * @return the total due after the discount
     */
    double calcTotalDue() {
        return priceBeforeDiscount + discountUsed;
    }

    /**
     * Returns the paid status
     * 
     * @return boolean representing if this order has been paid or not
     */
    boolean getPaid() {
        return this.paid;
    }

    /**
     * Returns order date
     * 
     * @return the order date
     */
    CalendarDate getOrderDate() {
        return this.orderDate;
    }

    /**
     * Returns pick up date
     * 
     * @return the pick up date
     */
    CalendarDate getPickedUpDate() {
        return this.getPickedUpDate();
    }

    /**
     * Method produces the list of bakery-item pairs of this orders
     * 
     * @return array list of data on what was ordered and how much
     */
    ArrayList<ItemQuantityPair> getItemList() {
        return this.itemList;
    }

    /**
     * Method adds to this order record the number of a specific bakery item
     * from the given pair
     * 
     * @param pair
     *            the pair containing the bakery item and quantity combo to be
     *            added to this
     */
    void addItemToOrder(ItemQuantityPair pair) {
        itemList.add(pair);
    }

    /**
     * Method determines if this order contains a quantity of the bakery item
     * with the given id
     * 
     * @param itemID
     *            the id to search bakery items of this order for
     * @return boolean stating if this order contains a bakery item with the
     *         given ID
     */
    boolean containsBakeryItem(int itemID) {
        for (ItemQuantityPair pair : itemList) {
            if (pair.getItem().getID() == itemID) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method updates the price of this order to what is supposed to be based on
     * the prices and quantities of the given items. This is used if the
     * quanities or bakery items ordered is updated.
     */
    void fixPrice() {
        double result = 0;
        for (ItemQuantityPair pair : itemList) {
            result = result + pair.totalPrice();
        }
        priceBeforeDiscount = result;
    }

}
