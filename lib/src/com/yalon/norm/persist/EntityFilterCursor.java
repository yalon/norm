package com.yalon.norm.persist;

import com.yalon.norm.Cursor;
import com.yalon.norm.NormException;

public class EntityFilterCursor<T extends Persistable> extends EntityCursor<T> {
	EntitySelectFilter<T> filter;
	int cachedCount;
	int position;

	public EntityFilterCursor(Class<T> entity, Cursor cursor, EntitySelectFilter<T> filter) {
		super(entity, cursor);
		this.filter = filter;
		this.cachedCount = -1;
		this.position = -1;
	}

	@Override
	public int getCount() {
		if (cachedCount == -1) {
			int realPosition = cursor.getPosition();
			int count = 0;
			cursor.moveToPosition(-1);
			while (cursor.moveToNext()) {
				if (filter.test(getEntity())) {
					count++;
				}
			}
			cursor.moveToPosition(realPosition);
			cachedCount = count;
		}
		return cachedCount;
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public boolean move(int offset) {
		int reqPosition = offset + position;
		if (reqPosition < 0) {
			position = -1;
			cursor.moveToPosition(-1);
			return reqPosition == position;
		}
		if ((cachedCount != -1) && (reqPosition >= cachedCount)) {
			position = cachedCount;
			cursor.moveToPosition(cursor.getCount());
			return reqPosition == position;
		}

		if (offset > 0) {
			while (offset > 0 && moveToNext()) {
				offset--;
			}
		} else if (offset < 0) {
			while (offset < 0 && moveToPrevious()) {
				offset++;
			}
		}
		return offset == 0;
	}

	@Override
	public boolean moveToPosition(int newPosition) {
		return move(newPosition - position);
	}

	@Override
	public boolean moveToFirst() {
		return cursor.moveToFirst();
	}

	@Override
	public boolean moveToLast() {
		int count = getCount();
		if (count == 0) {
			return false;
		}
		position = count - 1;
		cursor.moveToPosition(cursor.getCount());
		while (cursor.moveToPrevious()) {
			if (filter.test(getEntity())) {
				return true;
			}
		}
		throw new NormException("Assertion failed: cursor filter count is positive but can't find the last row");
	}

	@Override
	public boolean moveToNext() {
		while (cursor.moveToNext()) {
			if (filter.test(getEntity())) {
				position++;
				return true;
			}
		}
		if (cachedCount == -1) {
			cachedCount = position;
		}
		return false;
	}

	@Override
	public boolean moveToPrevious() {
		while (cursor.moveToPrevious()) {
			if (filter.test(getEntity())) {
				position--;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isFirst() {
		return position == 0;
	}

	@Override
	public boolean isLast() {
		return position == getCount() - 1;
	}

	@Override
	public boolean isBeforeFirst() {
		return position == -1;
	}

	@Override
	public boolean isAfterLast() {
		return position >= getCount();
	}

	@Override
	public boolean requery() {
		cachedCount = -1;
		position = -1;
		return super.requery();
	}
}
