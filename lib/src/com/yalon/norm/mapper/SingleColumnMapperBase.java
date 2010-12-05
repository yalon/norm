package com.yalon.norm.mapper;

import java.lang.reflect.Field;
import java.util.Map;

import com.yalon.norm.utils.ReflectionUtils;

public abstract class SingleColumnMapperBase implements SingleColumnMapper {
	protected String columnName;
	protected Field field;
	protected boolean mapObjectToRow;

	public SingleColumnMapperBase(String columnName, Field field, boolean mapObjectToRow) {
		this.columnName = columnName;
		this.field = field;
		this.mapObjectToRow = mapObjectToRow;
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
	public void mapObjectToRow(Object obj, Map<String, Object> row) {
		if (!mapObjectToRow) {
			return;
		}

		row.put(columnName,
				mapFieldValueToDatabasePrimitiveValue(ReflectionUtils.getFieldValue(field, obj)));
	}
}