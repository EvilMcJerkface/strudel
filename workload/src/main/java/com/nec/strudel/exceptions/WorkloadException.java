package com.nec.strudel.exceptions;

public class WorkloadException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public WorkloadException() {
	}

	public WorkloadException(String message) {
		super(message);
	}

	public WorkloadException(Throwable cause) {
		super(cause);
	}

	public WorkloadException(String message, Throwable cause) {
		super(message, cause);
	}

}
