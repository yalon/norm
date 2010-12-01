package com.yalon.norm.mapper;

import java.util.Map;

import com.yalon.norm.DataRow;

public interface Mapper {
	void mapRowToObject(DataRow row, Object obj);

	void mapObjectToRow(Object obj, Map<String, Object> row);
}