package org.xandercat.pmdb.exception;

/**
 * Exception for when collection sharing rules/permissions are violated.
 * 
 * @author Scott Arnold
 */
public class CollectionSharingException extends Exception {

	private static final long serialVersionUID = -5436109168065155578L;

	public CollectionSharingException() {
	}

	public CollectionSharingException(String message) {
		super(message);
	}

	public CollectionSharingException(Throwable cause) {
		super(cause);
	}

	public CollectionSharingException(String message, Throwable cause) {
		super(message, cause);
	}

	public CollectionSharingException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
