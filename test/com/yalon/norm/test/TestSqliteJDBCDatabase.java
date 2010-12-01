package com.yalon.norm.test;

import java.io.File;
import java.sql.SQLException;

import junit.framework.TestCase;

import com.yalon.norm.NormUniqueConstraintException;
import com.yalon.norm.Statement;
import com.yalon.norm.adapter.jdbc.SqliteJDBCDatabase;

public class TestSqliteJDBCDatabase extends TestCase {
	SqliteJDBCDatabase db;

	public TestSqliteJDBCDatabase(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		new File("unittest.db").delete();
		db = new SqliteJDBCDatabase("unittest.db");
	}

	@Override
	protected void tearDown() throws SQLException {
		db.close();
	}

	public void testInsert() throws Exception {
		db.execSQL("CREATE TABLE foo (col1 INTEGER)");
		Statement stmt = db.compileStatement("INSERT INTO foo (col1) VALUES ('1')");
		assertEquals(1, stmt.executeInsert());
		assertEquals(2, stmt.executeInsert());
	}

	public void testInsertAutoInc() throws Exception {
		db.execSQL("CREATE TABLE foo (col1 INTEGER PRIMARY KEY AUTOINCREMENT, col2 INTEGER)");
		Statement stmt = db.compileStatement("INSERT INTO foo (col1, col2) VALUES (NULL, '1')");
		assertEquals(1, stmt.executeInsert());
		assertEquals(2, stmt.executeInsert());
	}

	public void testInsertUniqueConstraint_PK() throws Exception {
		db.execSQL("CREATE TABLE foo (col1 INTEGER PRIMARY KEY, col2 INTEGER)");
		Statement stmt = db.compileStatement("INSERT INTO foo (col1, col2) VALUES ('1', '1')");
		assertEquals(1, stmt.executeInsert());
		try {
			stmt.executeInsert();
			fail();
		} catch (NormUniqueConstraintException e) {
			// Good.
		}
	}

	public void testInsertUniqueConstraint_SingleColumn() throws Exception {
		db.execSQL("CREATE TABLE foo (col1 INTEGER PRIMARY KEY AUTOINCREMENT, col2 INTEGER UNIQUE)");
		Statement stmt = db.compileStatement("INSERT INTO foo (col2) VALUES ('1')");
		assertEquals(1, stmt.executeInsert());
		try {
			stmt.executeInsert();
			fail();
		} catch (NormUniqueConstraintException e) {
			// Good.
		}
	}
	
	public void testInsertUniqueConstraint_MultipleColumns() throws Exception {
		db.execSQL("CREATE TABLE foo (col1 INTEGER, col2 INTEGER, UNIQUE (col1, col2))");
		Statement stmt = db.compileStatement("INSERT INTO foo (col1, col2) VALUES ('1', '1')");
		assertEquals(1, stmt.executeInsert());
		try {
			stmt.executeInsert();
			fail();
		} catch (NormUniqueConstraintException e) {
			// Good.
		}
	}
}