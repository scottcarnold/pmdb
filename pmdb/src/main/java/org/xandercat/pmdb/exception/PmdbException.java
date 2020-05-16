package org.xandercat.pmdb.exception;

public class PmdbException extends Exception {

	private static final long serialVersionUID = -309343344281958673L;

	public PmdbException() {
	}

	public PmdbException(String message) {
		super(message);
	}

	public PmdbException(Throwable cause) {
		super(cause);
	}

	public PmdbException(String message, Throwable cause) {
		super(message, cause);
	}

	public PmdbException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
