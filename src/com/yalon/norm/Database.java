package com.yalon.norm;

import java.util.Locale;

public interface Database {
	void beginTransaction();

	void endTransaction();

	void setTransactionSuccessful();

	boolean inTransaction();

	void close();

	Statement compileStatement(String sql);

	void execSQL(String sql);

	void execSQL(String sql, Object[] bindArgs);

	Cursor execQuerySQL(String sql);

	Cursor execQuerySQL(String sql, String[] bindArgs);

	boolean isReadOnly();

	boolean isOpen();

	void setLocale(Locale locale);
}