IMBD Movie Rating Sorter
===========================================================
This was an assignment as part of applying for a co-op.
It reads a list of movie titles in a specific format and outputs
those movies in order of IMBD rating, descending.

README for BoobBub IMDB Project
===========================================================
Run these command from the containing folder to perform the specified action.

Running Instructions:
-Java 1.5 or greater is required.

To run JAR file:
java -jar silvawBookBub.jar

For some, it may be more useful to output to a txt file:
java -jar silvawBookBub.jar > output.txt

===========================================================
Building Instructions (Windows):
-JDK 5.0 or greater is required

To compile: 
javac -cp silvawBookBub_lib/gson-2.3.jar -d ./bin src/silvawBookBub/*.java

To run your compiled files:
java -cp silvawBookBub_lib/gson-2.3.jar;bin silvawBookBub.Main

===========================================================
Program Summary

This program reads a movies.csv file in the same directory and outputs the
titles of each movie sorted by their IMBD rating, descending. The movies.csv file
must be in the format:

< MOVIE1 >, < YEAR1 >
...

The output is in the format:
< MOVIE1 >, < RATING1 >
< MOVIE2 >, < RATING2 >
...

where RATING1 >= RATING2 >= ... >= RATINGN.

If a movie does not have a rating available on IMBD, then it will be at the bottom
of the list in the format: < MOVIE >, N/A.
If the IMBD data of a movie cannot be accessed, then it will appear after the movies
with unavailable ratings in the format: < MOVIE >, < TYPE OF ERROR >.

Lastly, if either the movies.csv file cannot be found or if http://www.omdbapi.com/
cannot be accessed, an appropriate error message will be displayed.
