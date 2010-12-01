package com.yalon.norm.sqlite.ddl;

import java.util.ArrayList;

import com.yalon.norm.Database;

/**
 * BORKEN - much work to do here before it's ready...
 * 
 * @author tal
 */
public class AlterTableBuilder extends TableBuilderBase {
	String newName;
	ArrayList<Column> addColumns;
	ArrayList<String> dropColumns;
	ArrayList<UniqueConstraint> addUniqueConstraints;
	ArrayList<UniqueConstraint> dropUniqueConstraints;

	public AlterTableBuilder(Database db, String name) {
		super(db, name);
		this.newName = null;
		this.addColumns = new ArrayList<Column>();
		this.dropColumns = new ArrayList<String>();
		this.addUniqueConstraints = new ArrayList<UniqueConstraint>();
		this.dropUniqueConstraints = new ArrayList<UniqueConstraint>();
	}

	public void renameTo(String renameTableName) {
		this.newName = renameTableName;
	}

	public Column addColumn(String name, ColumnType type) {
		Column column = new Column(name, type);
		this.addColumns.add(column);
		return column;
	}

	public Column addInteger(String name) {
		return addColumn(name, ColumnType.INTEGER);
	}

	public Column addReal(String name) {
		return addColumn(name, ColumnType.REAL);
	}

	public Column addText(String name) {
		return addColumn(name, ColumnType.TEXT);
	}

	public Column addBlob(String name) {
		return addColumn(name, ColumnType.BLOB);
	}

	public UniqueConstraint addUniqueConstraint() {
		return addUniqueConstraint(null, null);
	}

	public UniqueConstraint addUniqueConstraint(ConflictAlgorithm alg) {
		return addUniqueConstraint(null, alg);
	}

	public UniqueConstraint addUniqueConstraint(String name) {
		return addUniqueConstraint(name, null);
	}

	public UniqueConstraint dropUniqueConstraint() {
		return dropUniqueConstraint(null);
	}

	public UniqueConstraint dropUniqueConstraint(String name) {
		return dropUniqueConstraint(name);
	}

	public UniqueConstraint addUniqueConstraint(String constraintName, ConflictAlgorithm alg) {
		UniqueConstraint constraint = new UniqueConstraint(constraintName, alg);
		addUniqueConstraints.add(constraint);
		return constraint;
	}

	public void alter() {
		alter(false);
	}

	public void alter(boolean forceNewTable) {

	}
	// db.execSQL("ALTER TABLE "
	// + DatabaseUtils.sqlEscapeString(fromTableName) + " RENAME TO "
	// + DatabaseUtils.sqlEscapeString(toTableName));
}