package org.xandercat.pmdb.exception;

public class BandwidthException extends Exception {

	private static final long serialVersionUID = 6186550261880652210L;

	private int count;
	private int max;
	
	public BandwidthException(int count, int max) {
		this.count = count;
		this.max = max;
	}

	public BandwidthException(String message, int count, int max) {
		super(message);
		this.count = count;
		this.max = max;
	}

	public BandwidthException(Throwable cause, int count, int max) {
		super(cause);
		this.count = count;
		this.max = max;
	}

	public BandwidthException(String message, Throwable cause, int count, int max) {
		super(message, cause);
		this.count = count;
		this.max = max;
	}

	public BandwidthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int count, int max) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.count = count;
		this.max = max;
	}

	public boolean isInitialTrigger() {
		return count == (max+1);
	}
}
