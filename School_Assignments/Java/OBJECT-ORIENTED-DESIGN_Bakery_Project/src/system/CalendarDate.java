package system;

import java.util.Scanner;
import java.util.Arrays;

/**
 * Represents a calendar date with a month, day, and year.
 * 
 * @author Nam Luu, luunhat.n@husky.neu.edu
 * @author William Silva, silva.w@husky.neu.edu
 * @version 16 June 2014
 */
class CalendarDate {

    /** All the months that have 31 days */
    private final static int[] ODD_MONTHS = { 1, 3, 5, 7, 8, 10, 12 };
    /** All the months that have 30 days */
    private final static int[] EVEN_MONTHS = { 4, 6, 9, 11 };
    /** Represents the month of this date */
    private int month;
    /** Represents the day of this date */
    private int day;
    /** Represents the year of this date */
    private int year;

    /**
     * Constructs a new date with the given month, day and year Throws an
     * exception in an invalid date is given.
     * 
     * @param month the month the the date to cteate
     * @param day the day of the date to create
     * @param year the year of the date to create in XX format
     */
    CalendarDate(int month, int day, int year) {
        if (!(checkForGoodDate(month, day, year))) {
            badDateError();
        }
        this.month = month;
        this.day = day;
        this.year = year;
    }

    /**
     * Given a string of the form (xx/xx/xx), convert to a date.
     * 
     * @param orderDateInput the string of the format (XX/XX/XX) to convert 
     * into a calendar date.
     */
    CalendarDate(String orderDateInput) throws IllegalArgumentException {
        Scanner date = new Scanner(orderDateInput);
        date.useDelimiter("/");
        if (!(date.hasNextInt())) {
            badDateError();
        }
        int m = date.nextInt();

        if (!(date.hasNextInt())) {
            badDateError();
        }
        int d = date.nextInt();

        if (!(date.hasNextInt())) {
            badDateError();
        }
        int y = date.nextInt();
        date.close();

        if (!(checkForGoodDate(m, d, y))) {
            badDateError();
        }

        this.month = m;
        this.day = d;
        this.year = y;
    }

    /**
     * Method determines if the date parameters given would make a valid date
     * 
     * @param month
     *            the month of the date
     * @param day
     *            the day of the date
     * @param year
     *            the year of the date in XX format (1-99)
     * @return boolean stating if the date given by the parameters is an actual
     *         valid date
     */
    static boolean checkForGoodDate(int month, int day, int year) {
        if (Arrays.binarySearch(ODD_MONTHS, month) >= 0) {
            if (day > 31) {
                return false;
            }
        }
        else if (Arrays.binarySearch(EVEN_MONTHS, month) >= 0) {
            if (day > 30) {
                return false;
            }
        }
        else if (month == 2) {
            if ((year % 4) == 0) {
                if (day > 29) {
                    return false;
                }
            }
            else {
                if (day > 28) {
                    return false;
                }
            }
        }
        else {
            return false;
        }
        String yearString = year + "";
        if (yearString.length() != 2) {
            return false;
        }

        return true;
    }

    /**
     * Method throws an exception appropriate for when parameters used for
     * making a date don't logically make sense.
     */
    static void badDateError() {
        throw new IllegalArgumentException("Bad date input!");
    }

    /**
     * Method determines if the given date is on the same month, day, and year
     * as this.
     * 
     * @param datePlaced
     *            the date to check for equality with
     * @return boolean stating if the given datePlaced is equal to this
     */
    boolean sameDate(CalendarDate datePlaced) {
        return (this.month == datePlaced.getMonth()
                && this.day == datePlaced.getDay() && this.year == datePlaced
                    .getYear());
    }

    /**
     * Gets the month of this date
     * 
     * @return the month of this date
     */
    int getMonth() {
        return this.month;
    }

    /**
     * Gets the day of this date
     * 
     * @return the day of this date
     */
    int getDay() {
        return this.day;
    }

    /**
     * Gets the year of this date
     * 
     * @return the year of this date
     */
    int getYear() {
        return this.year;
    }

    /**
     * Method returns a string representation of this date
     * 
     * @return a string representation of the date in the form x/x/xx
     */
    @Override
    public String toString() {
        String newYear = Integer.toString(year);
        if (year == 0.0) {
            newYear = "00";
        }
        return month + "/" + day + "/" + newYear;
    }
}
