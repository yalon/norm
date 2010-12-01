package com.yalon.norm.adapter.android;

import java.util.Locale;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.yalon.norm.Cursor;
import com.yalon.norm.Database;
import com.yalon.norm.Statement;

public class AndroidDatabase implements Database {
	SQLiteDatabase db;
	AndroidSQLExceptionConverter exceptionConverter;

	public AndroidDatabase(SQLiteDatabase db) {
		this.db = db;
		this.exceptionConverter = new AndroidSQLExceptionConverter();
	}

	@Override
	public void beginTransaction() {
		try {
			this.db.beginTransaction();
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}
	}

	@Override
	public void endTransaction() {
		try {
			this.db.endTransaction();
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}
	}

	@Override
	public void setTransactionSuccessful() {
		try {
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}
	}

	@Override
	public boolean inTransaction() {
		try {
			return this.db.inTransaction();
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}
	}

	@Override
	public void close() {
		try {
			this.db.close();
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}
	}

	@Override
	public Statement compileStatement(String sql) {
		try {
			return new AndroidStatement(this.db.compileStatement(sql), exceptionConverter);
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}
	}

	@Override
	public void execSQL(String sql) {
		try {
			this.db.execSQL(sql);
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}
	}

	@Override
	public void execSQL(String sql, Object[] bindArgs) {
		try {
			this.db.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}
	}

	@Override
	public Cursor execQuerySQL(String sql) {
		try {
			return new AndroidCursor(this.db.rawQuery(sql, new String[0]), exceptionConverter);
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}
	}

	@Override
	public Cursor execQuerySQL(String sql, String[] bindArgs) {
		try {
			return new AndroidCursor(this.db.rawQuery(sql, bindArgs), exceptionConverter);
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}
	}

	@Override
	public boolean isReadOnly() {
		try {
			return this.db.isReadOnly();
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}
	}

	@Override
	public boolean isOpen() {
		try {
			return this.db.isOpen();
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}
	}

	@Override
	public void setLocale(Locale locale) {
		try {
			this.db.setLocale(locale);
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}
	}
}
