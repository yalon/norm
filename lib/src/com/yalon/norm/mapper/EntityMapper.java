package com.yalon.norm.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yalon.norm.DataRow;
import com.yalon.norm.NormException;
import com.yalon.norm.NormSQLException;
import com.yalon.norm.annotations.Column;
import com.yalon.norm.annotations.Entity;
import com.yalon.norm.utils.Inflector;
import com.yalon.norm.utils.ReflectionUtils;
import com.yalon.norm.utils.StringUtils;

public class EntityMapper {
	public static final Logger LOG = LoggerFactory.getLogger(EntityMapper.class);
	public static final String DEFAULT_POLYMORPHIC_COLUMN = "type";

	protected EntityMapper parent;
	protected Class<?> clazz;
	protected String tableName;
	protected Entity.Polymorphic polymorphic;
	protected String polymorphicColumn;

	protected Method afterCreateMethod;

	protected Method beforeUpdateMethod;
	protected Method afterUpdateMethod;

	protected Method beforeDestroyMethod;
	protected Method afterDestroyMethod;

	protected ArrayList<Mapper> mappers;
	protected HashSet<String> columns;

	protected EntityMapper(EntityMapper parent, Class<?> clazz) {
		this.parent = parent;
		this.clazz = clazz;
		this.mappers = new ArrayList<Mapper>();
		this.columns = new HashSet<String>();
		createMap();
	}

	public Class<?> getEntityClass() {
		return clazz;
	}

	public String getTableName() {
		return tableName;
	}

	public Set<String> getColumns() {
		return columns;
	}

	@SuppressWarnings("unchecked")
	public <T> T mapRowToNewObject(DataRow row) {
		// TODO: call hooks (before/after stuff)
		Object obj = ReflectionUtils.newInstance((Class<T>) clazz);

		mapRowToObject(row, obj);

		return (T) obj;
	}

	public void mapObjectToRow(Object obj, Map<String, Object> row) {
		internalMapObjectToRow(obj, row);
		if (isPolymorphic()) {
			row.put(polymorphicColumn, obj.getClass().getName());
		}
	}

	public boolean isPolymorphic() {
		return polymorphic == Entity.Polymorphic.YES;
	}

	public String getPolymorphicInstanceClassName(DataRow row) {
		if (isPolymorphic()) {
			return row.getString(row.getColumnIndex(polymorphicColumn));
		}

		return null;
	}
	
	public Class<?> getPolymorphicInstanceClass(DataRow row) {
		String className = getPolymorphicInstanceClassName(row);
		return ReflectionUtils.classForName(className);
	}

	protected void mapRowToObject(DataRow row, Object obj) {
		// TODO: call hooks (before/after stuff)
		if (parent != null) {
			parent.mapRowToObject(row, obj);
		}

		for (Mapper mapper : mappers) {
			mapper.mapRowToObject(row, obj);
		}
	}

	public String getColumnForField(String fieldName) {
		SingleColumnMapper mapper = findSingleColumnMapper(fieldName);
		return mapper.getColumnName();
	}

	public Object mapFieldValueToColumnTypeValue(String fieldName, Object fieldValue) {
		SingleColumnMapper mapper = findSingleColumnMapper(fieldName);
		return mapper.mapFieldValueToDatabasePrimitiveValue(fieldValue);
	}

	protected SingleColumnMapper findSingleColumnMapper(String fieldName) {
		// TODO: not very efficient at the moment.
		for (Mapper mapper : mappers) {
			if (mapper instanceof SingleColumnMapper) {
				if (((SingleColumnMapper) mapper).getField().getName().equals(fieldName)) {
					return (SingleColumnMapper) mapper;
				}
			}
		}

		if (parent != null) {
			return parent.findSingleColumnMapper(fieldName);
		}

		throw new NormSQLException("field " + fieldName + " doesn't map into a single (or any) column");
	}

	protected void internalMapObjectToRow(Object obj, Map<String, Object> row) {
		// TODO: call hooks (before/after stuff)
		if (parent != null) {
			parent.mapObjectToRow(obj, row);
		}

		for (Mapper mapper : mappers) {
			mapper.mapObjectToRow(obj, row);
		}
	}

	protected void createMap() {
		Entity e = clazz.getAnnotation(Entity.class);
		tableName = e != null ? e.table() : parent.getTableName();

		// TODO: find the before/after filters.
		// for (Method m : clazz.getDeclaredMethods()) {
		// }

		buildDirectFieldMapping();

		buildPolymorphismMapping(e);

		// We do this after biuldPolymorphismMapping because we may get the
		// table name from one
		// of our parent.
		if (StringUtils.isEmpty(tableName)) {
			tableName = Inflector.tableize(clazz);
		}
	}

