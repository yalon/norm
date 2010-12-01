package com.yalon.norm.adapter.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.yalon.norm.NormException;
import com.yalon.norm.NormSQLException;

public class SqliteJDBCDatabase extends JDBCDatabase {
	public SqliteJDBCDatabase(String dbName) {
		super(null, new SqliteSQLExceptionConverter());
		try {
			Class.forName("org.sqlite.JDBC");
			setConnection(DriverManager.getConnection("jdbc:sqlite:" + dbName));
		} catch (ClassNotFoundException e) {
			throw new NormException(e);
		} catch (SQLException e) {
			throw new NormSQLException(e);
		}
	}

	public SqliteJDBCDatabase(Connection conn) {
		super(conn, new SqliteSQLExceptionConverter());
	}
}
