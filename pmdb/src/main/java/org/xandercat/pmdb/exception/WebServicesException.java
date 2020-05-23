package org.xandercat.pmdb.exception;

public class WebServicesException extends Exception {

	private static final long serialVersionUID = 8938442483844216256L;

	public WebServicesException() {
	}

	public WebServicesException(String message) {
		super(message);
	}

	public WebServicesException(Throwable cause) {
		super(cause);
	}

	public WebServicesException(String message, Throwable cause) {
		super(message, cause);
	}

	public WebServicesException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
