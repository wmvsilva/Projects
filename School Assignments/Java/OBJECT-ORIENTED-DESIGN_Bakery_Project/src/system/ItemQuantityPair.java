package system;

import java.util.ArrayList;

/**
 * Class holds a bakery item and integer quantity representing the item that is
 * being ordered and how many units of that item are being ordered,
 * 
 * @author William Silva, silva.w@husky.neu.edu
 * @version 17 June 2014
 * 
 */
class ItemQuantityPair {

    /** the bakery item being ordered */
    private BakeryItem item;
    /** the quantity of the bakery item being ordered */
    private int quantity;

    /**
     * Constructor for ItemQuantityPair
     * 
     * @param item
     *            the bakery item representing what is being ordered
     * @param quantity
     *            the units of the item of this pair being ordered
     */
    ItemQuantityPair(BakeryItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    /**
     * Accessor method for the item field of this
     * 
     * @return the item being ordered from this pair.
     */
    BakeryItem getItem() {
        return this.item;
    }

    /**
     * Accessor method for the quantity field of this
     * 
     * @return the quantity of the item being ordered
     */
    int getQuantity() {
        return this.quantity;
    }

    /**
     * Method calculates the total price of ordering the specified number of
     * units of the bakery item of this pair
     * 
     * @return the total price of ordering a quantity of the bakery item.
     */
    double totalPrice() {
        return item.getPrice() * quantity;
    }

    /**
     * Method returns string representation of the price of ordering the
     * specified quantity of the specified item
     * 
     * @return string representation of the total due in the form $x.xx
     */
    String priceToString() {
        int dollars = (int) this.totalPrice();
        double cents = this.totalPrice() - dollars;
        String centsString = cents + "";
        int lengthToUse = 3;
        if (centsString.length() > 4) {
            lengthToUse = 4;
        }
        centsString = centsString.substring(2, lengthToUse);
        if (centsString.length() == 1) {
            centsString = centsString + "0";
        }
        return "$" + dollars + "." + centsString;

    }

    /**
     * Method updates the quantity field of this
     * 
     * @param newQuantity
     *            the new quantity of the item
     */
    void updateQuantity(int newQuantity) {
        quantity = newQuantity;
    }

    /**
     * Method updtes the item field of this
     * 
     * @param newItem
     *            the new item being ordered
     */
    void updateItem(BakeryItem newItem) {
        this.item = newItem;
    }
    
    /**
     * Calculates the price for ordering a given quantity of a given bakery
     * item.
     * 
     * @param itemPairs
     *            the given item-quantity pairs to calculate the total price of
     * @return the total price of purchasing the given quanities of everything
     *         in the given itemPairs
     */
    static double calculatePrice(ArrayList<ItemQuantityPair> itemPairs) {
        double result = 0.0;
        for (ItemQuantityPair p : itemPairs) {
            result = result + p.totalPrice();
        }
        return result;
    }
}
