package silvawBookBub;

import com.google.gson.JsonObject;

/**
 * Class holds information about a single movie such as title, year, and the Json data from OMDb API
 * 
 * @author William Silva
 * @version 25 October 2014
 *
 */
class MovieInfo {
	
	/** the title of the movie */
	private String title;
	
	/** the year that the movie was released */
	private String year;
	
	/** the Json data looked up from the OMDb API */
	JsonObject imbdInfo;
	
	/** boolean stating if there is a problem with the Json data */
	boolean error = false;
	
	/** the type of problem that this movie Json data has, else null */
	String errorMsg = null;
	
	/**
	 * Constructor for MovieInfo.
	 * Note that Json data is initialized later.
	 * 
	 * @param t the title of the movie
	 * @param y the year that the movie was released. "" represents no year available.
	 */
	MovieInfo(String t, String y) {
		title = t;
		year = y;
	}
	
	/**
	 * Accessor for the title field of this
	 * @return the movie title of this
	 */
	String getTitle() {
		return title;
	}
	
	/**
	 * Accessor for the year field of this
	 * @return the year that this was released
	 */
	String getYear() {
		return year;
	}

	/**
	 * Is the year this was released available?
	 * @return boolean stating if the year this was released is available
	 */
	boolean hasYear() {
		return !(year.equals(""));
	}
	
	/**
	 * Adds the Json data to this from the OMDb API
	 * @param the Json data for this movie retrieved from teh OMDb API
	 */
	void addJson(JsonObject jo) {
		imbdInfo = jo;
	}
	
	/**
	 * Changes this to an error state and writes down what went wrong
	 * @param s message describing what went wrong
	 */
	void reportError(String s) {
		error = true;
		errorMsg = s;
	}
	
	/**
	 * prints the title and year of this to console
	 */
	void printInfo() {
		System.out.println(title + ", " + year);
	}
	
	/**
	 * if no error, prints the "<title> -- <imdb rating>"
	 * if there is an error, prints "<title> -- <error message>"
	 */
	void printTitleRating() {
		if (!error) {
			System.out.println(title + " -- " + accessJsonField("imdbRating"));
		}
		else {
			System.out.println(title + " -- " + errorMsg);
		}
	}
	
	/**
	 * Looks through the Json data of this and retrieves the string at the specified field
	 * 
	 * @param field the field of the Json data that we will return
	 * @return the value at the field we specify
	 * @throws RuntimeException error when the Json is accessed when this is in an error state
	 */
	String accessJsonField(String field) throws RuntimeException{
		if (!error) {
		return imbdInfo.get(field).getAsString();
		}
		else {
			throw new RuntimeException("Cannot access field of this malformed MovieInfo");
		}
	}
	
	/**
	 * Is this in an error state? Aka, should we not access the Json data
	 * @return boolean stating if this is in an error state
	 */
	boolean hasError() {
		return error;
	}
}
