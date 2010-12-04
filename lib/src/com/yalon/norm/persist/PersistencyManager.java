package com.yalon.norm.persist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yalon.norm.Cursor;
import com.yalon.norm.Database;
import com.yalon.norm.NormSQLException;
import com.yalon.norm.Statement;
import com.yalon.norm.mapper.EntityMap;
import com.yalon.norm.mapper.EntityMapper;
import com.yalon.norm.sqlite.ddl.ConflictAlgorithm;
import com.yalon.norm.utils.StringUtils;

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
		LOG.debug("insert entity={} sql={} values={}",
				new Object[] { obj.getClass(), sql.toString(), values });
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

	public static void destroy(Persistable obj) {
		Database db = DatabaseConnection.get();
		EntityMapper map = entityMap.get(obj.getClass());

		StringBuilder sql = new StringBuilder("DELETE FROM ");
		sql.append(map.getTableName());
		sql.append(" WHERE id=?");
		LOG.debug("destroy entity={} sql={}", obj.getClass(), sql.toString());
		db.execSQL(sql.toString(), new Object[] { obj.getId() });
	}

	public static <T> T findById(Class<T> entity, long id) {
		return findById(entity, (Long) id);
	}

	public static <T> T findById(Class<T> entity, Long id) {
		Database db = DatabaseConnection.get();
		EntityMapper map = entityMap.get(entity);

		StringBuilder sql = new StringBuilder("SELECT ");
		StringUtils.join(map.getColumns().iterator(), ", ", sql);
		sql.append(" FROM ");
		sql.append(map.getTableName());
		sql.append(" WHERE id=?");
		LOG.debug("findById entity={} id={}, sql={}", new Object[] { entity, id, sql });
		Cursor cur = db.execQuerySQL(sql.toString(), new String[] { id.toString() });
		try {
			if (cur.moveToNext()) {
				return map.<T> mapRowToNewObject(cur);
			} else {
				return null;
			}
		} finally {
			cur.close();
		}
	}

	public static <T> List<T> findAll(Class<T> entity, String condition, Object... bindArgs) {
		Database db = DatabaseConnection.get();
		EntityMapper map = entityMap.get(entity);

		StringBuilder sql = new StringBuilder("SELECT ");
		StringUtils.join(map.getColumns().iterator(), ", ", sql);
		sql.append(" FROM ");
		sql.append(map.getTableName());
		String[] strBindArgs = null;
		if (!StringUtils.isEmpty(condition)) {
			sql.append(" WHERE ");
			strBindArgs = mapAttributesToColumnsAndBindArgs(map, condition, bindArgs, sql);
		}
		LOG.debug("findById entity={} sql={} bindArgs={}",
				new Object[] { entity, sql, strBindArgs });
		Cursor cur = db.execQuerySQL(sql.toString(), strBindArgs);
		try {
			ArrayList<T> result = new ArrayList<T>();
			while (cur.moveToNext()) {
				result.add(map.<T> mapRowToNewObject(cur));
			}
			return result;
		} finally {
			cur.close();
		}
	}

	public static void register(Class<? extends PersistentObject> entity) {
		LOG.debug("register entity={}", entity);
		entityMap.putIfNotExists(entity);
	}

	protected static String[] mapAttributesToColumnsAndBindArgs(EntityMapper map, String str,
			Object[] bindArgs, StringBuilder sql) {
		String[] result = new String[bindArgs.length];
		int argIndex = 0;
		int matchIndex = str.indexOf('$');
		int lastMatchEndPos = 0;
		while (matchIndex >= 0) {
			sql.append(str.substring(lastMatchEndPos, matchIndex));
			if (matchIndex < str.length() - 1) {
				if (str.charAt(matchIndex + 1) != '$') {
					// This is an attribute.
					for (lastMatchEndPos = matchIndex + 1; Character.isJavaIdentifierPart(str
							.charAt(lastMatchEndPos)) && lastMatchEndPos < str.length(); ++lastMatchEndPos) {
					}
					String fieldName = str.substring(matchIndex + 1, lastMatchEndPos);
					String column = map.getColumnForField(fieldName);
					if (argIndex < result.length) {
						result[argIndex] = primitiveTypeToString(map
								.mapFieldValueToColumnTypeValue(fieldName, bindArgs[argIndex]));
						argIndex++;
					}
					sql.append(column);
				} else {
					// This is an escaped '$'.
					sql.append('$');
					lastMatchEndPos = matchIndex + 2;
				}
			} else {
				throw new NormSQLException("unescaped $ at the end of the string: " + str);
			}
			matchIndex = str.indexOf('$', matchIndex + 1);
		}

		if (lastMatchEndPos < str.length()) {
			sql.append(str.substring(lastMatchEndPos, str.length()));
		}

		for (; argIndex < result.length; ++argIndex) {
			result[argIndex] = primitiveTypeToString(bindArgs[argIndex]);
		}

		return result;
	}

	protected static String primitiveTypeToString(Object value) {
		if (value == null) {
			return null;
		} else if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue() ? "1" : "0";
		}

		return value.toString();
	}
}