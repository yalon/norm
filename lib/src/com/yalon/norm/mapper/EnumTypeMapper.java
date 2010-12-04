package com.yalon.norm.mapper;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yalon.norm.DataRow;
import com.yalon.norm.NormException;
import com.yalon.norm.persist.PersistencyManager;
import com.yalon.norm.utils.ReflectionUtils;
import com.yalon.norm.utils.StringUtils;

public class EnumTypeMapper implements SingleColumnMapper {
	protected static final Logger LOG = LoggerFactory.getLogger(PersistencyManager.class);

	String columnName;
	Field field;
	Object[] values;
	boolean mapObjectToRow;

	public EnumTypeMapper(String columnName, Field field, boolean mapObjectToRow) {
		LOG.debug("EnumTypeMapper columnName={} field={}", columnName, field);
		this.columnName = columnName;
		this.field = field;
		this.mapObjectToRow = mapObjectToRow;
		LOG.debug("EnumTypeMapper type methods={}", field.getType().getDeclaredMethods());
		Method enumValuesMethod = ReflectionUtils.findMethod("values", field.getType());
		Object arr = ReflectionUtils.callMethod(enumValuesMethod, field.getType());
		values = new Object[Array.getLength(arr)];
		for (int i = 0; i < values.length; ++i) {
			values[i] = Array.get(arr, i);
		}
	}

	@Override
	public String getColumnName() {
		return columnName;
	}

	@Override
	public Field getField() {
		return field;
	}

	@Override
	public void mapRowToObject(DataRow row, Object obj) {
		int columnIndex = row.getColumnIndex(columnName);
		int enumIndex = row.getInt(columnIndex);
		ReflectionUtils.setFieldValue(field, obj, values[enumIndex]);
	}

	@Override
	public void mapObjectToRow(Object obj, Map<String, Object> row) {
		if (!mapObjectToRow) {
			return;
		}

		row.put(columnName,
				mapFieldValueToDatabasePrimitiveValue(ReflectionUtils.getFieldValue(field, obj)));
	}

	@Override
	public Object mapFieldValueToDatabasePrimitiveValue(Object value) {
		for (int i = 0; i < values.length; ++i) {
			if (values[i].equals(value)) {
				return i;
			}
		}
		throw new NormException("cannot find enum value index for enum " + value);
	}

	public String toString() {
		return "EnumTypeMapper(columnName=" + columnName + ", field=" + field + ", values=("
				+ StringUtils.join(values, ", ") + "), mapObjectToRow=" + mapObjectToRow + ")";
	}
}