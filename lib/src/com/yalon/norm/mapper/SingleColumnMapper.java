package com.yalon.norm.mapper;

import java.lang.reflect.Field;

public interface SingleColumnMapper extends Mapper {
	String getColumnName();

	Field getField();
	
	Object mapFieldValueToDatabasePrimitiveValue(Object fieldValue);
}