package com.yalon.norm.persist;

import com.yalon.norm.Cursor;
import com.yalon.norm.Database;
import com.yalon.norm.NormSQLException;

public class SelectCountBuilder<T extends Persistable, CursorClass extends Cursor> extends
		SelectBuilderBase<T, CursorClass, SelectCountBuilder<T, CursorClass>> {
	public SelectCountBuilder(Database db, Class<T> entity, String condition) {
		super(db, entity, condition);
	}

	public long count() {
		CursorClass cur = executeQuery();
		try {
			if (cur.moveToNext()) {
				return cur.getLong(0);
			}
			throw new NormSQLException("select count(*) didn't return a single value");
		} finally {
			cur.close();
		}
	}

	protected void appendColumns(StringBuilder builder) {
		builder.append("COUNT(*)");
	}
}