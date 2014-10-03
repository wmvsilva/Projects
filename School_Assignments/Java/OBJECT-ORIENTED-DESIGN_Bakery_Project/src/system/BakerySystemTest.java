package system;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

/**
 * Testing class for KVMap
 * 
 * @author William Silva, silva.w@husky.neu.edu
 * @version May 16 2014
 * 
 */
public class BakerySystemTest {

    /** Bakery with no data */
    Bakery b1;
    /** Bakery initialized with bakeryItems.txt and orders.txt */
    Bakery fullb;
    /** BakeryItem ID:1, "Apple","Pie", 9.99 */
    BakeryItem bitem;
    /** ItemQuantityPair: bitem x1 */
    ItemQuantityPair bitemPair;
    /** Order ID: 3, Not Paid, 11/11/11, 1/1/12, 
     * Ordering bitem, $9.99, -$1.00 
     * */
    Order border;
    
    /** ID: 1, Name: Nam, Address: 33 Boston MA 02115 */
    Customer c1;
    /** 04/16/2014, order date for o1 */
    CalendarDate d1;
    /** 04/17/2014, pick up date for o1 */
    CalendarDate d2;
    /** ID: 1, has paid, no items, total and discount used order is 0 */
    Order o1;

    /**
     * setUp for the tests
     */
    @Before
    public void setUp() {
        b1 = new Bakery();
        fullb = new Bakery();
        try {
            fullb.initializeBakeryItems(new File("bakeryItems.txt"));
            fullb.initializeBakeryOrders(new File("orders.txt"));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        bitem = new BakeryItem(1, "Apple", "Pie", 9.99);
        
        bitemPair = new ItemQuantityPair(bitem, 1);
        
        ArrayList<ItemQuantityPair> arrayBitem =
                new ArrayList<ItemQuantityPair>();
        arrayBitem.add(bitemPair);
        border = new Order(3, false, new CalendarDate(11, 11, 11),
                new CalendarDate(1, 1, 12), arrayBitem, 9.99, -1.00);
        
        c1 = new Customer(1, "nam", "33", "boston", "ma", 02115);
        d1 = new CalendarDate(4, 16, 14);
        d2 = new CalendarDate(4, 17, 14);
        o1 = new Order(1, true, d1, d2, null, 0.0, 0.0);
    }
    
    /*
     * Tests for Bakery Class
     */

    /**
     * testing method noCustomers
     */
    @Test
    public void testNoCustomers() {
        assertTrue("noCustomers:b1", b1.noCustomers());
        b1.addBakeryItem("A", "B", 0.01);
        assertTrue("noCustomers:b1", b1.noCustomers());
        b1.addCustomer("A", "B", "C", "D", 00000);
        assertFalse("noCustomers:b1", b1.noCustomers());
        assertFalse("noCustomers:b1", fullb.noCustomers());
    }

    /**
     * testing method initializeBakeryItems
     */
    @Test
    public void testInitializeBakeryItems() {
        assertTrue("initializeItems:b1", b1.noBakeryItems());
        assertFalse("initializeItems:b1", b1.containsBakeryItem(1));
        assertFalse("initializeItems:b1", b1.containsBakeryItem(2));
        assertFalse("initializeItems:b1", b1.containsBakeryItem(85));
        try {
            b1.initializeBakeryItems(new File("bakeryItems.txt"));
        }
        catch (FileNotFoundException e) {
            assertTrue("FileNotFound", false);
        }
        catch (IllegalArgumentException e) {
            assertTrue("BadFile", false);
        }

        assertFalse("initializeItems:b1", b1.noBakeryItems());
        assertTrue("initializeItems:b1", b1.containsBakeryItem(1));
        assertTrue("initializeItems:b1", b1.containsBakeryItem(2));
        assertTrue("initializeItems:b1", b1.containsBakeryItem(85));
        assertFalse("initializeItems:b1", b1.containsBakeryItem(86));
        assertTrue("initializeItems:b1",
                b1.getBakeryItem(2).getName().equals("Apple"));
        assertTrue("initializeItems:b1",
                b1.getBakeryItem(3).getName().equals("Apple Crumb"));
        assertTrue("initializeItems:b1", b1.getBakeryItem(2).getCategory()
                .equals("Pies"));
        assertTrue("initializeItems:b1", b1.getBakeryItem(21).getCategory()
                .equals("Cakes"));
        assertSame("initializeItems:b1",
                new Double(b1.getBakeryItem(2).getPrice()).compareTo(16.0), 0);
        assertSame("initializeItems:b1",
                new Double(b1.getBakeryItem(3).getPrice()).compareTo(17.0), 0);
        assertSame("initializeItems:b1",
                new Double(b1.getBakeryItem(14).getPrice()).compareTo(2.5), 0);
    }
    
    /**
     * testing method badFileError
     */
    @Test
    public void testBadFileError() {
        try {
            Bakery.badFileError();
            assertTrue("badFileError", false);
        }
        catch (IllegalArgumentException e) {
            assertTrue("badFileError", true);
        }
    }
    
    /**
     * testing method initializeBakeryOrders
     */
    @Test
    public void testInitializeBakeryOrders() {
        try {
            b1.initializeBakeryItems(new File("bakeryItems.txt"));
            b1.initializeBakeryOrders(new File("orders.txt"));
            assertTrue("intializeOrders", true);
        }
        catch (Exception e) {
            assertTrue("intializeOrders", false);
        }
        assertTrue("intializeOrders:b1", 
                b1.getCustomerByID(56).getLastName().equals("Robinson"));
        assertTrue("intializeOrders:b1", 
                b1.getCustomerByID(56).getAddress().equals("634 Oak Ave."));
        assertTrue("intializeOrders:b1", 
                b1.getCustomerByID(56).getCity().equals("Raleigh"));
        assertTrue("intializeOrders:b1", 
                b1.getCustomerByID(56).getState().equals("NC"));
        assertSame("intializeOrders:b1", 
                new Integer(b1.getCustomerByID(
                        56).getZipCode()).compareTo(27612), 0);
        assertTrue("intializeOrders:b1", 
                b1.getCustomerByID(33).getLastName().equals("Bittiman"));
        assertTrue("intializeOrders:b1", 
                b1.getCustomerByID(22).getLastName().equals("Williams"));
        
        //Orders
        assertTrue("initializeOrders:b1", b1.getOrderByID(101).getPaid());
        assertSame("initializeOrders:b1", 
                new Double(
                        b1.getOrderByID(
                                101).getPriceBeforeDiscount()).compareTo(
                                        17.0), 0);
    }
    
    /**
     * testing scanner methods
     */
    @Test
    public void testScannerMethods() {
        Scanner testScan = new Scanner("String 1 1.1");
        try {
            Bakery.errorIfNoNext(testScan);
            assertTrue("errorIfNoNext", true);
        }
        catch (IllegalArgumentException e) {
            assertTrue("errorIfNoNext", false);
        }
        testScan.next();
        try {
            Bakery.errorIfNoNextInt(testScan);
            assertTrue("errorIfNoNext", true);
        }
        catch (IllegalArgumentException e) {
            assertTrue("errorIfNoNext", false);
        }
        testScan.nextInt();
        try {
            Bakery.errorIfNoNextDouble(testScan);
            assertTrue("errorIfNoNext", true);
        }
        catch (IllegalArgumentException e) {
            assertTrue("errorIfNoNext", false);
        }
        
        Scanner testScan2 = new Scanner("String s 1.1");
        try {
            Bakery.errorIfNoNextInt(testScan2);
            assertTrue("errorIfNoNext", false);
        }
        catch (IllegalArgumentException e) {
            assertTrue("errorIfNoNext", true);
        }
        testScan2.next();
        try {
            Bakery.errorIfNoNextDouble(testScan2);
            assertTrue("errorIfNoNext", false);
        }
        catch (IllegalArgumentException e) {
            assertTrue("errorIfNoNext", true);
        }
        testScan2.next();
        testScan2.nextDouble();
        try {
            Bakery.errorIfNoNextDouble(testScan2);
            assertTrue("errorIfNoNext", false);
        }
        catch (IllegalArgumentException e) {
            assertTrue("errorIfNoNext", true);
        }
    }
    
    /**
     * testing addCustomerMethods
     */
    @Test
    public void testAddCustomer() {
        assertTrue("addCustomer:b1", b1.noCustomers());
        assertFalse("addCustomer:b1", b1.hasCustomerWithName("Blossom"));
        b1.addCustomerWithID(0, "Blossom", "1 Street", "Townsville", "WA", 
                12345);
        assertTrue("addCustomer:b1", b1.hasCustomerWithName("Blossom"));
        assertFalse("addCustomer:b1", b1.noCustomers());
        assertTrue("addCustomer:b1", 
                b1.getCustomerByID(0).getLastName().equals("Blossom"));
        assertTrue("addCustomer:b1", 
                b1.getCustomerByID(0).getAddress().equals("1 Street"));
        assertSame("addCustomer:b1", 
                Double.compare(b1.getCustomerByID(0).getAvailableDiscount(), 
                        0.0), 0);
        
        b1.addCustomer("Buttercup", "2 Street", "Townsville", "NY", 54321);
        assertTrue("addCustomer:b1", 
                b1.getCustomerByName(
                        "Buttercup").getLastName().equals("Buttercup"));
        assertTrue("addCustomer:b1", 
                b1.getCustomerByName("Buttercup").getState().equals("NY"));
    }
    
    /**
     * testing methods for generating IDs
     */
    @Test
    public void testGenerateID() {
        assertFalse("generateID:fullb", 
                fullb.containsBakeryItem(fullb.generateBakeryID()));
        assertFalse("generateID:fullb", 
                fullb.containsOrder(fullb.generateOrderID()));
        assertFalse("generateID:fullb", 
                fullb.knownCustomer(fullb.generateCustomerID()));
    }
    
    /**
     * testing adding bakery items methods
     */
    @Test
    public void testAddBakeryItem() {
        assertTrue("addItem:b1", b1.noBakeryItems());
        int newID = b1.addBakeryItem("NumNums", "Cat", 19.95);
        assertTrue("addItem:b1", b1.containsBakeryItem(newID));
        assertFalse("addItem:b1", b1.noBakeryItems());
        assertTrue("addItem:b1", 
                b1.getBakeryItem(newID).getName().equals("NumNums"));
        assertTrue("addItem:b1", 
                b1.getBakeryItem(newID).getCategory().equals("Cat"));
        assertSame("addItem:b1", 
                Double.compare(b1.getBakeryItem(newID).getPrice(), 19.95), 0);
    }
    
    /**
     * testing methods for iterators in Bakery
     */
    @Test
    public void testIterators() {
        //Iterate customers
        Iterator<Customer> iterCust = b1.iterateCustomers().iterator();
        assertFalse("iterator:b1", iterCust.hasNext());
        b1.addCustomerWithID(0, "Blossom", "1 Street", "Townsville", "WA", 
                12345);
        Iterator<Customer> iterCustFull = b1.iterateCustomers().iterator();
        assertTrue("iterator:b1", iterCustFull.hasNext());
        //Iterate bakery items
        Iterator<BakeryItem> iterItem = b1.iterateBakeryItems().iterator();
        assertFalse("iterator:b1", iterItem.hasNext());
        b1.addBakeryItem("BLAH", "FOO", 0.01);
        Iterator<BakeryItem> iterItemFull = b1.iterateBakeryItems().iterator();
        assertTrue("iterator:b1", iterItemFull.hasNext());
    }
    
    /**
     * testing method customerWhoOrdered
     */
    @Test
    public void testCustomerWhoOrdered() {
        assertTrue("customerWhoOrdered:fullb", 
                fullb.customerWhoOrdered(117).getLastName().equals(
                        "Williams"));
        assertTrue("customerWhoOrdered:fullb", 
                fullb.customerWhoOrdered(115).getLastName().equals("Beckham"));
        assertTrue("customerWhoOrdered:fullb", 
                fullb.customerWhoOrdered(120).getLastName().equals("George"));
        assertTrue("customerWhoOrdered:fullb", 
                fullb.customerWhoOrdered(102).getLastName().equals(
                        "Bittiman"));
    }
    
    /*
     * BakeryItem Tests
     */
    
    /**
     * testing update methods
     */
    @Test
    public void testUpdateItem() {
        assertSame("updateItem", bitem.getID(), 1);
        assertTrue("updateItem", bitem.getName().equals("Apple"));
        assertTrue("updateItem", bitem.getCategory().equals("Pie"));
        assertSame("updateItem", Double.compare(bitem.getPrice(), 9.99), 0);
        bitem.updateID(2);
        bitem.updateName("Potato");
        bitem.updateCategory("Vegetable");
        bitem.updatePrice(0.01);
        assertSame("updateItem", bitem.getID(), 2);
        assertTrue("updateItem", bitem.getName().equals("Potato"));
        assertTrue("updateItem", bitem.getCategory().equals("Vegetable"));
        assertSame("updateItem", Double.compare(bitem.getPrice(), 0.01), 0);
    }
    
    /**
     * testing priceToSting
     */
    @Test
    public void testPriceToString() {
        assertTrue("priceToString", bitem.priceToString().equals("$9.99"));
    }
    
    /*
     * testing the Order class
     */
    
    /**
     * testing update methods for Order
     */
    @Test
    public void testUpdateOrder() {
        /** Order ID: 3, Not Paid, 11/11/11, 1/1/12, 
         * Ordering bitemx1, $9.99, -$1.00 
         * */
        //Checking the order
        assertSame("updateOrder", border.getID(), 3);
        assertFalse("updateOrder", border.getPaid());
        assertTrue("updateOrder",
                border.getOrderDateString().equals("11/11/11"));
        assertTrue("updateOrder",
                border.getPickUpDateString().equals("1/1/12"));
        assertTrue("updateOrder",
                border.getItemList().get(0).getItem().getName().equals(
                        "Apple"));
        assertSame("updateOrder",
                border.getItemList().get(0).getQuantity(), 1);
        assertSame("updateOrder",
                Double.compare(border.getPriceBeforeDiscount(), 9.99), 0);
        assertSame("updateOrder",
                Double.compare(border.getDiscountUsed(), -1.00), 0);
        
        //Updating the order and checking
        border.updateID(5);
        border.updatePaid(true);
        border.updatePickUpDate(new CalendarDate(1, 1, 14));
        border.updateOrderedDate(new CalendarDate(1, 1, 13));
        border.updatePrice(99.99);
        border.updateDiscount(-99.99);
        
        assertSame("updateOrder", border.getID(), 5);
        assertTrue("updateOrder", border.getPaid());
        assertTrue("updateOrder",
                border.getOrderDateString().equals("1/1/13"));
        assertTrue("updateOrder",
                border.getPickUpDateString().equals("1/1/14"));
        assertTrue("updateOrder",
                border.getItemList().get(0).getItem().getName().equals(
                        "Apple"));
        assertSame("updateOrder",
                Double.compare(border.getPriceBeforeDiscount(), 99.99), 0);
        assertSame("updateOrder",
                Double.compare(border.getDiscountUsed(), -99.99), 0);
    }
    
    /**
     * testing some small methods from Order
     */
    @Test
    public void testPriceAfterDiscount() {
        assertSame("calcTotalDue", 
                Double.compare(border.calcTotalDue(), 8.99), 0);
        assertTrue("getOrderDateString", 
                border.getOrderDateString().equals("11/11/11"));
        assertTrue("getHasPaidString",
                border.getHasPaidString().equals("No"));
        assertTrue("getPickUpDateString",
                border.getPickUpDateString().equals("1/1/12"));
        assertTrue("containsBakeryItem",
                border.containsBakeryItem(1));
        
        border.updatePrice(999.99);
        assertSame("calcTotalDue", 
                Double.compare(border.calcTotalDue(), 998.99), 0);
        border.fixPrice();
        assertSame("calcTotalDue", 
                Double.compare(border.calcTotalDue(), 8.99), 0);
    }
    
    /*
     * ItemQuantityPair testing
     */
    
    /**
     * testing update methods along with some various helper methods
     */
    @Test
    public void testUpdateQuantityPair() {
        assertSame("updatePair", bitemPair.getItem().getID(), 1);
        assertSame("updatePair", bitemPair.getQuantity(), 1);
        ArrayList<ItemQuantityPair> pairArray = 
                new ArrayList<ItemQuantityPair>();
        pairArray.add(bitemPair);
        assertSame("updatePair", 
                Double.compare(ItemQuantityPair.calculatePrice(pairArray),
                        9.99), 0);
        assertTrue("priceToString", bitemPair.priceToString().equals("$9.99"));
        
        BakeryItem newItem = new BakeryItem(5, "Bob", "Foo", 6.00);
        bitemPair.updateItem(newItem);
        bitemPair.updateQuantity(2);
        assertSame("updatePair", bitemPair.getItem().getID(), 5);
        assertSame("updatePair", bitemPair.getQuantity(), 2);
        assertSame("updatePair", 
                Double.compare(ItemQuantityPair.calculatePrice(pairArray),
                        12.00), 0);
        assertTrue("priceToString", bitemPair.priceToString().equals("$12.00"));
    }
    

    /**
     * Tests for customer class
     */
    
    /**
     * Tests updateID and getID in customer class
     */
    @Test
    public void testUpdateAndGetID() {
        c1.updateID(2);
        assertSame("test update and get id", c1.getID(), 2);
    }
    
    /**
     * Tests updateName and getName in customer class
     */
    @Test
    public void testUpdateAndGetName() {
        c1.updateLastName("luu");
        assertTrue("test update and get last name",
                c1.getLastName().equals("luu"));
    }
    
    /**
     * Tests updateAdress and getAddress in customer class
     */
    @Test
    public void testUpdateAndGetAddress() {
        c1.updateAddress("new york");
        assertTrue("test update and get address",
                c1.getAddress().equals("new york"));
    }
    
    /**
     * Tests updateZipCode and getZipCode in Customer class
     */
    @Test
    public void testUpdateAndGetZipCode() {
        c1.updateZipCode(21210);
        String stringZip = c1.getZipCode() + "";
        assertTrue("test update and get zip code",
                stringZip.equals("21210"));
    }
    
    /**
     * Tests updateState and getState in Customer class
     */
    @Test
    public void testUpdateAndGetState() {
        c1.updateState("mo");
        assertTrue("test update and get state",
                c1.getState().equals("mo"));
    }
    
    /**
     * Tests updateAvailableDiscount and getAvailableDiscount
     */
    @Test
    public void testUpdateAndGetAvailableDiscount() {
        assertSame("test available discount", 
                Double.compare(c1.getAvailableDiscount(), 0.0), 0);
        c1.updateAvailableDiscount(0.2);
        assertSame("test update available discount",
                Double.compare(c1.getAvailableDiscount(), 0.2), 0);
    }
    
    /**
     * Tests updateCurrentLoyalty and getCurrentLoyalty
     */
    @Test
    public void testUpdateAndGetCurrentLoyalty() {
        assertSame("test current loyalty",
                Double.compare(c1.getCurrentLoyalty(), 0.0), 0);
        c1.updateCurrentLoyalty(0.2);
        assertSame("test update current loyalty",
                Double.compare(c1.getCurrentLoyalty(), 0.2), 0);
        
    }
    
    /**
     * Tests updateOrder and getOrder
     */
    @Test
    public void testUpdateAndGetOrder() {
        assertTrue("test noOrder", c1.noOrders());
        c1.addOrder(o1);
        assertFalse("test noOrder", c1.noOrders());
        ArrayList<Order> temp = new ArrayList<Order>();
        temp.add(o1);
        assertTrue("test getOrder", c1.getOrders().equals(temp));
        assertTrue("test iterateOrder", c1.iterateOrders().equals(temp));
    }
    
    /**
     * Tests newOrderLoyalty
     */
    @Test
    public void testNewOrderLoyalty() {
        assertSame("test newOrderLoyalty", 
                Double.compare(c1.getAvailableDiscount(), 0.0), 0);
        assertSame("test newOrderLoyalty", 
                Double.compare(c1.getCurrentLoyalty(), 0.0), 0);
        c1.newOrderLoyalty(100);
        assertSame("test newOrderLoyalty", 
                Double.compare(c1.getCurrentLoyalty(), 0.0), 0);
        assertSame("test newOrderLoyalty", 
                Double.compare(c1.getAvailableDiscount(), 10.0), 0);
    }
    
    /**
     * Tests getOrderWithID
     */
    @Test
    public void testGetOrderWithID() {
        c1.addOrder(o1);
        assertTrue("test getOrderWithID", c1.getOrderWithID(1).equals(o1));
        try {
            c1.getOrderWithID(2);
        }
        catch (RuntimeException e) {
            assertTrue("test getOrderWithID", true);
        }
        catch (Exception e) {
            assertTrue("test getOrderWithID", false);
        }
    }
    
    /**
     * Tests hasOrderWithID
     */
    @Test
    public void testHasOrderWithID() {
        c1.addOrder(o1);
        assertTrue("test hasOrderWithID", c1.hasOrderWithID(1));
        assertSame("test hasOrderWithID", c1.getOrders().size(), 1);
    }
    
    /**
     * Test cases for CalendarDate class
     */
    
    /**
     * Tests initializing calendar date
     */
    @Test
    public void testInitializingCalendarDate() {
        try {
            CalendarDate temp1 = new CalendarDate(04, 31, 14);
            temp1.getDay();
        }
        catch (IllegalArgumentException e) {
            assertTrue("test initializing calendarDate", true);
        }
        catch (Exception e) {
            assertTrue("test initializing calendarDate", false);
        }
        
        try {
            CalendarDate temp2 = new CalendarDate(03, 32, 14);
            temp2.getDay();
        }
        catch (IllegalArgumentException e) {
            assertTrue("test initializing calendarDate", true);
        }
        catch (Exception e) {
            assertTrue("test initializing calendarDate", false);
        }
        
        try {
            CalendarDate temp3 = new CalendarDate(02, 29, 2014);
            temp3.getDay();
        }
        catch (IllegalArgumentException e) {
            assertTrue("test initializing calendarDate", true);
        }
        catch (Exception e) {
            assertTrue("test initializing calendarDate", false);
        }
        
        try {
            CalendarDate temp4 = new CalendarDate(13, 04, 14);
            temp4.getDay();
        }
        catch (IllegalArgumentException e) {
            assertTrue("test initializing calendarDate", true);
        }
        catch (Exception e) {
            assertTrue("test initializing calendarDate", false);
        }
        
        try {
            CalendarDate temp5 = new CalendarDate("02/29/14");
            temp5.getDay();
        }
        catch (IllegalArgumentException e) {
            assertTrue("test initialining calendarDate", true);
        }
        catch (Exception e) {
            assertTrue("test initializing calendarDatefalse", false);
        }
        
        try {
            CalendarDate temp5 = new CalendarDate("whatever");
            temp5.getDay();
        }
        catch (IllegalArgumentException e) {
            assertTrue("test initialining calendarDate", true);
        }
        catch (Exception e) {
            assertTrue("test initializing calendarDatefalse", false);
        }
        
        try {
            CalendarDate temp6 = new CalendarDate(-1, -2, -3);
            temp6.getDay();
        }
        catch (IllegalArgumentException e) {
            assertTrue("test initialining calendarDate", true);
        }
        catch (Exception e) {
            assertTrue("test initializing calendarDatefalse", false);
        }
        
        try {
            CalendarDate temp7 = new CalendarDate(01, 31, 14);
            CalendarDate temp8 = new CalendarDate(02, 29, 12);
            CalendarDate temp9 = new CalendarDate(04, 30, 14);
            temp7.getDay();
            temp8.getDay();
            temp9.getDay();
            assertTrue("test initializing calendarDate", true);
        }
        catch (Exception e) {
            assertTrue("test initializing calendarDate", false);
        }
    }
    
    /**
     * Tests toString method
     */
    @Test
    public void testToString() {
        assertTrue("test toString for calendarDate",
                d1.toString().equals("4/16/14"));
    }
    
    /**
     * Tests getDate, getMonth and getYears
     */
    @Test
    public void testGetDateMonthYear() {
        assertSame(d1.getMonth(), 4);
        assertSame(d1.getDay(), 16);
        assertSame(d1.getYear(), 14);
    }
}
