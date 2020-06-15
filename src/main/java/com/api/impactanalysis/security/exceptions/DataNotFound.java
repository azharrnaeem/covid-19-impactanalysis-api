package com.api.impactanalysis.security.exceptions;

public class DataNotFound extends Exception {
	private static final long serialVersionUID = 1976227234357295682L;

	public DataNotFound(String message, Throwable cause) {
		super(message, cause);
	}

	public DataNotFound(String message) {
		super(message);
	}

	public DataNotFound(Throwable cause) {
		super(cause);
	}

}
