package org.xandercat.pmdb.dao;

import java.time.LocalDate;
import java.util.Optional;

import org.xandercat.pmdb.dto.ApplicationAttribute;

/**
 * Interface for working with application attributes.
 * 
 * @author Scott Arnold
 */
public interface ApplicationDao {

	/**
	 * Add application attribute.
	 * 
	 * @param applicationAttribute attribute to store
	 */
	public void addApplicationAttribute(ApplicationAttribute applicationAttribute);
	
	/**
	 * Update application attribute.
	 * 
	 * @param applicationAttribute attribute to update
	 */
	public void updateApplicationAttribute(ApplicationAttribute applicationAttribute);
	
	/**
	 * Returns application attribute of given name for given date.
	 * 
	 * @param name  name of attribute to return
	 * @param date  date of attribute to return
	 * 
	 * @return attribute of given name and date
	 */
	public Optional<ApplicationAttribute> getApplicationAttribute(String name, LocalDate date);
	
}
