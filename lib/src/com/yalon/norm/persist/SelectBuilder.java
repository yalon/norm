package com.yalon.norm.persist;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yalon.norm.Cursor;
import com.yalon.norm.Database;
import com.yalon.norm.NormSQLException;
import com.yalon.norm.mapper.EntityMapper;
import com.yalon.norm.utils.StringUtils;

public class SelectBuilder<T extends Persistable> {
	protected static final Logger LOG = LoggerFactory.getLogger(PersistencyManager.class);

	Class<T> entity;
	EntityMapper entityMapper;
	String condition;
	ArrayList<String> bindValues;
	boolean distinct;
	long limit;
	long offset;

	public SelectBuilder(Class<T> entity, String condition) {
		this.entity = entity;
		this.entityMapper = PersistencyManager.entityMap.get(entity);
		this.condition = parseCondition(condition);
		this.bindValues = new ArrayList<String>();
		this.distinct = false;
		this.limit = -1;
		this.offset = -1;
	}

	public SelectBuilder<T> distinct() {
		this.distinct = true;
		return this;
	}

	public SelectBuilder<T> limit(long limit) {
		return limit(limit, -1);
	}

	public SelectBuilder<T> limit(long limit, long offset) {
		this.limit = limit;
		this.offset = offset;
		return this;
	}

	public SelectBuilder<T> bindFieldType(String fieldName, Object value) {
		bindValues.add(primitiveTypeToString(entityMapper.mapFieldValueToColumnTypeValue(fieldName,
				value)));
		return this;
	}

	public SelectBuilder<T> bind(Object value) {
		bindValues.add(primitiveTypeToString(value));
		return this;
	}

	public List<T> execute() {
		StringBuilder sql = new StringBuilder("SELECT ");
		if (distinct) {
			sql.append("DISTINCT ");
		}
		StringUtils.join(entityMapper.getColumns().iterator(), ", ", sql);
		sql.append(" FROM ");
		sql.append(entityMapper.getTableName());
		if (!StringUtils.isEmpty(condition)) {
			sql.append(" WHERE ");
			sql.append(condition);
		}

		if (limit >= 0) {
			sql.append(" LIMIT ?");
			bindValues.add(primitiveTypeToString(limit));
			if (offset >= 0) {
				sql.append(", OFFSET ?");
				bindValues.add(primitiveTypeToString(offset));
			}
		}

		Database db = DatabaseConnection.get();
		String[] bindValuesArray = bindValues.toArray(new String[bindValues.size()]);
		LOG.debug("SelectBuilder.execute sql={} bindVars={}", sql.toString(), bindValuesArray);
		Cursor cur = db.execQuerySQL(sql.toString(), bindValuesArray);
		try {
			ArrayList<T> result = new ArrayList<T>();
			while (cur.moveToNext()) {
				result.add(entityMapper.<T> mapRowToNewObject(cur));
			}
			return result;
		} finally {
			cur.close();
		}
	}

	protected static String primitiveTypeToString(Object value) {
		if (value == null) {
			return null;
		} else if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue() ? "1" : "0";
		}

		return value.toString();
	}

	protected String parseCondition(String str) {
		StringBuilder sql = new StringBuilder();
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
					String column = entityMapper.getColumnForField(fieldName);
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

		return sql.toString();
	}

}
