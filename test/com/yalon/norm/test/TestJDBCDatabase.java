package com.yalon.norm.test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import junit.framework.TestCase;

import com.yalon.norm.Statement;
import com.yalon.norm.adapter.jdbc.JDBCDatabase;

public class TestJDBCDatabase extends TestCase {
	Connection conn;

	public TestJDBCDatabase(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		new File("unittest.db").delete();
		Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection("jdbc:sqlite:unittest.db");
	}

	@Override
	protected void tearDown() throws SQLException {
		conn.close();
	}

	public void testInsert() throws Exception {
		JDBCDatabase db = new JDBCDatabase(conn);
		db.execSQL("CREATE TABLE foo (col1 INTEGER)");
		Statement stmt = db
				.compileStatement("INSERT INTO foo (col1) VALUES ('1')");
		assertEquals(1, stmt.executeInsert());
		assertEquals(2, stmt.executeInsert());
	}
	
	public void testInsertAutoInc() throws Exception {
		JDBCDatabase db = new JDBCDatabase(conn);
		db.execSQL("CREATE TABLE foo (col1 INTEGER PRIMARY KEY, col2 INTEGER)");
		Statement stmt = db
				.compileStatement("INSERT INTO foo (col2) VALUES ('1')");
		assertEquals(1, stmt.executeInsert());
		assertEquals(2, stmt.executeInsert());
	}
	
}
