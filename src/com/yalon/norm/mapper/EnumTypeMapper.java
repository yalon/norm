package com.yalon.norm.mapper;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import com.yalon.norm.DataRow;
import com.yalon.norm.NormException;
import com.yalon.norm.utils.ReflectionUtils;

public class EnumTypeMapper implements Mapper {
	String columnName;
	Field field;
	Object[] values;
	
	public EnumTypeMapper(String columnName, Field field) {
		this.columnName = columnName;
		this.field = field;
		Method enumValuesMethod = ReflectionUtils.getMethod("values", field.getType());
		Object arr = ReflectionUtils.callMethod(enumValuesMethod, field.getType());
		values = new Object[Array.getLength(arr)];
		for (int i = 0; i < values.length; ++i) {
			values[i] = Array.get(arr, i);
		}
	}
	
	@Override
	public void mapRowToObject(DataRow row, Object obj) {
		int columnIndex = row.getColumnIndex(columnName);
		int enumIndex = row.getInt(columnIndex);
		ReflectionUtils.setFieldValue(field, obj, values[enumIndex]);
	}

	@Override
	public void mapObjectToRow(Object obj, Map<String, Object> row) {
		for (int i = 0; i < values.length; ++i) {
			if (values[i].equals(obj)) {
				row.put(columnName, i);
				return;
			}
		}
		throw new NormException("cannot find enum value index for enum " + obj);
	}
}
