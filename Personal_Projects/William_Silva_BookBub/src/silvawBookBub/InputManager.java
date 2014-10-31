package silvawBookBub;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Class represents a module for parsing input of movie information.
 * @author William Silva
 * @version 25 October 2014
 *
 */
abstract class InputManager {
	
	/**
	 * Turns a text-file list in the form of <title>, <year> on each line into an array list of MovieInfo
	 * containing the same information.
	 * 
	 * @param filename the filename of the file to parse
	 * @return an Array List of MovieInfo (which each contain the title-year pair from the parsed list)
	 * @throws FileNotFoundException error if the file with the given filename isn't found
	 */
	static ArrayList<MovieInfo> parseCSVList(String filename) 
			throws FileNotFoundException {
		Scanner input = new Scanner(new File(filename));
		ArrayList<MovieInfo> result = new ArrayList<MovieInfo>();

		while (input.hasNext()) {
			String pair = input.nextLine();
			String title = retrieveTitle(pair);
			String year = retrieveYear(pair);
			result.add(new MovieInfo(title, year));
		}
		
		input.close();
		return result;
	}
	
	/**
	 * Retrieves the title string from a line of parsed input in the form "<title>", <year> or in
	 * the form <title>, <pair>
	 * 
	 * @param textPair a line of parsed input in the form <title>, <pair> or "<title>", <pair>
	 * @return the <title> of the pair
	 */
	private static String retrieveTitle(String textPair) {
		String title;
		if (textPair.charAt(0) == '"') {
			Scanner scan = new Scanner(textPair.substring(1));
			scan.useDelimiter(Pattern.compile("\","));
			title = scan.next();
			scan.close();
		}
		else {
			Scanner scan = new Scanner(textPair);
			scan.useDelimiter(Pattern.compile(","));
			title = scan.next();
			scan.close();
		}
		
		return title;
	}
	
	/**
	 * Retrieves the title string from a line of parsed input in the form "<title>", <year> or in
	 * the form <title>, <year>
	 * 
	 * @param textPair a line of parsed input in the form <title>, <pair> or "<title>", <year>
	 * @return the <year> of the pair
	 */
	private static String retrieveYear(String textPair) {
		String year;
		if (textPair.charAt(0) == '"') {
			Scanner scan = new Scanner(textPair.substring(1));
			scan.useDelimiter(Pattern.compile("\","));
			scan.next();
			if (scan.hasNext()) {
				year = scan.next();
			}
			else {
				year = "";
			}
			scan.close();
		}
		else {
			Scanner scan = new Scanner(textPair);
			scan.useDelimiter(Pattern.compile(","));
			scan.next();
			if (scan.hasNext()) {
				year = scan.next();
			}
			else {
				year = "";
			}
			scan.close();
		}
		
		return year;
	}
	
	
}
