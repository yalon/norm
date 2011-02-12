package com.yalon.norm.persist;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yalon.norm.Cursor;
import com.yalon.norm.Database;
import com.yalon.norm.NormSQLException;
import com.yalon.norm.mapper.EntityMapper;
import com.yalon.norm.utils.StringUtils;

public class SelectBuilderBase<T extends Persistable, CursorClass extends Cursor, S extends SelectBuilderBase<T, CursorClass, ?>> {
	protected static final Logger LOG = LoggerFactory.getLogger(PersistencyManager.class);

	protected Class<T> entity;
	protected EntityMapper entityMapper;
	protected String condition;
	protected ArrayList<String> bindValues;
	protected ArrayList<String> orderByColumns;
	protected boolean distinct;
	protected long limit;
	protected long offset;
	protected Database db;

	public SelectBuilderBase(Database db, Class<T> entity, String condition) {
		this.db = db;
		this.entity = entity;
		this.entityMapper = PersistencyManager.entityMap.get(entity);
		this.condition = condition == null ? "" : parseFieldReferences(condition);
		this.bindValues = new ArrayList<String>();
		this.orderByColumns = new ArrayList<String>();
		this.distinct = false;
		this.limit = -1;
		this.offset = -1;
	}

	@SuppressWarnings("unchecked")
	public S distinct() {
		this.distinct = true;
		return (S) this;
	}

	public S orderBy(String name) {
		return orderBy(name, true);
	}

	@SuppressWarnings("unchecked")
	public S orderBy(String name, boolean asc) {
		orderByColumns.add(parseFieldReferences(name) + (asc ? " ASC" : " DESC"));
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
	protected CursorClass executeQuery() {
		StringBuilder sql = new StringBuilder("SELECT ");
		if (distinct) {
			sql.append("DISTINCT ");
		}
		appendColumns(sql);
		sql.append(" FROM ");
		sql.append(entityMapper.getTableName());

		if (entityMapper.isPolymorphic()) {
			StringBuilder typeClauseBuilder = new StringBuilder("type IN (?");
			bindValues.add(entityMapper.getEntityClass().getName());
			for (Class<?> clazz : entityMapper.getChildren()) {
				typeClauseBuilder.append(", ?");
				bindValues.add(clazz.getName());
			}
			typeClauseBuilder.append(")");
			if (StringUtils.isEmpty(condition)) {
				condition = typeClauseBuilder.toString();
			} else {
				condition += " AND " + typeClauseBuilder.toString();
			}
		}

		if (!StringUtils.isEmpty(condition)) {
			sql.append(" WHERE ");
			sql.append(condition);
		}
		
		if (!orderByColumns.isEmpty()) {
			sql.append(" ORDER BY ");
			StringUtils.join(orderByColumns.iterator(), ", ", sql);
		}

		if (limit >= 0) {
			sql.append(" LIMIT ?");
			bindValues.add(primitiveTypeToString(limit));
			if (offset >= 0) {
				sql.append(", OFFSET ?");
				bindValues.add(primitiveTypeToString(offset));
			}
		}

		String[] bindValuesArray = bindValues.toArray(new String[bindValues.size()]);
		LOG.debug("SelectBuilder.executeQuery sql={} bindVars={}", sql.toString(), bindValuesArray);
		return (CursorClass) db.execQuerySQL(sql.toString(), bindValuesArray);
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

	protected String parseFieldReferences(String str) {
		StringBuilder sql = new StringBuilder();
		int matchIndex = str.indexOf('$');
		int lastMatchEndPos = 0;
		while (matchIndex >= 0) {
			sql.append(str.substring(lastMatchEndPos, matchIndex));
			if (matchIndex < str.length() - 1) {
				if (str.charAt(matchIndex + 1) != '$') {
					// This is an attribute.
					for (lastMatchEndPos = matchIndex + 1; lastMatchEndPos < str.length()
							&& Character.isJavaIdentifierPart(str.charAt(lastMatchEndPos)); ++lastMatchEndPos) {
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