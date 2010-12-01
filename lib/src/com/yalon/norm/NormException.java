package com.yalon.norm;

@SuppressWarnings("serial")
public class NormException extends RuntimeException {
	public NormException() {
	}

	public NormException(String error) {
		super(error);
	}

	public NormException(Exception e) {
		super(e);
	}
}
