package org.xandercat.pmdb.dao;

import java.time.LocalDate;

import org.xandercat.pmdb.dto.ApplicationAttribute;

public interface ApplicationDao {

	public void addApplicationAttribute(ApplicationAttribute applicationAttribute);
	
	public void updateApplicationAttribute(ApplicationAttribute applicationAttribute);
	
	public ApplicationAttribute getApplicationAttribute(String name, LocalDate date);
	
}
