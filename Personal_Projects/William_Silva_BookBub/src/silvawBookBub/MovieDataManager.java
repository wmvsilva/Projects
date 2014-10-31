package silvawBookBub;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class represents a module to manage the movie data after it is parsed.
 * 
 * @author William Silva
 * @version 25 October 2014
 *
 */
class MovieDataManager {

	/** an arraylist containing all of the parsed movies */
	private ArrayList<MovieInfo> movieList;

	/**
	 * Constructor for MovieDataManager
	 * @param m the list of movie information this will hold
	 */
	MovieDataManager(ArrayList<MovieInfo> m) {
		movieList = m;
	}

	/**
	 * Sorts all the movies in this movieList by imdbRating, descending.
	 * If a movie has an error, it will be at the end.
	 * Any movies with unavailable ratings will be right before any error movies.
	 */
	void sortMoviesByRating() {
		Collections.sort(movieList, new MovieRatingComp());
	}

	/**
	 * Prints all movies, line after line, in the format "<title> -- <imbdRating>"
	 */
	void printMoviesRatings() {
		for (int i = 0; i < movieList.size(); i++) {
			movieList.get(i).printTitleRating();
		}
	}
}
