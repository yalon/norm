package com.yalon.norm.adapter.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.yalon.norm.NormSQLException;
import com.yalon.norm.Statement;

public class JDBCPreparedStatement implements Statement {
	java.sql.PreparedStatement stmt;
	SQLExceptionConverter sqlExceptionConverter;

	public JDBCPreparedStatement(java.sql.PreparedStatement stmt,
			SQLExceptionConverter sqlExceptionConverter) {
		this.stmt = stmt;
		this.sqlExceptionConverter = sqlExceptionConverter;
	}

	@Override
	public void execute() {
		try {
			stmt.execute();
		} catch (SQLException e) {
			throw sqlExceptionConverter.convert(e);
		}
	}

	@Override
	public long executeInsert() {
		try {
			if (stmt.executeUpdate() > 0) {
				ResultSet rs = stmt.getGeneratedKeys();
				try {
					if (rs.next()) {
						return rs.getLong(1);
					} else {
						throw new NormSQLException("generatedKeys didn't return a result");
					}
				} finally {
					rs.close();
				}
			}
			return 0;
		} catch (SQLException e) {
			throw sqlExceptionConverter.convert(e);
		}
	}

	@Override
	public long simpleQueryForLong() {
		try {
			stmt.execute();
			ResultSet rs = stmt.getResultSet();
			try {
				if (rs.next()) {
					return rs.getLong(1);
				} else {
					throw new NormSQLException("query didn't return a result");
				}
			} finally {
				rs.close();
			}
		} catch (SQLException e) {
			throw sqlExceptionConverter.convert(e);
		}
	}

	@Override
	public String simpleQueryForString() {
		try {
			stmt.execute();
			ResultSet rs = stmt.getResultSet();
			try {
				if (rs.next()) {
					return rs.getString(1);
				} else {
					throw new NormSQLException("query didn't return a result");
				}
			} finally {
				rs.close();
			}
		} catch (SQLException e) {
			throw sqlExceptionConverter.convert(e);
		}
	}

	@Override
	public void bindNull(int index) {
		try {
			stmt.setNull(index - 1, stmt.getMetaData().getColumnType(index - 1));
		} catch (SQLException e) {
			throw sqlExceptionConverter.convert(e);
		}
	}

	@Override
	public void bindInt(int index, int value) {
		try {
			stmt.setInt(index - 1, value);
		} catch (SQLException e) {
			throw sqlExceptionConverter.convert(e);
		}
	}

	@Override
	public void bindLong(int index, long value) {
		try {
			stmt.setLong(index - 1, value);
		} catch (SQLException e) {
			throw sqlExceptionConverter.convert(e);
		}
	}

	@Override
	public void bindDouble(int index, double value) {
		try {
			stmt.setDouble(index - 1, value);
		} catch (SQLException e) {
			throw sqlExceptionConverter.convert(e);
		}
	}

	@Override
	public void bindString(int index, String value) {
		try {
			stmt.setString(index - 1, value);
		} catch (SQLException e) {
			throw sqlExceptionConverter.convert(e);
		}
	}

	@Override
	public void bindBlob(int index, byte[] value) {
		try {
			stmt.setBytes(index - 1, value);
		} catch (SQLException e) {
			throw sqlExceptionConverter.convert(e);
		}
	}

	@Override
	public void clearBindings() {
		try {
			stmt.clearParameters();
		} catch (SQLException e) {
			throw sqlExceptionConverter.convert(e);
		}
	}

	@Override
	public void close() {
		try {
			stmt.close();
		} catch (SQLException e) {
			throw sqlExceptionConverter.convert(e);
		}
	}
}
