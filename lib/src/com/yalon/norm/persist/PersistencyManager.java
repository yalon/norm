package com.yalon.norm.persist;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yalon.norm.Cursor;
import com.yalon.norm.DataRow;
import com.yalon.norm.Database;
import com.yalon.norm.NormSQLException;
import com.yalon.norm.Statement;
import com.yalon.norm.mapper.EntityMap;
import com.yalon.norm.mapper.EntityMapper;
import com.yalon.norm.sqlite.ddl.ConflictAlgorithm;

public class PersistencyManager<CursorClass extends Cursor> {
	// TODO: although this interface requires static functions so Persistable
	// can call them, we can
	// create it as an instance so it can be extended if needed.
	// TODO: almost everything here is SQL so it's DB specific.
	protected static EntityMap entityMap = new EntityMap();

	protected static final Logger LOG = LoggerFactory.getLogger(PersistencyManager.class);
	
	protected Database db;
	
	public PersistencyManager(Database database) {
		this.db = database;
	}
	
	public Database getDatabase() {
		return this.db;
	}

	public void save(Persistable obj) {
		save(obj, null);
	}

	public void save(Persistable obj, ConflictAlgorithm conflictResolution) {
		if (obj.hasId()) {
			update(obj, conflictResolution);
		} else {
			obj.setId(insert(obj, conflictResolution));
		}
	}

	public void update(Persistable obj, ConflictAlgorithm conflictResolution) {
		EntityMapper map = entityMap.get(obj.getClass());

		HashMap<String, Object> row = new HashMap<String, Object>();
		map.mapObjectToRow(obj, row);

		Object[] values = new Object[row.size() + 1]; // +1 is for the ID in the
														// WHERE clause

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

		sql.append(" WHERE rowid=?");
		LOG.debug("update entity={} sql={}", obj.getClass(), sql.toString());

		values[values.length - 1] = obj.getId();

		db.execSQL(sql.toString(), values);
	}

	public long insert(Persistable obj, ConflictAlgorithm conflictResolution) {
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
		LOG.debug("insert entity={} sql={} values={}", new Object[] { obj.getClass(), sql.toString(), values });
		Statement stmt = db.compileStatement(sql.toString());
		try {
			for (int i = 0; i < values.length; ++i) {
				stmt.bindByValueType(i, values[i]);
			}
			return stmt.executeInsert();
		} finally {
			stmt.close();
		}
	}

	public void destroy(Persistable obj) {
		if (!obj.hasId()) {
			// TODO: exceptions.
			throw new NormSQLException("object " + this + " does not have an ID.");
		}

		EntityMapper map = entityMap.get(obj.getClass());

		StringBuilder sql = new StringBuilder("DELETE FROM ");
		sql.append(map.getTableName());
		sql.append(" WHERE rowid=?");
		LOG.debug("destroy entity={} sql={}", obj.getClass(), sql.toString());
		db.execSQL(sql.toString(), new Object[] { obj.getId() });
	}

	public <T extends Persistable> T findById(Class<T> entity, long id) {
		return findById(entity, (Long) id);
	}

	public <T extends Persistable> T findById(Class<T> entity, Long id) {
		EntityCursor<T> cursor = new EntityCursor<T>(entity, findAll(entity, "rowid=?").bind(id).execute());
		try {
			if (cursor.moveToNext()) {
				return cursor.getEntity();
			} else {
				return null;
			}
		} finally {
			cursor.close();
		}
	}

	public <T extends Persistable> SelectBuilder<T, CursorClass> findAll(Class<T> entity) {
		return findAll(entity, "");
	}

	public <T extends Persistable> SelectBuilder<T, CursorClass> findAll(Class<T> entity, String condition) {
		return new SelectBuilder<T, CursorClass>(db, entity, condition);
	}

	public <T extends Persistable> SelectCountBuilder<T, CursorClass> countAll(Class<T> entity) {
		return new SelectCountBuilder<T, CursorClass>(db, entity, "");
	}

	public <T extends Persistable> SelectCountBuilder<T, CursorClass> countAll(Class<T> entity, String condition) {
		return new SelectCountBuilder<T, CursorClass>(db, entity, condition);
	}

	public static <T extends Persistable> T mapRowToNewObject(Class<T> entityClass, DataRow row) {
		EntityMapper entityMapper = entityMap.get(entityClass);
		if (entityMapper.isPolymorphic()) {
			Class<?> clazz = entityMapper.getPolymorphicInstanceClass(row);
			return PersistencyManager.entityMap.get(clazz).mapRowToNewObject(row);
		}

		return entityMapper.mapRowToNewObject(row);
	}

	public static void register(Class<? extends PersistentObject> entity) {
		LOG.debug("register entity={}", entity);
		entityMap.putIfNotExists(entity);
	}
}