package com.yalon.norm.sqlite.ddl;

public enum ColumnType {
	INTEGER, REAL, TEXT, BLOB;

	public static String toString(ColumnType t) {
		switch (t) {
		case INTEGER:
			return "INTEGER";
		case REAL:
			return "REAL";
		case TEXT:
			return "TEXT";
		case BLOB:
			return "BLOB";
		default:
			throw new RuntimeException("this can't be.");
		}
	}
}
