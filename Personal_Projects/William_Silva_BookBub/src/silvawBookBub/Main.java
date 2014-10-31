package silvawBookBub;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Main class which contains main method that reads in a file named "movies.csv" and outputs the titles
 * of each movie sorted by their IMDB rating, descending.
 * 
 * @author William Silva
 * @version 25 October 2014
 */
public class Main {
	
	/**
	 * Main method which reads in a file named "movies.csv" and outputs the titles
	 * of each movie sorted by their IMDB rating, descending.
	 * @param args command line arguments that are not used
	 */
	public static void main(String args[]) {
		
		String filename = "movies.csv";
		
		// Confirm that we can access the website
		if (!OMBdMangager.websiteAccessible()) {
			System.out.println("Sorry, we cannot perform the action you requested.");
			System.out.println("OMDb may be offline, or something may be wrong with your internet.");
			return;
		}
		
		// Produce output of titles sorted by IMDB rating, descending
		try {
			ArrayList<MovieInfo> movies  = InputManager.parseCSVList(filename);
			OMBdMangager.addJSONData(movies);
			MovieDataManager movieData = new MovieDataManager(movies);
			movieData.sortMoviesByRating();
			movieData.printMoviesRatings();
		} catch (FileNotFoundException e) {
			System.out.println("Sorry, we cannot perform the action you requested.");
			System.out.println("The input file " + filename + " was not found.");
		}
	}

}
