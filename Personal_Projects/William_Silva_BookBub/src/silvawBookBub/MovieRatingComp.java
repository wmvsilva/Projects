package silvawBookBub;

import java.util.Comparator;
/**
 * Comparator to compare two MovieInfo's by their IMDB rating.
 * NOTE: This doesn't exactly follow the compator interface as the compare function returns a positive
 * value only if the second argument is larger and a negative value only if the first argument is larger.
 * This is to easily sort by descending order without reversing the list at the end
 * 
 * @author William Silva
 * @version 25 October 2014
 */
class MovieRatingComp implements Comparator<MovieInfo> {

	/** the Json field that we are using to compare Json fields */
	private final String FIELD = "imdbRating";

	/**
	 * Compare function that must be implemented as part of the Comparator interface.
	 * NOTE: Negative value is returned if mi1 is larger and positive value is returned if 
	 * mi2 is larger.
	 * If a MovieInfo has an error, it is less than everything.
	 * If a MovieInfo has "N/A" as a rating, it is less than those with ratings.
	 * 
	 * @param mi1 the MovieInfo to compare to mi2
	 * @param mi2 the MovieInfo to compare to mi1
	 * @return 0 if they are equal. Positive if mi2 > mi1. Negative if mi1 > mi2.
	 */
	public int compare(MovieInfo mi1, MovieInfo mi2) {
		if (mi1.hasError() || mi2.hasError()) {
			if (mi1.hasError() && mi2.hasError()) {
				return 0;
			}
			else if (mi1.hasError()) {
				return 1;
			}
			else
				return -1;
		}

		String f1 = mi1.accessJsonField(FIELD);
		String f2 = mi2.accessJsonField(FIELD);
		if (f1.equals("N/A") || f2.equals("N/A")) {
			if (f1.equals("N/A") && f2.equals("N/A")) {
				return 0;
			}
			else if (f1.equals("N/A")) {
				return 1;
			}
			else
				return -1;
		}

		double fint = Double.parseDouble(f1);
		double fint2 = Double.parseDouble(f2);
		return Double.compare(fint2, fint);
	}
}
