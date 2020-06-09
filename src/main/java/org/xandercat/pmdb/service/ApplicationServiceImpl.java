package org.xandercat.pmdb.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xandercat.pmdb.dao.ApplicationDao;
import org.xandercat.pmdb.dto.ApplicationAttribute;

@Component
public class ApplicationServiceImpl implements ApplicationService {

	public static final String IMDB_SERVICE_CALLS = "IMDB Service Calls";
	public static final String REGISTRATIONS_COUNT_TRIGGER_TIME = "Registrations Trigger Time";
	public static final String REGISTRATIONS_COUNT = "Registrations Trigger Count";
	
	private static final int MAX_REGISTRATIONS_COUNT = 1000;
	
	@Autowired
	private ApplicationDao applicationDao;
	
	@Value("${registrations.interval.minutes}")
	private int regMinutes;
	
	@Override
	public int getImdbServiceCallCount() {
		return getImdbServiceCallCount(LocalDate.now());
	}

	@Override
	public int getImdbServiceCallCount(LocalDate date) {
		Optional<ApplicationAttribute> imdbServiceCalls = applicationDao.getApplicationAttribute(IMDB_SERVICE_CALLS, date);
		return (imdbServiceCalls.isPresent())? Integer.parseInt(imdbServiceCalls.get().getValue()) : 0;		
	}
	
	@Override
	public void incrementImdbServiceCallCount() {
		LocalDate today = LocalDate.now();
		Optional<ApplicationAttribute> imdbServiceCallsOptional = applicationDao.getApplicationAttribute(IMDB_SERVICE_CALLS, today);
		if (!imdbServiceCallsOptional.isPresent()) {
			ApplicationAttribute imdbServiceCalls = new ApplicationAttribute();
			imdbServiceCalls.setName(IMDB_SERVICE_CALLS);
			imdbServiceCalls.setDate(today);
			imdbServiceCalls.setValue("1");
			applicationDao.addApplicationAttribute(imdbServiceCalls);
		} else {
			ApplicationAttribute imdbServiceCalls = imdbServiceCallsOptional.get();
			int count = Integer.parseInt(imdbServiceCalls.getValue());
			imdbServiceCalls.setValue(String.valueOf(count+1));
			applicationDao.updateApplicationAttribute(imdbServiceCalls);
		}
	}

	@Override
	public int incrementRegistrationsTriggerCount() {
		LocalDate today = LocalDate.now();
		Optional<ApplicationAttribute> triggerTimeAttributeOptional = applicationDao.getApplicationAttribute(REGISTRATIONS_COUNT_TRIGGER_TIME, today);
		long systemTime = System.currentTimeMillis();
		if (!triggerTimeAttributeOptional.isPresent()) {
			ApplicationAttribute triggerTimeAttribute = new ApplicationAttribute(REGISTRATIONS_COUNT_TRIGGER_TIME, String.valueOf(systemTime), today);
			applicationDao.addApplicationAttribute(triggerTimeAttribute);
			setRegistrationsTriggerCount(1, today);
			return 1;
		}
		ApplicationAttribute triggerTimeAttribute = triggerTimeAttributeOptional.get();
		long triggerTime = Long.parseLong(triggerTimeAttribute.getValue());
		if ((systemTime - triggerTime) > (regMinutes * 60 * 1000)) {
			triggerTimeAttribute.setValue(String.valueOf(systemTime));
			applicationDao.updateApplicationAttribute(triggerTimeAttribute);
			setRegistrationsTriggerCount(1, today);
			return 1;
		}
		return incrementRegistrationsTriggerCount(today);
	}
	
	private int incrementRegistrationsTriggerCount(LocalDate date) {
		Optional<ApplicationAttribute> triggerCountAttributeOptional = applicationDao.getApplicationAttribute(REGISTRATIONS_COUNT, date);
		if (!triggerCountAttributeOptional.isPresent()) {
			ApplicationAttribute triggerCountAttribute = new ApplicationAttribute(REGISTRATIONS_COUNT, "1", date);
			applicationDao.addApplicationAttribute(triggerCountAttribute);
			return 1;
		}
		ApplicationAttribute triggerCountAttribute = triggerCountAttributeOptional.get();
		int count = Integer.parseInt(triggerCountAttribute.getValue());
		count = Math.min(MAX_REGISTRATIONS_COUNT, count+1);
		triggerCountAttribute.setValue(String.valueOf(count));
		applicationDao.updateApplicationAttribute(triggerCountAttribute);
		return count;
	}
	
	private void setRegistrationsTriggerCount(int count, LocalDate date) {
		Optional<ApplicationAttribute> triggerCountAttributeOptional = applicationDao.getApplicationAttribute(REGISTRATIONS_COUNT, date);
		if (!triggerCountAttributeOptional.isPresent()) {
			ApplicationAttribute triggerCountAttribute = new ApplicationAttribute(REGISTRATIONS_COUNT, String.valueOf(count), date);
			applicationDao.addApplicationAttribute(triggerCountAttribute);			
		} else {
			ApplicationAttribute triggerCountAttribute = triggerCountAttributeOptional.get();
			triggerCountAttribute.setValue(String.valueOf(count));
			applicationDao.updateApplicationAttribute(triggerCountAttribute);
		}
	}
}
