package com.yalon.norm;

public interface Statement {
	void execute();

	long executeInsert();

	long simpleQueryForLong();

	String simpleQueryForString();

	void bindNull(int index);

	void bindBoolean(int index, boolean value);

	void bindByte(int index, byte value);

	void bindShort(int index, short value);

	void bindInt(int index, int value);

	void bindLong(int index, long value);

	void bindFloat(int index, float value);

	void bindDouble(int index, double value);

	void bindString(int index, String value);

	void bindBlob(int index, byte[] value);

	void bindByValueType(int index, Object obj);

	void clearBindings();

	void close();
}
