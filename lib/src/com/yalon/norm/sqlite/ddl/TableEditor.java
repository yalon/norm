package com.yalon.norm.sqlite.ddl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.yalon.norm.Cursor;
import com.yalon.norm.Database;
import com.yalon.norm.utils.DatabaseUtils;
import com.yalon.norm.utils.StringUtils;

// TODO: someday we'll parse the CREATE statement and create a smart "alter table" builder.
public class TableEditor {
	protected Database db;

	public TableEditor(Database db) {
		this.db = db;
	}

	public TableBuilder build(String tableName) {
		return new TableBuilder(db, tableName);
	}

	public void drop(String tableName) {
		StringBuilder sql = new StringBuilder("DROP TABLE ");
		sql.append(tableName);
		db.execSQL(sql.toString());
	}

	public void dropIfExists(String tableName) {
		StringBuilder sql = new StringBuilder("DROP TABLE IF EXISTS ");
		sql.append(tableName);
		db.execSQL(sql.toString());
	}

	// Used to do a poor-man's ALTER TABLE...
	public void copy(String fromTableName, String toTableName) {
		Cursor cur = db.execQuerySQL("pragma table_info("
				+ DatabaseUtils.sqlEscapeString(toTableName) + ")");
		HashMap<String, String> mapping = new HashMap<String, String>();
		try {
			while (cur.moveToNext()) {
				mapping.put(cur.getString(1), cur.getString(1));
			}
		} finally {
			cur.close();
		}
		copy(fromTableName, toTableName, mapping);
	}

	public void copy(String fromTableName, String toTableName, Map<String, String> columnMapping) {
		StringBuilder fromColumnList = new StringBuilder();
		StringBuilder toColumnList = new StringBuilder();
		String sep = "";
		for (Entry<String, String> entry : columnMapping.entrySet()) {
			fromColumnList.append(sep);
			toColumnList.append(sep);
			fromColumnList.append(entry.getKey());
			toColumnList.append(entry.getValue());
			sep = ", ";
		}
		StringBuilder sql = new StringBuilder("INSERT INTO ");
		sql.append(toTableName);
		sql.append(" (");
		sql.append(toColumnList);
		sql.append(") SELECT ");
		sql.append(fromColumnList);
		sql.append(" FROM ");
		sql.append(fromTableName);
		db.execSQL(sql.toString());
	}

	public void rename(String fromTableName, String toTableName) {
		db.execSQL("ALTER TABLE " + DatabaseUtils.sqlEscapeString(fromTableName) + " RENAME TO "
				+ DatabaseUtils.sqlEscapeString(toTableName));
	}

	public IndexBuilder buildIndex(String tableName, String indexName) {
		return new IndexBuilder(db, tableName, indexName);
	}

	public IndexBuilder buildIndex(String tableName) {
		return new IndexBuilder(db, tableName, null);
	}

	public void dropIndex(String indexName) {
		StringBuilder sql = new StringBuilder("DROP INDEX ");
		DatabaseUtils.appendEscapedSQLString(sql, indexName);
		db.execSQL(sql.toString());
	}

	public void dropIndexIfExists(String indexName) {
		StringBuilder sql = new StringBuilder("DROP INDEX IF EXISTS ");
		DatabaseUtils.appendEscapedSQLString(sql, indexName);
		db.execSQL(sql.toString());
	}

	public void dropIndex(String tableName, String[] columns) {
		dropIndex(buildIndexName(tableName, columns));
	}

	public void dropIndexIfExists(String tableName, String[] columns) {
		dropIndexIfExists(buildIndexName(tableName, columns));
	}

	public static String buildIndexName(String tableName, String[] columns) {
		StringBuilder s = new StringBuilder("i_");
		s.append(tableName);
		StringUtils.join(columns, "_", "_", s);
		return s.toString();
	}
}
