package com.yalon.norm.persist;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yalon.norm.Database;
import com.yalon.norm.Statement;
import com.yalon.norm.mapper.EntityMap;
import com.yalon.norm.mapper.EntityMapper;
import com.yalon.norm.sqlite.ddl.ConflictAlgorithm;

public class PersistencyManager {
	// TODO: although this interface requires static functions so Persistable
	// can call them, we can
	// create it as an instance so it can be extended if needed.
	// TODO: almost everything here is SQL so it's DB specific.
	protected static EntityMap entityMap = new EntityMap();
	
	protected static final Logger LOG = LoggerFactory.getLogger(PersistencyManager.class);

	public static void update(Persistable obj, ConflictAlgorithm conflictResolution) {
		Database db = DatabaseConnection.get();
		EntityMapper map = entityMap.get(obj.getClass());

		HashMap<String, Object> row = new HashMap<String, Object>();
		map.mapObjectToRow(obj, row);

		Object[] values = new Object[row.size() + 1];	// +1 is for the ID in the WHERE clause

		StringBuilder sql = new StringBuilder("UPDATE ");
		if (conflictResolution != null) {
			sql.append(" OR ");
			sql.append(ConflictAlgorithm.toString(conflictResolution));
		}

		sql.append(map.getTableName());
		sql.append(" SET");
		String sep = " ";

		Iterator<Entry<String, Object>> iter = row.entrySet().iterator();
		for (int i = 0; i < values.length - 1; ++i) {
			Entry<String, Object> entry = iter.next();
			sql.append(sep);
			sql.append(entry.getKey());
			sql.append("=?");
			values[i] = entry.getValue();
			sep = ", ";
		}
		
		sql.append(" WHERE id=?");
		LOG.debug("update entity={} sql={}", obj.getClass(), sql.toString());
		
		values[values.length - 1] = obj.getId();
		
		db.execSQL(sql.toString(), values);
	}

	public static long insert(Persistable obj, ConflictAlgorithm conflictResolution) {
		Database db = DatabaseConnection.get();
		EntityMapper map = entityMap.get(obj.getClass());

		HashMap<String, Object> row = new HashMap<String, Object>();
		map.mapObjectToRow(obj, row);

		Object[] values = new Object[row.size()];

		StringBuilder sql = new StringBuilder("INSERT INTO ");
		sql.append(map.getTableName());
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
		LOG.debug("insert entity={} sql={}", obj.getClass(), sql.toString());
		Statement stmt = db.compileStatement(sql.toString());
		try {
			for (int i = 0 ; i < values.length; ++i) {
				stmt.bindByValueType(i, values[i]);
			}
			return stmt.executeInsert();
		} finally {
			stmt.close();
		}
	}

	public static void destroy(Persistable obj) {		
		Database db = DatabaseConnection.get();
		EntityMapper map = entityMap.get(obj.getClass());
		
		StringBuilder sql = new StringBuilder("DELETE FROM ");
		sql.append(map.getTableName());
		sql.append(" WHERE id=?");
		LOG.debug("destroy entity={} sql={}", obj.getClass(), sql.toString());
		db.execSQL(sql.toString(), new Object[] { obj.getId() });
	}
	
	public static void register(Class<? extends PersistentObject> entity) {
		LOG.debug("register entity={}", entity);
		entityMap.putIfNotExists(entity);
	}
}