package org.xandercat.pmdb.service;

import java.time.LocalDate;

/**
 * Service class for application meta-data.
 * 
 * @author Scott Arnold
 */
public interface ApplicationService {

	/**
	 * Returns the number of IMDB service calls made for the current date.
	 * 
	 * @return number of IMDB service calls made for the current date
	 */
	public int getImdbServiceCallCount();
	
	/**
	 * Returns the number of IMDB service calls made for the given date.
	 * 
	 * @param date date to query
	 * 
	 * @return number of IMDB service calls made on the provided date
	 */
	public int getImdbServiceCallCount(LocalDate date);
	
	/**
	 * Increment the number of IMDB service calls made for the current date.
	 */
	public void incrementImdbServiceCallCount();
	
	/**
	 * Increment and return the number of new user registrations within the current user registrations time block.
	 * 
	 * When no time block is active, a new user registration will begin a new time block, and the registration count
	 * returned by this method will begin counting up for each registration.  This count will continue to increment
	 * for the fixed duration of the time block.  In addition to being informational, this provides a means to test
	 * for and limit the frequency of new user registrations.
	 * 
	 * @return number of user registrations within current time block
	 */
	public int incrementRegistrationsTriggerCount();
}
