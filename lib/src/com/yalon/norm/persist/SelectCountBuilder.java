package com.yalon.norm.persist;

import com.yalon.norm.NormSQLException;

public class SelectCountBuilder<T extends Persistable> extends SelectBuilderBase<T, SelectCountBuilder<T>> {
	public SelectCountBuilder(Class<T> entity, String condition) {
		super(entity, condition);
	}

	public long count() {
		EntityCursor<T> cur = executeQuery();
		if (filter == null) {
			try {
				if (cur.moveToNext()) {
					return cur.getLong(0);
				}
				throw new NormSQLException("select count(*) didn't return a single value");
			} finally {
				cur.close();
			}
		} else {
			try {
				return cur.getCount();
			} finally {
				cur.close();
			}
		}
	}

	protected void appendColumns(StringBuilder builder) {
		if (filter == null) {
			builder.append("COUNT(*)");
		} else {
			super.appendColumns(builder);
		}
	}
}