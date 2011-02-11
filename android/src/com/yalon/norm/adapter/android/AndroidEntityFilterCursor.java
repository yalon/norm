package com.yalon.norm.adapter.android;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

import com.yalon.norm.persist.EntityFilterCursor;
import com.yalon.norm.persist.EntitySelectFilter;
import com.yalon.norm.persist.Persistable;

public class AndroidEntityFilterCursor<T extends Persistable> extends EntityFilterCursor<T> implements
		android.database.Cursor {
	private android.database.Cursor androidCursor;

	public AndroidEntityFilterCursor(Class<T> entity, AndroidCursor cursor, EntitySelectFilter<T> filter) {
		super(entity, cursor, filter);
		this.androidCursor = cursor;
	}

	@Override
	public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
		androidCursor.copyStringToBuffer(columnIndex, buffer);
	}

	@Override
	public void registerContentObserver(ContentObserver observer) {
		androidCursor.registerContentObserver(observer);
	}

	@Override
	public void unregisterContentObserver(ContentObserver observer) {
		androidCursor.unregisterContentObserver(observer);
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		androidCursor.registerDataSetObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		androidCursor.unregisterDataSetObserver(observer);
	}

	@Override
	public void setNotificationUri(ContentResolver cr, Uri uri) {
		androidCursor.setNotificationUri(cr, uri);
	}

	@Override
	public boolean getWantsAllOnMoveCalls() {
		return androidCursor.getWantsAllOnMoveCalls();
	}

	@Override
	public Bundle getExtras() {
		return androidCursor.getExtras();
	}

	@Override
	public Bundle respond(Bundle extras) {
		return androidCursor.respond(extras);
	}
}
