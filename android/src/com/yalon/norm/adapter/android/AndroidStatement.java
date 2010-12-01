package com.yalon.norm.adapter.android;

import android.database.SQLException;
import android.database.sqlite.SQLiteStatement;

import com.yalon.norm.Statement;

public class AndroidStatement implements Statement {
	SQLiteStatement statement;
	AndroidSQLExceptionConverter exceptionConverter;

	AndroidStatement(SQLiteStatement statement, AndroidSQLExceptionConverter exceptionConverter) {
		this.statement = statement;
		this.exceptionConverter = exceptionConverter;
	}

	@Override
	public void execute() {
		try {
			this.statement.execute();
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}

	}

	@Override
	public long executeInsert() {
		try {
			return this.statement.executeInsert();
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}
	}

	@Override
	public long simpleQueryForLong() {
		try {
			return this.statement.simpleQueryForLong();
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}
	}

	@Override
	public String simpleQueryForString() {
		try {
			return this.statement.simpleQueryForString();
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}
	}

	@Override
	public void bindNull(int index) {
		try {
			this.statement.bindNull(index);
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}
	}

	@Override
	public void bindInt(int index, int value) {
		try {
			this.statement.bindLong(index, value);
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}
	}

	@Override
	public void bindLong(int index, long value) {
		try {
			this.statement.bindLong(index, value);
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}
	}

	@Override
	public void bindDouble(int index, double value) {
		try {
			this.statement.bindDouble(index, value);
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}
	}

	@Override
	public void bindString(int index, String value) {
		try {
			this.statement.bindString(index, value);
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}
	}

	@Override
	public void bindBlob(int index, byte[] value) {
		try {
			this.statement.bindBlob(index, value);
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}
	}

	@Override
	public void clearBindings() {
		try {
			this.statement.clearBindings();
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}
	}

	@Override
	public void close() {
		try {
			this.statement.close();
		} catch (SQLException e) {
			throw this.exceptionConverter.convert(e);
		}
	}
}
