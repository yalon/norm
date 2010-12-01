package com.yalon.norm.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.yalon.norm.DataRow;
import com.yalon.norm.NormSQLException;
import com.yalon.norm.annotations.Column;
import com.yalon.norm.annotations.Entity;
import com.yalon.norm.utils.Inflector;
import com.yalon.norm.utils.ReflectionUtils;
import com.yalon.norm.utils.StringUtils;

public class EntityMapper {
	public static final String DEFAULT_POLYMORPHIC_COLUMN = "type";

	protected EntityMapper parent;
	protected Class<?> clazz;
	protected String tableName;
	protected Entity.Polyphormic polymorphic;
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

	public <T> T mapRowToNewObject(DataRow row) {
		// TODO: call hooks (before/after stuff)
		@SuppressWarnings("unchecked")
		T obj = ReflectionUtils.newInstance((Class<T>) clazz);

		mapRowToObject(row, obj);

		return obj;
	}

	public void mapObjectToRow(Object obj, Map<String, Object> row) {
		internalMapObjectToRow(obj, row);
		if (isPolymorphic()) {
			row.put(polymorphicColumn, obj.getClass().getName());
		}
	}

	public boolean isPolymorphic() {
		return polymorphic == Entity.Polyphormic.YES;
	}

	public String getPolymorphicInstanceClassName(DataRow row) {
		if (isPolymorphic()) {
			return row.getString(row.getColumnIndex(polymorphicColumn));
		}

		return null;
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
		tableName = e.table();

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
		polymorphic = e.polymorphic();
		polymorphicColumn = e.polyColumn();
		if (parent != null) {
			if (parent.polymorphic == Entity.Polyphormic.YES) {
				// Make sure the child is a YES or AUTO.
				if (polymorphic == Entity.Polyphormic.NO) {
					throw new NormSQLException("class " + clazz
							+ " defined as non-polyphormic, but anscestor " + parent.clazz + " is.");
				}
				polymorphic = Entity.Polyphormic.YES;

				// Make sure the column is the same.
				if (!StringUtils.isEmpty(polymorphicColumn)
						&& !polymorphicColumn.equals(parent.polymorphicColumn)) {
					throw new NormSQLException("class " + clazz
							+ " defined as polyphormic with a different type column ("
							+ e.polyColumn() + ") than its anscestor (" + parent.polymorphicColumn
							+ ")");
				}

				if (!StringUtils.isEmpty(tableName) && !tableName.equals(parent.tableName)) {
					throw new NormSQLException("class " + clazz
							+ " defined as polymorphic with a different table (" + tableName
							+ ") than its anscetor (" + parent.tableName + ")");
				}
				polymorphicColumn = parent.polymorphicColumn;
				tableName = parent.tableName;
			} else { /* must be Entity.Polyphormic.NO */
				if (polymorphic == Entity.Polyphormic.YES) {
					if (StringUtils.isEmpty(polymorphicColumn)) {
						polymorphicColumn = DEFAULT_POLYMORPHIC_COLUMN;
					}
				} else {
					polymorphic = Entity.Polyphormic.NO;
				}
			}
		} else {
			if (polymorphic == Entity.Polyphormic.AUTO) {
				polymorphic = Entity.Polyphormic.NO;
			}

			if (polymorphic == Entity.Polyphormic.NO && !StringUtils.isEmpty(polymorphicColumn)) {
				throw new NormSQLException("class " + clazz
						+ " is not polymorphic, but still you defined polyColumn");
			}
			if (polymorphic == Entity.Polyphormic.YES && StringUtils.isEmpty(e.polyColumn())) {
				polymorphicColumn = DEFAULT_POLYMORPHIC_COLUMN;
			}
		}

		if (polymorphic == Entity.Polyphormic.YES) {
			columns.add(polymorphicColumn);

			// Put all our columns to all of our parents as well.
			EntityMapper curParent = parent;
			while (curParent != null) {
				curParent.columns.addAll(columns);
				curParent = curParent.parent;
			}
		}
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
				mappers.add(new EnumTypeMapper(columnName, f));
			} else if (ReflectionUtils.isDatabasePrimitiveType(f.getType())) {
				mappers.add(new PrimitiveTypeMapper(columnName, f));
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