package com.yalon.norm.persist;

import com.yalon.norm.Cursor;
import com.yalon.norm.Database;

public class NormPersistencyManager extends PersistencyManager<Cursor> {
	public NormPersistencyManager(Database database) {
		super(database);
	}
}