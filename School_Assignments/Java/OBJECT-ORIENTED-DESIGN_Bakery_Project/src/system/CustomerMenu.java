package system;

/**
 * Class represents a user interface for performing actions on the customers of
 * the bakery database.
 * 
 * @author William Silva, silva.w@husky.neu.edu
 * @version 18 June 2014
 * 
 */
class CustomerMenu {

    /**
     * Method displays the menu for customer options and asks the user to pick
     * an action to perform.
     */
    static void customerMenu() {
        Main.resetScanner();
        Util.println("Customer Menu- Please select an option.");
        Util.println("1. Add customer");
        Util.println("2. View/Update customer information");
        Util.println("3. Main Menu");

        if (!(Main.scan.hasNextInt())) {
            Util.println("Invalid input! Please enter in either 1, 2, or 3.");
            customerMenu();
        }

        int input = Main.scan.nextInt();

        switch (input)
        {
            case 1:
                addCustomerOption();
                break;

            case 2:
                viewCustomersOption();
                break;

            case 3:
                Main.mainMenu();
                break;

            default:
                Util.println("Invalid input! Please enter in either "
                        + "1, 2, or 3.");
                customerMenu();
                break;
        }
    }

    /**
     * Method asks the user for information about a customer to add and adds the
     * new customer to the bakery database.
     */
    static void addCustomerOption() {
        int newID = addCustomerOptionHelper();

        Util.println("Customer added with ID: " + newID + "!");
        customerMenu();
    }

    /**
     * Method prompts the user for information regarding a customer to be added
     * and adds that user to the database. The ID that is generated
     * automatically for that user is returned.
     * 
     * @return the ID of the customer added to the bakery database.
     */
    static int addCustomerOptionHelper() {
        Main.resetScanner();
        Util.println("Now adding a new customer!");
        Util.println("Please enter the customer's last name: ");
        String lastName = Main.scan.next();

        Main.resetScanner();
        Util.println("Please enter the customer's address: ");
        String address = Main.scan.nextLine();

        Main.resetScanner();
        Util.println("Please enter the customer's city: ");
        String city = Main.scan.nextLine();

        Main.resetScanner();
        Util.println("Please enter the customer's state: ");
        String state = Main.scan.nextLine();

        Main.resetScanner();
        Util.println("Please enter the customer's zip code: ");
        if (!(Main.scan.hasNextInt())) {
            Util.println("Zipcode must be a number! Please reenter the info.");
            addCustomerOption();
        }
        int zipCode = Main.scan.nextInt();

        return Main.bakery.addCustomer(lastName, address, city, 
                state, zipCode);
    }

    /**
     * Method asks the user how they would like to find a customer to view and
     * then displays the customer they chose.
     */
    static void viewCustomersOption() {
        if (Main.bakery.noCustomers()) {
            Util.println("No customers to view!");
            customerMenu();
        }
        Customer chosenCustomer = pickACustomer();

        viewUpdateOption(chosenCustomer);
    }

    /**
     * Method has the user pick a customer from the bakery database by picking
     * from a list of all customers or searching by last name.
     * 
     * @return the customer that the user has chosen
     */
    static Customer pickACustomer() {
        Main.resetScanner();
        Util.println("Would you like to view a list of all customers "
                + "or search for" + " a specific customer?");
        Util.println("1. View all customers");
        Util.println("2. Search for a customer");

        if (!(Main.scan.hasNextInt())) {
            Util.println("Invalid input! Please enter in either 1 or 2.");
            InitializeMenu.initializeMenu();
        }

        int input = Main.scan.nextInt();

        switch (input)
        {
            case 1:
                return viewAllCustomersAndUserSelect();

            case 2:
                Main.resetScanner();
                Util.println("Please input a customer's last name to "
                        + "search for.");
                String lastName = Main.scan.next();
                if (Main.bakery.hasCustomerWithName(lastName)) {
                    return Main.bakery.getCustomerByName(lastName);
                }
                else {
                    Util.println("No customer with that name found.");
                    return pickACustomer();
                }

            default:
                Util.println("Invalid input! Please enter in either 1 or 2.");
                return pickACustomer();
        }
    }

