package org.xandercat.pmdb.dao;

import java.time.LocalDate;
import java.util.Optional;

import org.xandercat.pmdb.dto.ApplicationAttribute;

public interface ApplicationDao {

	public void addApplicationAttribute(ApplicationAttribute applicationAttribute);
	
	public void updateApplicationAttribute(ApplicationAttribute applicationAttribute);
	
	public Optional<ApplicationAttribute> getApplicationAttribute(String name, LocalDate date);
	
}
