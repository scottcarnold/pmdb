package org.xandercat.pmdb.service;

import java.time.LocalDate;

public interface ApplicationService {

	public int getImdbServiceCallCount();
	
	public int getImdbServiceCallCount(LocalDate date);
	
	public void incrementImdbServiceCallCount();
	
	public int incrementRegistrationsTriggerCount();
}
