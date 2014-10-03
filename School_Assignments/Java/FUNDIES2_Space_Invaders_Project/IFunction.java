/**
 * 
 */

/**
 * Represents a function
 * @author briandesnoyers
 * @version April 10, 2014
 */

interface IFunction<T, U>
{

	//Returns the result of some function applied to the given t
    U apply(T t);
}
