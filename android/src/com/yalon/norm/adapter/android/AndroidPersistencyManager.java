package com.yalon.norm.adapter.android;

import com.yalon.norm.Database;
import com.yalon.norm.persist.Persistable;
import com.yalon.norm.persist.PersistencyManager;
import com.yalon.norm.persist.SelectBuilder;

public class AndroidPersistencyManager extends PersistencyManager<AndroidCursor> {
	static class AndroidSelectBuilder<T extends Persistable> extends SelectBuilder<T, AndroidCursor> {
		public AndroidSelectBuilder(Database db, Class<T> entityClass, String condition) {
			super(db, entityClass, condition);
		}
		
		protected void appendColumns(StringBuilder sql) {
			super.appendColumns(sql);
			sql.append(", rowid AS _id");
		}
	}
	
	public AndroidPersistencyManager(Database database) {
		super(database);
	}
	
	@Override
	public <T extends Persistable> SelectBuilder<T, AndroidCursor> findAll(Class<T> entityClass, String condition) {
		return new AndroidSelectBuilder<T>(db, entityClass, condition);
	}
}
