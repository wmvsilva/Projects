package system;

/**
 * Class contains utility functions for printing to console. They were created
 * as an abstraction of some console printing statements to prevent code
 * duplication.
 * 
 * @author William Silva, silva.w@husky.neu.edu
 * @version 17 June 2014
 * 
 */
class Util {

    /**
     * Method is a shorthand for System.out.println. It prints out messages to
     * console.
     * 
     * @param s
     *            the string to print to console.
     */
    static void println(String s) {
        System.out.println(s);
    }

    /**
     * Method prints an error message to console that tells the user of invalid
     * input and asks them to please perform the action specified in the given
     * string s
     * 
     * @param s
     *            a description of an action the user should perform.
     */
    static void printerr(String s) {
        System.out.println("Invalid input! Please " + s);
    }

}
