package com.yalon.norm.mapper;

import java.lang.reflect.Field;
import java.util.Map;

import com.yalon.norm.DataRow;
import com.yalon.norm.NormException;
import com.yalon.norm.NormSQLException;
import com.yalon.norm.utils.ReflectionUtils;

public class PrimitiveTypeMapper implements Mapper {
	String columnName;
	Field field;
	boolean mapObjectToRow;

	public PrimitiveTypeMapper(String columnName, Field field, boolean mapObjectToRow) {
		this.columnName = columnName;
		this.field = field;
		this.mapObjectToRow = mapObjectToRow;
	}

	@Override
	public void mapRowToObject(DataRow row, Object obj) {
		int columnIndex = row.getColumnIndex(columnName);
		if (columnIndex < 0) {
			throw new NormSQLException("column " + columnName
					+ " is missing from the SQL query for field " + field);
		}
		if (row.isNull(columnIndex) && field.getType().isPrimitive()) {
			throw new NormSQLException("field " + field + " is primitive, but got NULL column "
					+ columnName + " for it");
		}

		Class<?> fieldType = field.getType();
		Object value;
		if (row.isNull(columnIndex)) {
			value = null;
		} else if (fieldType == Boolean.class || fieldType == Boolean.TYPE) {
			value = row.getBoolean(columnIndex);
		} else if (fieldType == Short.class || fieldType == Short.TYPE) {
			value = row.getShort(columnIndex);
		} else if (fieldType == Integer.class || fieldType == Integer.TYPE) {
			value = row.getInt(columnIndex);
		} else if (fieldType == Long.class || fieldType == Long.TYPE) {
			value = row.getLong(columnIndex);
		} else if (fieldType == Float.class || fieldType == Float.TYPE) {
			value = row.getFloat(columnIndex);
		} else if (fieldType == Double.class || fieldType == Double.TYPE) {
			value = row.getDouble(columnIndex);
		} else if (fieldType == Character.class || fieldType == Character.TYPE) {
			value = row.getCharacter(columnIndex);
		} else if (fieldType == Byte.class || fieldType == Byte.TYPE) {
			value = row.getByte(columnIndex);
		} else if (fieldType == String.class) {
			value = row.getString(columnIndex);
		} else if (fieldType == byte[].class) {
			value = row.getBlob(columnIndex);
		} else {
			throw new NormException("field " + field + " isn't of primitive type");
		}

		ReflectionUtils.setFieldValue(field, obj, value);
	}

	@Override
	public void mapObjectToRow(Object obj, Map<String, Object> row) {
		if (!mapObjectToRow) {
			return;
		}
		Object value = ReflectionUtils.getFieldValue(field, obj);
		row.put(columnName, value);
	}

	public String toString() {
		return "PrimitiveTypeMapper(columnName=" + columnName + ", field=" + field
				+ ", mapObjectToRow=" + mapObjectToRow + ")";
	}
}