package com.yalon.norm.adapter.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.yalon.norm.Cursor;
import com.yalon.norm.NormSQLException;

public class JDBCCursor implements Cursor {
	Statement stmt;
	ResultSet resultSet;
	boolean closeStatement;

	public JDBCCursor(Statement stmt, ResultSet resultSet, boolean closeStatement) {
		this.stmt = stmt;
		this.resultSet = resultSet;
		this.closeStatement = closeStatement;
	}

	@Override
	public int getCount() {
		try {
			int cur = resultSet.getRow();
			resultSet.last();
			int count = resultSet.getRow();
			resultSet.absolute(cur);
			return count;
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public int getPosition() {
		try {
			return resultSet.getRow() - 1;
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public boolean move(int offset) {
		try {
			return resultSet.relative(offset);
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public boolean moveToPosition(int position) {
		try {
			return resultSet.absolute(position + 1);
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public boolean moveToFirst() {
		try {
			return resultSet.first();
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public boolean moveToLast() {
		try {
			return resultSet.last();
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public boolean moveToNext() {
		try {
			return resultSet.next();
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public boolean moveToPrevious() {
		try {
			return resultSet.previous();
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public boolean isFirst() {
		try {
			return resultSet.isFirst();
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public boolean isLast() {
		try {
			return resultSet.isLast();
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public boolean isBeforeFirst() {
		try {
			return resultSet.isBeforeFirst();
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public boolean isAfterLast() {
		try {
			return resultSet.isAfterLast();
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public int getColumnIndex(String columnName) {
		try {
			int count = resultSet.getMetaData().getColumnCount();
			for (int i = 0; i < count; ++i) {
				if (getColumnName(i).equalsIgnoreCase(columnName)) {
					return i;
				}
			}
			return -1;
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
		int idx = getColumnIndex(columnName);
		if (idx == -1) {
			throw new IllegalArgumentException();
		}
		return idx;
	}

	@Override
	public String getColumnName(int columnIndex) {
		try {
			return resultSet.getMetaData().getColumnName(columnIndex + 1);
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public String[] getColumnNames() {
		try {
			int count = resultSet.getMetaData().getColumnCount();
			String[] columns = new String[count];
			for (int i = 0; i < count; ++i) {
				columns[i] = resultSet.getMetaData().getColumnName(i + 1);
			}
			return columns;
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public int getColumnCount() {
		try {
			return resultSet.getMetaData().getColumnCount();
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public boolean getBoolean(int columnIndex) {
		try {
			return resultSet.getBoolean(columnIndex + 1);
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public char getCharacter(int columnIndex) {
		try {
			// TODO: verify string length == 1
			return resultSet.getString(columnIndex + 1).charAt(0);
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public byte getByte(int columnIndex) {
		try {
			return resultSet.getByte(columnIndex + 1);
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public byte[] getBlob(int columnIndex) {
		try {
			return resultSet.getBytes(columnIndex + 1);
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public String getString(int columnIndex) {
		try {
			return resultSet.getString(columnIndex + 1);
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public short getShort(int columnIndex) {
		try {
			return resultSet.getShort(columnIndex + 1);
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public int getInt(int columnIndex) {
		try {
			return resultSet.getInt(columnIndex + 1);
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public long getLong(int columnIndex) {
		try {
			return resultSet.getLong(columnIndex + 1);
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public float getFloat(int columnIndex) {
		try {
			return resultSet.getFloat(columnIndex + 1);
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public double getDouble(int columnIndex) {
		try {
			return resultSet.getDouble(columnIndex + 1);
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public boolean isNull(int columnIndex) {
		try {
			return resultSet.getObject(columnIndex + 1) == null;
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public void deactivate() {
	}

	@Override
	public boolean requery() {
		return false;
	}

	@Override
	public void close() {
		try {
			resultSet.close();
			if (closeStatement) {
				stmt.close();
			}
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public boolean isClosed() {
		try {
			return resultSet.isClosed();
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public Object getPlatformCursor() {
		return resultSet;
	}
}
