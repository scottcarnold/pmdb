package org.xandercat.pmdb.exception;

/**
 * Exception class for when a service call exceeds a set limit.
 * 
 * @author Scott Arnold
 */
public class ServiceLimitExceededException extends Exception {

	private static final long serialVersionUID = 4512274297937794213L;
	private int serviceCalls;

	public ServiceLimitExceededException(int serviceCalls) {
		this.serviceCalls = serviceCalls;
	}

	public ServiceLimitExceededException(String message, int serviceCalls) {
		super(message);
		this.serviceCalls = serviceCalls;
	}

	public ServiceLimitExceededException(Throwable cause, int serviceCalls) {
		super(cause);
		this.serviceCalls = serviceCalls;
	}

	public ServiceLimitExceededException(String message, Throwable cause, int serviceCalls) {
		super(message, cause);
		this.serviceCalls = serviceCalls;
	}

	public ServiceLimitExceededException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace, int serviceCalls) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.serviceCalls = serviceCalls;
	}

	public int getServiceCalls() {
		return serviceCalls;
	}
}
