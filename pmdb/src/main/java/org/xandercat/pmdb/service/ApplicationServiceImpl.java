package org.xandercat.pmdb.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xandercat.pmdb.dao.ApplicationDao;
import org.xandercat.pmdb.dto.ApplicationAttribute;

@Component
public class ApplicationServiceImpl implements ApplicationService {

	public static final String IMDB_SERVICE_CALLS = "IMDB Service Calls";
	
	@Autowired
	private ApplicationDao applicationDao;
	
	@Override
	public int getImdbServiceCallCount() {
		return getImdbServiceCallCount(LocalDate.now());
	}

	@Override
	public int getImdbServiceCallCount(LocalDate date) {
		ApplicationAttribute imdbServiceCalls = applicationDao.getApplicationAttribute(IMDB_SERVICE_CALLS, date);
		return (imdbServiceCalls == null)? 0 : Integer.parseInt(imdbServiceCalls.getValue());		
	}
	
	@Override
	public void incrementImdbServiceCallCount() {
		LocalDate today = LocalDate.now();
		ApplicationAttribute imdbServiceCalls = applicationDao.getApplicationAttribute(IMDB_SERVICE_CALLS, today);
		if (imdbServiceCalls == null) {
			imdbServiceCalls = new ApplicationAttribute();
			imdbServiceCalls.setName(IMDB_SERVICE_CALLS);
			imdbServiceCalls.setDate(today);
			imdbServiceCalls.setValue("1");
			applicationDao.addApplicationAttribute(imdbServiceCalls);
		} else {
			int count = Integer.parseInt(imdbServiceCalls.getValue());
			imdbServiceCalls.setValue(String.valueOf(count+1));
			applicationDao.updateApplicationAttribute(imdbServiceCalls);
		}
	}
}
