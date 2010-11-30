package com.yalon.norm;

public interface Statement {
	void execute();

	long executeInsert();

	long simpleQueryForLong();

	String simpleQueryForString();

	void bindNull(int index);

	void bindLong(int index, long value);

	void bindDouble(int index, double value);

	void bindString(int index, String value);

	void bindBlob(int index, byte[] value);

	void clearBindings();

	void close();
}
