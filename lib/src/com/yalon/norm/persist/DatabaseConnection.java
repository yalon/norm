package com.yalon.norm.persist;

import com.yalon.norm.Database;

public class DatabaseConnection {
	private static ThreadLocal<Database> dbHolder = new ThreadLocal<Database>();

	public static void open(Database db) {
		if (dbHolder.get() != null) {
			// TODO: log
			dbHolder.get().close();
		}

		dbHolder.set(db);
	}
	
	public static Database get() {
		return dbHolder.get();
	}

	public static void close() {
		if (dbHolder.get() != null) {
			dbHolder.get().close();
			dbHolder.remove();
		} else {
			// TODO: log
		}
	}
}
