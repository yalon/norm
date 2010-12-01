package com.yalon.norm.adapter.android;

import com.yalon.norm.Cursor;

public class AndroidCursor implements Cursor {
	android.database.Cursor cursor;

	AndroidCursor(android.database.Cursor cursor) {
		this.cursor = cursor;
	}

	@Override
	public int getColumnCount() {
		return this.cursor.getColumnCount();
	}

	@Override
	public int getColumnIndex(String columnName) {
		return this.cursor.getColumnIndex(columnName);
	}

	@Override
	public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
		return this.cursor.getColumnIndexOrThrow(columnName);
	}

	@Override
	public String getColumnName(int columnIndex) {
		return this.cursor.getColumnName(columnIndex);
	}

	@Override
	public String[] getColumnNames() {
		return this.cursor.getColumnNames();
	}

	@Override
	public byte[] getBlob(int columnIndex) {
		return this.cursor.getBlob(columnIndex);
	}

	@Override
	public boolean getBoolean(int columnIndex) {
		return this.cursor.getInt(columnIndex) == 0 ? false : true;
	}

	@Override
	public char getCharacter(int columnIndex) {
		return this.cursor.getString(columnIndex).charAt(0);
	}

	@Override
	public byte getByte(int columnIndex) {
		return (byte) this.cursor.getInt(columnIndex);
	}

	@Override
	public double getDouble(int columnIndex) {
		return this.cursor.getDouble(columnIndex);
	}

	@Override
	public float getFloat(int columnIndex) {
		return this.cursor.getFloat(columnIndex);
	}

	@Override
	public int getInt(int columnIndex) {
		return this.cursor.getInt(columnIndex);
	}

	@Override
	public long getLong(int columnIndex) {
		return this.cursor.getLong(columnIndex);
	}

	@Override
	public short getShort(int columnIndex) {
		return this.cursor.getShort(columnIndex);
	}

	@Override
	public String getString(int columnIndex) {
		return this.cursor.getString(columnIndex);
	}

	@Override
	public boolean isNull(int columnIndex) {
		return this.cursor.isNull(columnIndex);
	}

	@Override
	public int getCount() {
		return this.cursor.getCount();
	}

	@Override
	public int getPosition() {
		return this.cursor.getPosition();
	}

	@Override
	public boolean move(int offset) {
		return this.cursor.move(offset);
	}

	@Override
	public boolean moveToPosition(int position) {
		return this.cursor.moveToPosition(position);
	}

	@Override
	public boolean moveToFirst() {
		return this.cursor.moveToFirst();
	}

	@Override
	public boolean moveToLast() {
		return this.cursor.moveToLast();
	}

	@Override
	public boolean moveToNext() {
		return this.cursor.moveToNext();
	}

	@Override
	public boolean moveToPrevious() {
		return this.cursor.moveToPrevious();
	}

	@Override
	public boolean isFirst() {
		return this.cursor.isFirst();
	}

	@Override
	public boolean isLast() {
		return this.cursor.isLast();
	}

	@Override
	public boolean isBeforeFirst() {
		return this.cursor.isBeforeFirst();
	}

	@Override
	public boolean isAfterLast() {
		return this.cursor.isAfterLast();
	}

	@Override
	public void deactivate() {
		this.cursor.deactivate();
	}

	@Override
	public boolean requery() {
		return this.cursor.requery();
	}

	@Override
	public void close() {
		this.cursor.close();
	}

	@Override
	public boolean isClosed() {
		return this.cursor.isClosed();
	}

}
