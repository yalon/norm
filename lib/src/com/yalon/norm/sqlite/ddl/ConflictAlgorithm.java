package com.yalon.norm.sqlite.ddl;

public enum ConflictAlgorithm {
	ROLLBACK, ABORT, FAIL, IGNORE, REPLACE;

	public static String toString(ConflictAlgorithm t) {
		switch (t) {
		case ROLLBACK:
			return "ROLLBACK";
		case ABORT:
			return "ABORT";
		case FAIL:
			return "FAIL";
		case IGNORE:
			return "IGNORE";
		case REPLACE:
			return "REPLACE";
		default:
			throw new RuntimeException("this can't be.");
		}
	}
}
