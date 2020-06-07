package org.xandercat.pmdb.exception;

/**
 * Exception class for when a service call exceeds a set limit.
 * 
 * @author Scott Arnold
 */
public class ServiceLimitExceededException extends Exception {

	private static final long serialVersionUID = 4512274297937794213L;
	private int serviceCalls;
	private int callsLimit;

	public ServiceLimitExceededException(int serviceCalls, int callsLimit) {
		this.serviceCalls = serviceCalls;
		this.callsLimit = callsLimit;
	}

	public ServiceLimitExceededException(String message, int serviceCalls, int callsLimit) {
		super(message);
		this.serviceCalls = serviceCalls;
		this.callsLimit = callsLimit;
	}

	public ServiceLimitExceededException(Throwable cause, int serviceCalls, int callsLimit) {
		super(cause);
		this.serviceCalls = serviceCalls;
		this.callsLimit = callsLimit;
	}

	public ServiceLimitExceededException(String message, Throwable cause, int serviceCalls, int callsLimit) {
		super(message, cause);
		this.serviceCalls = serviceCalls;
		this.callsLimit = callsLimit;
	}

	public ServiceLimitExceededException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace, int serviceCalls, int callsLimit) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.serviceCalls = serviceCalls;
		this.callsLimit = callsLimit;
	}

	public int getServiceCalls() {
		return serviceCalls;
	}
	
	public int getCallsLimit() {
		return callsLimit;
	}

	public boolean isInitialTrigger() {
		return serviceCalls == (callsLimit+1);
	}
}