    /**
     * Method displays all of the customers and asks a user to chose one by
     * customer ID. That customer is then displayed and can be updated.
     * 
     * @return the customer that the user selected
     */
    static Customer viewAllCustomersAndUserSelect() {
        Main.resetScanner();
        Main.bakery.displayAllCustomers();
        Util.println("Please select a customer ID from the list:");
        if (!(Main.scan.hasNextInt())) {
            Util.println("Input invalid! Not a valid! Please try again.");
            return viewAllCustomersAndUserSelect();
        }

        int id = Main.scan.nextInt();

        if (Main.bakery.knownCustomer(id)) {
            return Main.bakery.getCustomerByID(id);
        }
        else {
            Util.println("Customer ID not found! Please try again.");
            return viewAllCustomersAndUserSelect();
        }
    }

    /**
     * Method allows the given customer's information to be viewed and updated
     * by the user.
     * 
     * @param customer
     *            the customer to be updated.
     */
    static void viewUpdateOption(Customer customer) {
        Main.resetScanner();
        Util.println("Viewing customer-");
        customer.displayCustomer();
        Util.println("Would you like to-");
        Util.println("1. Update customer information.");
        Util.println("2. View another customer.");
        Util.println("3. Go back to Customer Menu");
        if (!(Main.scan.hasNextInt())) {
            Util.println("Invalid input! Please enter 1 or 2.");
            viewUpdateOption(customer);
        }

        int input = Main.scan.nextInt();

        switch (input)
        {
            case 1:
                updateCustomerOption(customer);
                break;

            case 2:
                viewCustomersOption();
                break;

            case 3:
                customerMenu();
                break;

            default:
                Util.println("Invalid input! Please enter 1 or 2");
                viewUpdateOption(customer);
                break;
        }
    }

    /**
     * Method that updates one field of the given customer's information in the
     * database.
     * 
     * @param customer
     *            the customer to be updated.
     */
    static void updateCustomerOption(Customer customer) {
        Main.resetScanner();
        Util.println("What would you like to update?");
        Util.println("1. ID");
        Util.println("2. Last Name");
        Util.println("3. Address");
        Util.println("4. City");
        Util.println("5. State");
        Util.println("6. Zip Code");
        Util.println("7. Available Discount");
        Util.println("8. Current Loyalty");
        Util.println("9. Nothing. Go back to Customer Menu");

        if (!(Main.scan.hasNextInt())) {
            Util.println("Invalid Input! Please re-enter!");
            updateCustomerOption(customer);
        }

        int input = Main.scan.nextInt();

        Main.resetScanner();
        switch (input)
        {
            case 1:
                Util.println("Please enter the new ID:");
                if (Main.scan.hasNextInt()) {
                    customer.updateID(Main.scan.nextInt());
                }
                else {
                    Util.println("Invalid Input! Please try again.");
                    updateCustomerOption(customer);
                }
                break;
            case 2:
                Util.println("Please enter the new last name:");
                customer.updateLastName(Main.scan.nextLine());
                break;

            case 3:
                Util.println("Please enter the new address:");
                customer.updateAddress(Main.scan.nextLine());
                break;

            case 4:
                Util.println("Please enter the new city:");
                customer.updateCity(Main.scan.next());
                break;

            case 5:
                Util.println("Please enter the new state:");
                customer.updateState(Main.scan.next());
                break;

            case 6:
                Util.println("Please enter the new zip code:");
                if (Main.scan.hasNextInt()) {
                    customer.updateZipCode(Main.scan.nextInt());
                }
                else {
                    Util.println("Invalid input! Please try again.");
                    updateCustomerOption(customer);
                }
                break;

            case 7:
                Util.println("Please enter the new available discount:");
                if (Main.scan.hasNextDouble()) {
                    customer.updateAvailableDiscount(Main.scan.nextDouble());
                }
                else {
                    Util.println("Invalid input! Please try again.");
                    updateCustomerOption(customer);
                }
                break;

            case 8:
                Util.println("Please enter the new current loyalty:");
                if (Main.scan.hasNextDouble()) {
                    customer.updateCurrentLoyalty(Main.scan.nextDouble());
                }
                else {
                    Util.println("Invalid input! Please try again.");
                    updateCustomerOption(customer);
                }
                break;

            case 9:
                customerMenu();
                break;

            default:
                Util.println("Invalid input! Please try again!");
                updateCustomerOption(customer);
        }

        Util.println("Customer information updated!");
        customerMenu();
    }

}
