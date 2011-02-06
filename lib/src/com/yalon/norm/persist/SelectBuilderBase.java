package com.yalon.norm.persist;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yalon.norm.Cursor;
import com.yalon.norm.Database;
import com.yalon.norm.NormSQLException;
import com.yalon.norm.mapper.EntityMapper;
import com.yalon.norm.utils.StringUtils;

public class SelectBuilderBase<T extends Persistable, S extends SelectBuilderBase<T, ?>> {
	protected static final Logger LOG = LoggerFactory.getLogger(PersistencyManager.class);

	protected Class<T> entity;
	protected EntityMapper entityMapper;
	protected String condition;
	protected ArrayList<String> bindValues;
	protected boolean distinct;
	protected long limit;
	protected long offset;
	protected EntitySelectFilter<T> filter;

	public SelectBuilderBase(Class<T> entity, String condition) {
		this.entity = entity;
		this.entityMapper = PersistencyManager.entityMap.get(entity);
		this.condition = parseCondition(condition);
		this.bindValues = new ArrayList<String>();
		this.distinct = false;
		this.limit = -1;
		this.offset = -1;
		this.filter = null;
	}

	@SuppressWarnings("unchecked")
	public S distinct() {
		this.distinct = true;
		return (S) this;
	}

	public S limit(long limit) {
		return limit(limit, -1);
	}

	@SuppressWarnings("unchecked")
	public S limit(long limit, long offset) {
		this.limit = limit;
		this.offset = offset;
		return (S) this;
	}

	@SuppressWarnings("unchecked")
	public S bindFieldType(String fieldName, Object value) {
		bindValues.add(primitiveTypeToString(entityMapper.mapFieldValueToColumnTypeValue(fieldName, value)));
		return (S) this;
	}

	@SuppressWarnings("unchecked")
	public S bind(Object value) {
		bindValues.add(primitiveTypeToString(value));
		return (S) this;
	}

	@SuppressWarnings("unchecked")
	public S filter(EntitySelectFilter<T> filter) {
		this.filter = filter;
		return (S) this;
	}

	protected EntityCursor<T> executeQuery() {
		StringBuilder sql = new StringBuilder("SELECT ");
		if (distinct) {
			sql.append("DISTINCT ");
		}
		appendColumns(sql);
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
		LOG.debug("SelectBuilder.executeQuery sql={} bindVars={}", sql.toString(), bindValuesArray);
		Cursor cur = db.execQuerySQL(sql.toString(), bindValuesArray);
		if (filter != null) {
			return new EntityFilterCursor<T>(entity, cur, filter);
		}
		return new EntityCursor<T>(entity, cur);
	}

	protected void appendColumns(StringBuilder sql) {
		StringUtils.join(entityMapper.getColumns().iterator(), ", ", sql);
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
					for (lastMatchEndPos = matchIndex + 1; Character.isJavaIdentifierPart(str.charAt(lastMatchEndPos))
							&& lastMatchEndPos < str.length(); ++lastMatchEndPos) {
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