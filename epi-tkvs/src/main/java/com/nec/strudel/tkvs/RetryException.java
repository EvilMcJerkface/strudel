package com.nec.strudel.tkvs;

public class RetryException extends Exception {
	private static final long serialVersionUID = 1L;
	private final boolean retryExpired;
	public RetryException(String message, 
			boolean retryExpired, Throwable cause) {
		super(message, cause);
		this.retryExpired = retryExpired;
	}
	/**
	 * @return true if the retry is done until the
	 * limit. false if the execution failed for
	 * other reasons.
	 */
	public boolean isRetryExpired() {
		return retryExpired;
	}

}
