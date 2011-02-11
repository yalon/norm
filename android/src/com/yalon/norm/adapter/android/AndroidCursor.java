package com.yalon.norm.adapter.android;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.DataSetObserver;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;

import com.yalon.norm.Cursor;

public class AndroidCursor implements Cursor, android.database.Cursor {
	android.database.Cursor cursor;
	AndroidSQLExceptionConverter exceptionConverter;

	public AndroidCursor(android.database.Cursor cursor, AndroidSQLExceptionConverter exceptionConverter) {
		this.cursor = cursor;
		this.exceptionConverter = exceptionConverter;
	}

	@Override
	public int getColumnCount() {
		try {
			return this.cursor.getColumnCount();
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public int getColumnIndex(String columnName) {
		try {
			return this.cursor.getColumnIndex(columnName);
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
		try {
			return this.cursor.getColumnIndexOrThrow(columnName);
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public String getColumnName(int columnIndex) {
		try {
			return this.cursor.getColumnName(columnIndex);
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public String[] getColumnNames() {
		try {
			return this.cursor.getColumnNames();
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public byte[] getBlob(int columnIndex) {
		try {
			return this.cursor.getBlob(columnIndex);
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public boolean getBoolean(int columnIndex) {
		try {
			return this.cursor.getInt(columnIndex) == 0 ? false : true;
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public char getCharacter(int columnIndex) {
		try {
			return this.cursor.getString(columnIndex).charAt(0);
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public byte getByte(int columnIndex) {
		try {
			return (byte) this.cursor.getInt(columnIndex);
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public double getDouble(int columnIndex) {
		try {
			return this.cursor.getDouble(columnIndex);
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public float getFloat(int columnIndex) {
		try {
			return this.cursor.getFloat(columnIndex);
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public int getInt(int columnIndex) {
		try {
			return this.cursor.getInt(columnIndex);
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public long getLong(int columnIndex) {
		try {
			return this.cursor.getLong(columnIndex);
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public short getShort(int columnIndex) {
		try {
			return this.cursor.getShort(columnIndex);
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public String getString(int columnIndex) {
		try {
			return this.cursor.getString(columnIndex);
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public boolean isNull(int columnIndex) {
		try {
			return this.cursor.isNull(columnIndex);
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public int getCount() {
		try {
			return this.cursor.getCount();
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public int getPosition() {
		try {
			return this.cursor.getPosition();
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public boolean move(int offset) {
		try {
			return this.cursor.move(offset);
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public boolean moveToPosition(int position) {
		try {
			return this.cursor.moveToPosition(position);
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public boolean moveToFirst() {
		try {
			return this.cursor.moveToFirst();
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public boolean moveToLast() {
		try {
			return this.cursor.moveToLast();
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public boolean moveToNext() {
		try {
			return this.cursor.moveToNext();
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public boolean moveToPrevious() {
		try {
			return this.cursor.moveToPrevious();
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public boolean isFirst() {
		try {
			return this.cursor.isFirst();
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public boolean isLast() {
		try {
			return this.cursor.isLast();
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public boolean isBeforeFirst() {
		try {
			return this.cursor.isBeforeFirst();
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public boolean isAfterLast() {
		try {
			return this.cursor.isAfterLast();
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public void deactivate() {
		try {
			this.cursor.deactivate();
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public boolean requery() {
		try {
			return this.cursor.requery();
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public void close() {
		try {
			this.cursor.close();
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public boolean isClosed() {
		try {
			return this.cursor.isClosed();
		} catch (SQLException e) {
			throw exceptionConverter.convert(e);
		}
	}

	@Override
	public Object getPlatformCursor() {
		return cursor;
	}

	@Override
	public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
		cursor.copyStringToBuffer(columnIndex, buffer);
	}

	@Override
	public void registerContentObserver(ContentObserver observer) {
		cursor.registerContentObserver(observer);
	}

	@Override
	public void unregisterContentObserver(ContentObserver observer) {
		cursor.unregisterContentObserver(observer);
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		cursor.registerDataSetObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		cursor.unregisterDataSetObserver(observer);
	}

	@Override
	public void setNotificationUri(ContentResolver cr, Uri uri) {
		cursor.setNotificationUri(cr, uri);
	}

	@Override
	public boolean getWantsAllOnMoveCalls() {
		return cursor.getWantsAllOnMoveCalls();
	}

	@Override
	public Bundle getExtras() {
		return cursor.getExtras();
	}

	@Override
	public Bundle respond(Bundle extras) {
		return cursor.respond(extras);
	}
}