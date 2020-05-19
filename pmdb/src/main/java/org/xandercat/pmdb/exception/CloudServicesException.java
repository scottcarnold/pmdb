package org.xandercat.pmdb.exception;

public class CloudServicesException extends Exception {

	private static final long serialVersionUID = 8938442483844216256L;

	public CloudServicesException() {
	}

	public CloudServicesException(String message) {
		super(message);
	}

	public CloudServicesException(Throwable cause) {
		super(cause);
	}

	public CloudServicesException(String message, Throwable cause) {
		super(message, cause);
	}

	public CloudServicesException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
