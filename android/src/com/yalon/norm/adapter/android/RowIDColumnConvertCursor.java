package com.yalon.norm.adapter.android;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

/**
 * Because we lack column aliasing on the ORM level...
 * @author tal
 *
 */
public class RowIDColumnConvertCursor implements android.database.Cursor {
	private android.database.Cursor cursor;
	private int rowIDColumnIndex;

	public RowIDColumnConvertCursor(android.database.Cursor cursor) {
		this.cursor = cursor;
		this.rowIDColumnIndex = cursor.getColumnIndexOrThrow("rowid");
	}

	public int getCount() {
		return cursor.getCount();
	}

	public int getPosition() {
		return cursor.getPosition();
	}

	public boolean move(int offset) {
		return cursor.move(offset);
	}

	public boolean moveToPosition(int position) {
		return cursor.moveToPosition(position);
	}

	public boolean moveToFirst() {
		return cursor.moveToFirst();
	}

	public boolean moveToLast() {
		return cursor.moveToLast();
	}

	public boolean moveToNext() {
		return cursor.moveToNext();
	}

	public boolean moveToPrevious() {
		return cursor.moveToPrevious();
	}

	public boolean isFirst() {
		return cursor.isFirst();
	}

	public boolean isLast() {
		return cursor.isLast();
	}

	public boolean isBeforeFirst() {
		return cursor.isBeforeFirst();
	}

	public boolean isAfterLast() {
		return cursor.isAfterLast();
	}

	public int getColumnIndex(String columnName) {
		if (columnName.equals("_id") || columnName.equals("rowid")) {
			return rowIDColumnIndex;
		}
		return cursor.getColumnIndex(columnName);
	}

	public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
		if (columnName.equals("_id") || columnName.equals("rowid")) {
			return rowIDColumnIndex;
		}
		return cursor.getColumnIndexOrThrow(columnName);
	}

	public String getColumnName(int columnIndex) {
		if (columnIndex == rowIDColumnIndex) {
			return "_id";
		}
		return cursor.getColumnName(columnIndex);
	}

	public String[] getColumnNames() {
		String[] result = cursor.getColumnNames();
		result[rowIDColumnIndex] = "_id";
		return result;
	}

	public int getColumnCount() {
		return cursor.getColumnCount();
	}

	public byte[] getBlob(int columnIndex) {
		return cursor.getBlob(columnIndex);
	}

	public String getString(int columnIndex) {
		return cursor.getString(columnIndex);
	}

	public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
		cursor.copyStringToBuffer(columnIndex, buffer);
	}

	public short getShort(int columnIndex) {
		return cursor.getShort(columnIndex);
	}

	public int getInt(int columnIndex) {
		return cursor.getInt(columnIndex);
	}

	public long getLong(int columnIndex) {
		return cursor.getLong(columnIndex);
	}

	public float getFloat(int columnIndex) {
		return cursor.getFloat(columnIndex);
	}

	public double getDouble(int columnIndex) {
		return cursor.getDouble(columnIndex);
	}

	public boolean isNull(int columnIndex) {
		return cursor.isNull(columnIndex);
	}

	public void deactivate() {
		cursor.deactivate();
	}

	public boolean requery() {
		return cursor.requery();
	}

	public void close() {
		cursor.close();
	}

	public boolean isClosed() {
		return cursor.isClosed();
	}

	public void registerContentObserver(ContentObserver observer) {
		cursor.registerContentObserver(observer);
	}

	public void unregisterContentObserver(ContentObserver observer) {
		cursor.unregisterContentObserver(observer);
	}

	public void registerDataSetObserver(DataSetObserver observer) {
		cursor.registerDataSetObserver(observer);
	}

	public void unregisterDataSetObserver(DataSetObserver observer) {
		cursor.unregisterDataSetObserver(observer);
	}

	public void setNotificationUri(ContentResolver cr, Uri uri) {
		cursor.setNotificationUri(cr, uri);
	}

	public boolean getWantsAllOnMoveCalls() {
		return cursor.getWantsAllOnMoveCalls();
	}

	public Bundle getExtras() {
		return cursor.getExtras();
	}

	public Bundle respond(Bundle extras) {
		return cursor.respond(extras);
	}
}
