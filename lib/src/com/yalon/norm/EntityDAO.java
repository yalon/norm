package com.yalon.norm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import com.yalon.norm.mapper.EntityMap;
import com.yalon.norm.mapper.EntityMapper;
import com.yalon.norm.sqlite.ddl.ConflictAlgorithm;

public class EntityDAO<T> {
	Database db;
	EntityMap map;
	EntityMapper mapper;

	public EntityDAO(Database db, EntityMap map, Class<T> entityClass) {
		this.db = db;
		this.map = map;
		this.mapper = map.get(entityClass);
	}

	public List<T> findAll() {
		String tableName = mapper.getTableName();
		Set<String> columns = mapper.getColumns();

		StringBuilder sql = new StringBuilder("SELECT ");
		String sep = "";
		for (String col : columns) {
			sql.append(sep);
			sql.append(col);
			sep = ", ";
		}

		sql.append(" FROM ");
		sql.append(tableName);

		Cursor cur = db.execQuerySQL(sql.toString());
		try {
			Vector<T> res = new Vector<T>();
			while (cur.moveToNext()) {
				res.add(map.<T> mapRowToNewObject(cur, mapper.getEntityClass()));
			}
			return res;
		} finally {
			cur.close();
		}
	}

	public void insert(T obj) {
		HashMap<String, Object> row = new HashMap<String, Object>();
		map.mapObjectToRow(obj, row);

		Object[] values = new Object[row.size()];

		StringBuilder sql = new StringBuilder("INSERT INTO ");
		sql.append(mapper.getTableName());
		sql.append(" (");
		String sep = "";

		Iterator<Entry<String, Object>> iter = row.entrySet().iterator();
		for (int i = 0; i < values.length; ++i) {
			Entry<String, Object> entry = iter.next();
			sql.append(sep);
			sql.append(entry.getKey());
			values[i] = entry.getValue();
			sep = ", ";
		}
		sql.append(") VALUES (");
		sep = "";
		for (int i = 0; i < values.length; ++i) {
			sql.append(sep);
			sql.append("?");
			sep = ", ";
		}
		sql.append(")");
		db.execSQL(sql.toString(), values);
	}

	public void update(T obj, ConflictAlgorithm conflictResolution) {
		HashMap<String, Object> row = new HashMap<String, Object>();
		map.mapObjectToRow(obj, row);

		Object[] values = new Object[row.size()];

		StringBuilder sql = new StringBuilder("UPDATE ");
		if (conflictResolution != null) {
			sql.append(" OR ");
			sql.append(ConflictAlgorithm.toString(conflictResolution));
		}

		sql.append(mapper.getTableName());
		sql.append(" SET");
		String sep = " ";

		Iterator<Entry<String, Object>> iter = row.entrySet().iterator();
		for (int i = 0; i < values.length; ++i) {
			Entry<String, Object> entry = iter.next();
			sql.append(sep);
			sql.append(entry.getKey());
			values[i] = entry.getValue();
			sep = ", ";
		}
		sql.append(") VALUES (");
		sep = "";
		for (int i = 0; i < values.length; ++i) {
			sql.append(sep);
			sql.append("?");
			sep = ", ";
		}
		sql.append(")");
		db.execSQL(sql.toString(), values);
	}
}