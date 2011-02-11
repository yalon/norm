package com.yalon.norm.persist;

import com.yalon.norm.Cursor;
import com.yalon.norm.Database;

public class SelectBuilder<T extends Persistable, CursorClass extends Cursor> extends
		SelectBuilderBase<T, CursorClass, SelectBuilder<T, CursorClass>> {
	public SelectBuilder(Database db, Class<T> entity, String condition) {
		super(db, entity, condition);
	}

	public CursorClass execute() {
		return executeQuery();
	}
}