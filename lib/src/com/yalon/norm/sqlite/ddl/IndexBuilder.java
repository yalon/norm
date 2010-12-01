package com.yalon.norm.sqlite.ddl;

import com.yalon.norm.Database;

public class IndexBuilder extends ConstraintBase {
	protected Database db;
	protected boolean createIfNotExists;

	/* package */IndexBuilder(Database db, String tableName, String name) {
		super(tableName, name);
		this.createIfNotExists = false;
	}

	public void createIfNotExists() {
		createIfNotExists = true;
		create();
	}

	public void create() {
		db.execSQL(toCreateSQL());
	}

	/* package */String toCreateSQL() {
		StringBuilder sql = new StringBuilder("CREATE INDEX");
		if (unique) {
			sql.append(" UNIQUE");
		}

		if (createIfNotExists) {
			sql.append(" IF NOT EXISTS");
		}

		sql.append(" ");
		sql.append(getName());
		sql.append(" ON ");
		sql.append(tableName);
		appendColumnsSQL(sql);
		return sql.toString();
	}

	/* package */String toTableConstraintSQL() {
		StringBuilder sql = new StringBuilder("CONSTRAINT");
		sql.append(" ");
		sql.append(getName());
		if (unique) {
			sql.append(" UNIQUE");
		}
		appendColumnsSQL(sql);

		return sql.toString();
	}
}