package com.yalon.norm.persist;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yalon.norm.Cursor;
import com.yalon.norm.mapper.EntityMapper;

public class EntityCursor<T> implements Cursor {
	public static final Logger LOG = LoggerFactory.getLogger(EntityCursor.class);
	public static final int CACHE_SIZE = 20;

	protected Class<T> entity;
	protected Cursor cursor;
	protected EntityMapper entityMapper;
	protected WeakHashMap<Integer, T> cache;

	public EntityCursor(Class<T> entity, Cursor cursor) {
		this.entity = entity;
		this.cursor = cursor;
		this.entityMapper = PersistencyManager.entityMap.get(entity);
		this.cache = new WeakHashMap<Integer, T>();
	}

	public T getEntity() {
		int pos = cursor.getPosition();
		LOG.debug("getEntity pos={}", pos);
		T cachedEntity = cache.get(pos);
		if (cachedEntity == null) {
			cachedEntity = entityMapper.mapRowToNewObject(this);
			cache.put(pos, cachedEntity);
			if (cache.size() > CACHE_SIZE) {
				Iterator<Integer> iter = cache.keySet().iterator();
				while (iter.hasNext()) {
					try {
						Integer val = iter.next();
						if (!val.equals(pos)) {
							cache.remove(val);
							break;
						}
					} catch (NoSuchElementException e) {
						break;
					}
				}
			}
		}
		return cachedEntity;
	}

	// Shortcut so we won't have to instantiate the entity just for getting the ID.
	// NOTE: The ROWID is _case-sensitive_ and must be the same as PersistentObject's annotation definition!
	public long getEntityId() {
		LOG.debug("getEntityId");
		return cursor.getLong(cursor.getColumnIndex("rowid"));
	}

	@Override
	public int getColumnCount() {
		return cursor.getColumnCount();
	}

	@Override
	public int getColumnIndex(String columnName) {
		return cursor.getColumnIndex(columnName);
	}

	@Override
	public int getCount() {
		int res = cursor.getCount();
		LOG.debug("getCount res={}", res);
		return res;
	}

	@Override
	public int getPosition() {
		return cursor.getPosition();
	}

	@Override
	public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
		return cursor.getColumnIndexOrThrow(columnName);
	}

	@Override
	public boolean move(int offset) {
		return cursor.move(offset);
	}

	@Override
	public String getColumnName(int columnIndex) {
		return cursor.getColumnName(columnIndex);
	}

	@Override
	public boolean moveToPosition(int position) {
		return cursor.moveToPosition(position);
	}

	@Override
	public String[] getColumnNames() {
		return cursor.getColumnNames();
	}

	@Override
	public byte[] getBlob(int columnIndex) {
		return cursor.getBlob(columnIndex);
	}

	@Override
	public boolean moveToFirst() {
		return cursor.moveToFirst();
	}

	@Override
	public boolean getBoolean(int columnIndex) {
		return cursor.getBoolean(columnIndex);
	}

	@Override
	public boolean moveToLast() {
		return cursor.moveToLast();
	}

	@Override
	public char getCharacter(int columnIndex) {
		return cursor.getCharacter(columnIndex);
	}

	@Override
	public byte getByte(int columnIndex) {
		return cursor.getByte(columnIndex);
	}

	@Override
	public double getDouble(int columnIndex) {
		return cursor.getDouble(columnIndex);
	}

	@Override
	public boolean moveToNext() {
		return cursor.moveToNext();
	}

	@Override
	public boolean moveToPrevious() {
		return cursor.moveToPrevious();
	}

	@Override
	public float getFloat(int columnIndex) {
		return cursor.getFloat(columnIndex);
	}

	@Override
	public boolean isFirst() {
		return cursor.isFirst();
	}

	@Override
	public boolean isLast() {
		return cursor.isLast();
	}

	@Override
	public int getInt(int columnIndex) {
		return cursor.getInt(columnIndex);
	}

	@Override
	public boolean isBeforeFirst() {
		return cursor.isBeforeFirst();
	}

	@Override
	public boolean isAfterLast() {
		return cursor.isAfterLast();
	}

	@Override
	public long getLong(int columnIndex) {
		return cursor.getLong(columnIndex);
	}

	@Override
	public void deactivate() {
		cursor.deactivate();
	}

	@Override
	public boolean requery() {
		this.cache.clear();
		return cursor.requery();
	}

	@Override
	public short getShort(int columnIndex) {
		return cursor.getShort(columnIndex);
	}

	@Override
	public void close() {
		cursor.close();
	}

	@Override
	public boolean isClosed() {
		return cursor.isClosed();
	}

	@Override
	public String getString(int columnIndex) {
		return cursor.getString(columnIndex);
	}

	@Override
	public boolean isNull(int columnIndex) {
		return cursor.isNull(columnIndex);
	}
}
