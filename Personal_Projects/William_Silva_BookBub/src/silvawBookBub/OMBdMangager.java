package silvawBookBub;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Class manages interaction with the OMBd online API.
 * 
 * @author William Silva
 * @version 25 October 2014
 *
 */
abstract class OMBdMangager {

	/**
	 * For all MovieInfo in given movies, attempts to lookup the Json data for them on the OMBd API.
	 * If the Json is unaccessible, marks down on the MovieInfo that an error occured.
	 * @param movies an arraylist of MovieInfo which have their titles and years initialized
	 */
	static void addJSONData(ArrayList<MovieInfo> movies) {
		BufferedReader reader;
		JsonParser parser = new JsonParser();
		URL url;
		JsonObject o;

		for (MovieInfo mi : movies) {
			try {
				url = new URL(createURL(mi));
				reader = new BufferedReader(new InputStreamReader(url.openStream()));
				String jsonString = reader.readLine();
				if (jsonString.equals("{\"Response\":\"False\",\"Error\":\"Movie not found!\"}")) {
					mi.reportError("Movie not found");
				}
				else {
					o = parser.parse(jsonString).getAsJsonObject();
					mi.addJson(o);
				}
			}
			catch (MalformedURLException e) {
				mi.reportError("Malformed URL");
			}
			catch (IOException e) {
				mi.reportError("Could not open OMDb url");
			}
		}
	}

	/**
	 * Constructs a URL which searches the OMDb API for the given movie's title and year
	 * 
	 * @param mi movieInfo to construct the corresponding OMDb URL for based on title and year
	 * @return a string URL that searches the OMDb API for the Json data of the given movie
	 */
	static String createURL(MovieInfo mi) {
		String title = mi.getTitle();
		title = title.replaceAll(" ", "%20");
		String result = "http://www.omdbapi.com/?t=" + title;
		if (mi.hasYear()) {
			result = result + "&y=" + mi.getYear();
		}

		return result;
	}

	/**
	 * Is the OMBd API website accessible?
	 * @return boolean stating if the omdbapi.com website can be opened
	 */
	static boolean websiteAccessible() {
		BufferedReader reader;
		try {
			URL url = new URL("http://www.omdbapi.com");
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			reader.close();
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
}
