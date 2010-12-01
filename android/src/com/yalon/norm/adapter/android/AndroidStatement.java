package com.yalon.norm.adapter.android;

import android.database.sqlite.SQLiteStatement;

import com.yalon.norm.Statement;

public class AndroidStatement implements Statement {
	SQLiteStatement statement;
	
	AndroidStatement(SQLiteStatement statement) {
		this.statement = statement;
	}

	@Override
	public void execute() {
		this.statement.execute();

	}

	@Override
	public long executeInsert() {
		return this.statement.executeInsert();
	}

	@Override
	public long simpleQueryForLong() {
		return this.statement.simpleQueryForLong();
	}

	@Override
	public String simpleQueryForString() {
		return this.statement.simpleQueryForString();
	}

	@Override
	public void bindNull(int index) {
		this.statement.bindNull(index);
	}

	@Override
	public void bindInt(int index, int value) {
		this.statement.bindLong(index, value);
	}

	@Override
	public void bindLong(int index, long value) {
		this.statement.bindLong(index, value);
	}

	@Override
	public void bindDouble(int index, double value) {
		this.statement.bindDouble(index, value);
	}

	@Override
	public void bindString(int index, String value) {
		this.statement.bindString(index, value);
	}

	@Override
	public void bindBlob(int index, byte[] value) {
		this.statement.bindBlob(index, value);
	}

	@Override
	public void clearBindings() {
		this.statement.clearBindings();
	}

	@Override
	public void close() {
		this.statement.close();
	}
}
