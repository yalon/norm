package com.yalon.norm.adapter.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import com.yalon.norm.Cursor;
import com.yalon.norm.Database;
import com.yalon.norm.NormSQLException;
import com.yalon.norm.Statement;

public class JDBCDatabase implements Database {
	private Connection conn;
	private boolean successful;

	public JDBCDatabase(Connection conn) {
		this.conn = conn;
		this.successful = false;
	}

	@Override
	public void beginTransaction() {
		try {
			conn.setAutoCommit(false);
			successful = false;
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public void endTransaction() {
		try {
			if (successful) {
				conn.commit();
			} else {
				conn.rollback();
			}
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public void setTransactionSuccessful() {
		successful = true;
	}

	@Override
	public boolean inTransaction() {
		try {
			return conn.getAutoCommit() == false;
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public Statement compileStatement(String sql) {
		try {
			return new JDBCPreparedStatement(conn.prepareStatement(sql));
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public void execSQL(String sql) {
		try {
			java.sql.Statement stmt = conn.createStatement();
			try {
				stmt.execute(sql);
			} finally {
				stmt.close();
			}
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public void execSQL(String sql, Object[] bindArgs) {
		try {
			java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
			try {
				for (int i = 0; i < bindArgs.length; ++i) {
					stmt.setObject(i + 1, bindArgs[i]);
				}
				stmt.execute();
			} finally {
				stmt.close();
			}
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public Cursor execQuerySQL(String sql) {
		try {
			java.sql.Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			return new JDBCCursor(stmt, rs, true);
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public Cursor execQuerySQL(String sql, String[] bindArgs) {
		try {
			java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
			for (int i = 0; i < bindArgs.length; ++i) {
				stmt.setString(i + 1, bindArgs[i]);
			}
			ResultSet rs = stmt.executeQuery();
			return new JDBCCursor(stmt, rs, true);
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public boolean isReadOnly() {
		try {
			return conn.isReadOnly();
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public boolean isOpen() {
		try {
			return !conn.isClosed();
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	@Override
	public void setLocale(Locale locale) {
	}

	public Connection getConnection() {
		return conn;
	}
}
