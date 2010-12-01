package com.yalon.norm.sqlite.ddl;

import java.util.ArrayList;

import com.yalon.norm.Database;

public class TableBuilder extends TableBuilderBase {
	boolean temporary;
	boolean createIfNotExists;
	ArrayList<Column> columns;
	ArrayList<UniqueConstraint> uniqueConstraints;

	/* package */TableBuilder(Database db, String name) {
		super(db, name);
		this.temporary = false;
		this.columns = new ArrayList<Column>();
		this.uniqueConstraints = new ArrayList<UniqueConstraint>();
		this.createIfNotExists = false;
	}

	public void setTemporary() {
		temporary = true;
	}

	public Column column(String name, ColumnType type) {
		Column column = new Column(name, type);
		this.columns.add(column);
		return column;
	}

	public Column integer(String name) {
		return column(name, ColumnType.INTEGER);
	}

	public Column real(String name) {
		return column(name, ColumnType.REAL);
	}

	public Column text(String name) {
		return column(name, ColumnType.TEXT);
	}

	public Column blob(String name) {
		return column(name, ColumnType.BLOB);
	}

	public UniqueConstraint uniqueConstraint() {
		return uniqueConstraint(null, null);
	}

	public UniqueConstraint uniqueConstraint(ConflictAlgorithm alg) {
		return uniqueConstraint(null, alg);
	}

	public UniqueConstraint uniqueConstraint(String name) {
		return uniqueConstraint(name, null);
	}

	public UniqueConstraint uniqueConstraint(String constraintName, ConflictAlgorithm alg) {
		UniqueConstraint constraint = new UniqueConstraint(constraintName, alg);
		uniqueConstraints.add(constraint);
		return constraint;
	}

	public void create() {
		String sql = toCreateSQL();
		db.execSQL(sql);
	}

	public void createIfNotExists(Database db) {
		createIfNotExists = true;
		create();
	}

	public String toCreateSQL() {
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE");
		if (temporary) {
			sql.append(" TEMP");
		}
		if (createIfNotExists) {
			sql.append(" IF NOT EXISTS");
		}
		sql.append(" ");
		sql.append(name);
		sql.append(" (");
		String sep = "";
		for (Column c : columns) {
			sql.append(sep);
			sql.append(c.toString());
			sep = ", ";
		}
		sql.append(")");

		for (UniqueConstraint c : uniqueConstraints) {
			sql.append(" ");
			sql.append(c.toString());
		}
		return sql.toString();
	}
}