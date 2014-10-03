package system;

/**
 * Represents an available bakery item of a bakery
 * 
 * @author Nam Luu, luunhat.n@husky.neu.edu
 * @author William Silva, silva.w@husky.neu.edu
 * @version 16 June 2014
 */
class BakeryItem {

    /** the ID of this item */
    private int id;
    /** the name of this item */
    private String name;
    /** the category of this item */
    private String category;
    /** the price of this item */
    private double price;

    /**
     * Constructor for a standard BakeryItem
     * 
     * @param itemID
     *            the unique integer ID associated with this item
     * @param name
     *            the name of the bakery item
     * @param category
     *            what category of food this bakery item is in
     * @param price
     *            the price per unit of this item
     */
    BakeryItem(int itemID, String name, String category, double price) {
        this.id = itemID;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    /**
     * Display all of the information of this items
     */
    void displayItem() {
        System.out.println(id + " " + name + " " + category + " "
                + this.priceToString());
    }

    /**
     * Updates the name of this item
     * 
     * @param newName
     *            the new name of this item
     */
    void updateName(String newName) {
        this.name = newName;
    }

    /**
     * Updates the id of this item
     * 
     * @param newID
     *            the new id of this item
     */
    void updateID(int newID) {
        this.id = newID;
    }

    /**
     * Updates the category of this item
     * 
     * @param newCategory
     *            the new category of this item
     */
    void updateCategory(String newCategory) {
        this.category = newCategory;
    }

    /**
     * Updates the price of this item
     * 
     * @param newPrice
     *            the price of this item
     */
    void updatePrice(double newPrice) {
        this.price = newPrice;
    }

    /**
     * Returns the id of this item
     * 
     * @return the id of this item
     */
    int getID() {
        return id;
    }

    /**
     * Returns the name of this item
     * 
     * @return the name of this item
     */
    String getName() {
        return name;
    }

    /**
     * Returns the category of this item
     * 
     * @return the category of this item
     */
    String getCategory() {
        return category;
    }

    /**
     * Returns the price of this item
     * 
     * @return the price of this item
     */
    double getPrice() {
        return price;
    }

    /**
     * Method produces a string representation of the price of this
     * 
     * @return string representation of the price of this in $x.xx format
     */
    String priceToString() {
        int dollars = (int) price;
        double cents = price - dollars;
        String centsString = cents + "";
        int lengthToUse = 1;
        if (centsString.length() >= 4) {
            lengthToUse = 4;
        }
        centsString = centsString.substring(2, lengthToUse);
        if (centsString.length() == 1) {
            centsString = centsString + "0";
        }
        return "$" + dollars + "." + centsString;

    }

}
