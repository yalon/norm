package com.yalon.norm.sqlite.ddl;

import com.yalon.norm.Database;
import com.yalon.norm.utils.DatabaseUtils;

public abstract class TableBuilderBase {
	public static class Column {
		String name;
		ColumnType type;
		Integer length;
		Integer precision;

		boolean notNull;
		ConflictAlgorithm notNullConflictAlgorithm;

		boolean primaryKey;
		boolean primaryKeyAsc;
		boolean primaryKeyAutoIncrement;
		ConflictAlgorithm primaryKeyConflictAlgorithm;

		boolean unique;
		ConflictAlgorithm uniqueConflictAlgorithm;

		Object defaultValue;

		String collate;

		public Column(String name, ColumnType type) {
			this.name = name;
			this.type = type;
			this.notNull = false;
			this.notNullConflictAlgorithm = null;
			this.primaryKey = false;
			this.primaryKeyAutoIncrement = false;
			this.primaryKeyConflictAlgorithm = null;
			this.unique = false;
			this.uniqueConflictAlgorithm = null;
			this.defaultValue = null;
			this.collate = null;
		}

		public Column notNull() {
			return notNull(null);
		}

		public Column notNull(ConflictAlgorithm alg) {
			notNull = true;
			notNullConflictAlgorithm = alg;
			return this;
		}

		public Column primaryKey() {
			return primaryKey(null);
		}

		public Column primaryKey(ConflictAlgorithm alg) {
			primaryKey = true;
			primaryKeyAsc = true;
			primaryKeyConflictAlgorithm = alg;
			return this;
		}

		public Column asc() {
			primaryKeyAsc = true;
			return this;
		}

		public Column desc() {
			primaryKeyAsc = false;
			return this;
		}

		public Column autoIncrement() {
			primaryKeyAutoIncrement = true;
			return this;
		}

		public Column unique() {
			return unique(null);
		}

		public Column unique(ConflictAlgorithm alg) {
			unique = true;
			uniqueConflictAlgorithm = alg;
			return this;
		}

		public Column length(int length) {
			this.length = length;
			return this;
		}

		public Column precision(int precision) {
			this.precision = precision;
			return this;
		}

		public Column lengthAndPrecision(int length, int precision) {
			this.length = length;
			this.precision = precision;
			return this;
		}

		public Column defaultValue(Object obj) {
			this.defaultValue = obj;
			return this;
		}

		public Column collate(String collate) {
			this.collate = collate;
			return this;
		}

		public String toString() {
			StringBuilder sql = new StringBuilder(name);
			sql.append(" ");
			sql.append(ColumnType.toString(type));
			if (primaryKey) {
				sql.append(" PRIMARY KEY");
				sql.append(primaryKeyAsc ? " ASC" : " DESC");
				appendConflictAlgorithm(sql, primaryKeyConflictAlgorithm);

				if (primaryKeyAutoIncrement) {
					sql.append(" AUTOINCREMENT");
				}
			}
			if (notNull) {
				sql.append(" NOT NULL");
				appendConflictAlgorithm(sql, notNullConflictAlgorithm);
			}
			if (unique) {
				sql.append(" UNIQUE");
				appendConflictAlgorithm(sql, uniqueConflictAlgorithm);
			}
			if (defaultValue != null) {
				sql.append(" DEFAULT ");
				DatabaseUtils.appendValueToSql(sql, defaultValue);
			}
			if (collate != null) {
				sql.append(" COLLATE ");
				sql.append(collate);
			}
			return sql.toString();
		}

		private void appendConflictAlgorithm(StringBuilder sql,
				ConflictAlgorithm alg) {
			if (alg != null) {
				sql.append(" ON CONFLICT ");
				sql.append(ConflictAlgorithm.toString(alg));
			}
		}
	}

	public class UniqueConstraint extends ConstraintBase {
		ConflictAlgorithm uniqueConflictAlgorithm;

		public UniqueConstraint(String name, ConflictAlgorithm alg) {
			super(null, name);
			this.uniqueConflictAlgorithm = alg;
		}

		public String toString() {
			StringBuilder sql = new StringBuilder();
			if (name != null) {
				sql.append("CONSTRAINT ");
				sql.append(getName());
				sql.append(" UNIQUE");
			} else {
				sql.append("UNIQUE");
			}

			appendColumnsSQL(sql);

			// TODO: same code like appendConflictAlgorithm
			if (uniqueConflictAlgorithm != null) {
				sql.append(" ON CONFLICT ");
				sql.append(ConflictAlgorithm.toString(uniqueConflictAlgorithm));
			}

			return sql.toString();
		}
	}

	Database db;
	String name;

	public TableBuilderBase(Database db, String name) {
		this.db = db;
		this.name = name;
	}
}