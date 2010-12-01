package com.yalon.norm;

@SuppressWarnings("serial")
public class NormSQLException extends NormException {
	public NormSQLException() {
		super();
	}

	public NormSQLException(String error) {
		super(error);
	}
	
	public NormSQLException(Exception e) {
		super(e);
	}
}