	protected void buildPolymorphismMapping(Entity e) {
		// See if ancestors support polymorphism.
		polymorphic = e != null ? e.polymorphic() : parent.polymorphic;
		polymorphicColumn = e != null ? e.polyColumn() : parent.polymorphicColumn;

		if (parent != null) {
			if (parent.polymorphic == Entity.Polymorphic.YES) {
				// Make sure the child is a YES or AUTO.
				if (polymorphic == Entity.Polymorphic.NO) {
					throw new NormSQLException("class " + clazz + " defined as non-polyphormic, but anscestor "
							+ parent.clazz + " is.");
				}
				polymorphic = Entity.Polymorphic.YES;

				// Make sure the column is the same.
				if (!StringUtils.isEmpty(polymorphicColumn) && !polymorphicColumn.equals(parent.polymorphicColumn)) {
					throw new NormSQLException("class " + clazz
							+ " defined as polyphormic with a different type column (" + e.polyColumn()
							+ ") than its anscestor (" + parent.polymorphicColumn + ")");
				}

				if (!StringUtils.isEmpty(tableName) && !tableName.equals(parent.tableName)) {
					throw new NormSQLException("class " + clazz + " defined as polymorphic with a different table ("
							+ tableName + ") than its anscetor (" + parent.tableName + ")");
				}
				polymorphicColumn = parent.polymorphicColumn;
				tableName = parent.tableName;
			} else { /* must be Entity.Polyphormic.NO */
				if (polymorphic == Entity.Polymorphic.YES) {
					if (StringUtils.isEmpty(polymorphicColumn)) {
						polymorphicColumn = DEFAULT_POLYMORPHIC_COLUMN;
					}
				} else {
					polymorphic = Entity.Polymorphic.NO;
				}
			}
		} else {
			if (polymorphic == Entity.Polymorphic.AUTO) {
				polymorphic = Entity.Polymorphic.NO;
			}

			if (polymorphic == Entity.Polymorphic.NO && !StringUtils.isEmpty(polymorphicColumn)) {
				throw new NormSQLException("class " + clazz + " is not polymorphic, but still you defined polyColumn");
			}
			if (polymorphic == Entity.Polymorphic.YES && StringUtils.isEmpty(e.polyColumn())) {
				polymorphicColumn = DEFAULT_POLYMORPHIC_COLUMN;
			}
		}

		// Put all of our parents columns on us (regardless of polymorphism).
		HashSet<String> parentColumns = new HashSet<String>();
		EntityMapper curParent = parent;
		while (curParent != null) {
			parentColumns.addAll(curParent.columns);
			curParent = curParent.parent;
		}

		if (polymorphic == Entity.Polymorphic.YES) {
			columns.add(polymorphicColumn);

			// Put all our columns to all of our parents as well.
			curParent = parent;
			while (curParent != null) {
				curParent.columns.addAll(columns);
				curParent = curParent.parent;
			}
		}

		columns.addAll(parentColumns);
	}

	protected void buildDirectFieldMapping() {
		for (Field f : clazz.getDeclaredFields()) {
			// TODO: support relations here.
			Column col = f.getAnnotation(Column.class);
			if (col == null) {
				continue;
			}
			String columnName = col.name();
			if (StringUtils.isEmpty(columnName)) {
				columnName = Inflector.underscore(f.getName());
			}

			if (f.getType().isEnum()) {
				mappers.add(new EnumTypeMapper(columnName, f, !col.dbToObjectOnly()));
			} else if (ReflectionUtils.isDatabasePrimitiveType(f.getType())) {
				mappers.add(new PrimitiveTypeMapper(columnName, f, !col.dbToObjectOnly()));
			} else if (ReflectionUtils.isPrimitiveArrayType(f.getType())) {
				mappers.add(new PrimitiveArrayTypeMapper(columnName, f, !col.dbToObjectOnly()));
			} else {
				throw new NormException("don't know how to map field " + f);
			}
			// TODO: allow custom mapping here, arrays of stuff, etc.

			columns.add(columnName);
		}
	}

	public String toString() {
		StringBuilder str = new StringBuilder("EntityMapper(");
		str.append("clazz=");
		str.append(clazz);
		str.append(", parent=");
		str.append(parent.clazz);
		str.append(", tableName=");
		str.append(tableName);
		str.append(", polymorphic=");
		str.append(polymorphic);
		str.append(", polymorphicColumn=");
		str.append(polymorphicColumn);
		str.append(", mappers=");
		str.append(mappers);
		str.append(")");
		return str.toString();
	}
}