package com.yalon.norm.mapper;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.yalon.norm.DataRow;
import com.yalon.norm.utils.ReflectionUtils;

public class PrimitiveArrayTypeMapper extends SingleColumnMapperBase {
	public PrimitiveArrayTypeMapper(String columnName, Field field, boolean mapObjectToRow) {
		super(columnName, field, mapObjectToRow);
	}

	@Override
	public void mapRowToObject(DataRow row, Object obj) {
		int columnIndex = row.getColumnIndex(columnName);
		if (row.isNull(columnIndex)) {
			ReflectionUtils.setFieldValue(field, obj, null);
			return;
		}
		String serializedStr = row.getString(columnIndex);
		int ofs = serializedStr.indexOf(',');
		int len = Integer.decode(serializedStr.substring(0, ofs));
		Class<?> componentType = field.getType().getComponentType();
		Class<?> componentTypeClass = ReflectionUtils.primitiveToWrapper(componentType);
		Method valueOf;
		if (componentType != String.class) {
			valueOf = ReflectionUtils.findMethod("valueOf", componentTypeClass, String.class);
		} else {
			valueOf = ReflectionUtils.findMethod("valueOf", componentTypeClass, Object.class);
		}
		Object val = Array.newInstance(componentType, len);
		for (int i = 0; i < len; ++i) {
			int nextSep = ofs + 1;
			do {
				nextSep = serializedStr.indexOf(',', nextSep);
				if (nextSep + 1 < serializedStr.length()
						&& serializedStr.charAt(nextSep + 1) == ',') {
					nextSep += 2;
				} else {
					if (nextSep == -1) {
						nextSep = serializedStr.length();
					}
					break;
				}
			} while (nextSep < serializedStr.length());

			String elem = serializedStr.substring(ofs + 1, nextSep).replace(",,", ",");
			Array.set(val, i, ReflectionUtils.callMethod(valueOf, componentTypeClass, elem));
			ofs = nextSep;
		}

		ReflectionUtils.setFieldValue(field, obj, val);
	}

	@Override
	public Object mapFieldValueToDatabasePrimitiveValue(Object fieldValue) {
		if (fieldValue == null) {
			return null;
		}

		int len = Array.getLength(fieldValue);
		StringBuilder result = new StringBuilder("" + len);
		String sep = ",";
		for (int i = 0; i < len; ++i) {
			result.append(sep);
			Object elem = Array.get(fieldValue, i);
			result.append(elem.toString().replace(",", ",,"));
		}
		return result.toString();
	}
}