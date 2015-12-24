package com.nec.strudel.tkvs;

public class TkvStoreException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public TkvStoreException(String message) {
		super(message);
	}

	public TkvStoreException(Throwable cause) {
		super(cause);
	}

	public TkvStoreException(String message, Throwable cause) {
		super(message, cause);
	}

	public TkvStoreException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
