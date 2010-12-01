package com.yalon.norm.adapter.android;

import java.util.Locale;

import android.database.sqlite.SQLiteDatabase;

import com.yalon.norm.Cursor;
import com.yalon.norm.Database;
import com.yalon.norm.Statement;

public class AndroidDatabase implements Database {
	SQLiteDatabase db;

	public AndroidDatabase(SQLiteDatabase db) {
		this.db = db;
	}

	@Override
	public void beginTransaction() {
		this.db.beginTransaction();
	}

	@Override
	public void endTransaction() {
		this.db.endTransaction();
	}

	@Override
	public void setTransactionSuccessful() {
		this.db.setTransactionSuccessful();
	}

	@Override
	public boolean inTransaction() {
		return this.db.inTransaction();
	}

	@Override
	public void close() {
		this.db.close();
	}

	@Override
	public Statement compileStatement(String sql) {
		return new AndroidStatement(this.db.compileStatement(sql));
	}

	@Override
	public void execSQL(String sql) {
		this.db.execSQL(sql);
	}

	@Override
	public void execSQL(String sql, Object[] bindArgs) {
		this.db.execSQL(sql, bindArgs);
	}

	@Override
	public Cursor execQuerySQL(String sql) {
		return new AndroidCursor(this.db.rawQuery(sql, new String[0]));
	}

	@Override
	public Cursor execQuerySQL(String sql, String[] bindArgs) {
		return new AndroidCursor(this.db.rawQuery(sql, bindArgs));
	}

	@Override
	public boolean isReadOnly() {
		return this.db.isReadOnly();
	}

	@Override
	public boolean isOpen() {
		return this.db.isOpen();
	}

	@Override
	public void setLocale(Locale locale) {
		this.db.setLocale(locale);
	}
}
