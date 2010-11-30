package com.yalon.norm.sqlite.ddl;

import java.util.ArrayList;

/* package */ abstract class ConstraintBase {
	public static class Column {
		String name;
		String collate;
		boolean asc;

		public Column(String name) {
			this.name = name;
			this.collate = null;
			this.asc = true;
		}

		public Column asc() {
			asc = true;
			return this;
		}

		public Column desc() {
			asc = false;
			return this;
		}

		public Column collate(String collate) {
			this.collate = collate;
			return this;
		}

		public String toString() {
			StringBuilder sql = new StringBuilder(name);
			if (collate != null) {
				sql.append(" COLLATE ");
				sql.append(collate);
			}
			sql.append(asc ? " ASC" : " DESC");
			return sql.toString();
		}
	}

	protected String tableName;
	protected String name;
	protected boolean unique;
	protected ArrayList<Column> columns;

	/* package */  ConstraintBase(String tableName, String name) {
		this.tableName = tableName;
		this.name = name;
		this.unique = false;
		this.columns = new ArrayList<Column>();
	}

	public Column column(String name) {
		Column col = new Column(name);
		columns.add(col);
		return col;
	}

	public String getName() {
		if (this.name != null) {
			return this.name;
		}

		StringBuilder name = new StringBuilder("i_");
		name.append(tableName);
		for (Column col : columns) {
			name.append("_");
			name.append(col.name);
		}
		return name.toString();
	}

	protected void appendColumnsSQL(StringBuilder sql) {
		sql.append(" (");
		String sep = "";
		for (Column col : columns) {
			sql.append(sep);
			sql.append(col.toString());
			sep = ", ";
		}
		sql.append(")");
	}
}