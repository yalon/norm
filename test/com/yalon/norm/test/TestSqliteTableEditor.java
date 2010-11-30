package com.yalon.norm.test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import junit.framework.TestCase;

import com.yalon.norm.Cursor;
import com.yalon.norm.adapter.jdbc.JDBCDatabase;
import com.yalon.norm.sqlite.ddl.ColumnType;
import com.yalon.norm.sqlite.ddl.ConflictAlgorithm;
import com.yalon.norm.sqlite.ddl.TableBuilder;
import com.yalon.norm.sqlite.ddl.TableBuilderBase.UniqueConstraint;
import com.yalon.norm.sqlite.ddl.TableEditor;

public class TestSqliteTableEditor extends TestCase {
	Connection conn;
	JDBCDatabase db;
	TableEditor editor;


	@Override
	protected void setUp() throws Exception {
		new File("unittest.db").delete();
		Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection("jdbc:sqlite:unittest.db");
		db = new JDBCDatabase(conn);
		editor = new TableEditor(db);
	}

	@Override
	protected void tearDown() throws SQLException {
		conn.close();
	}

	public TestSqliteTableEditor(String name) {
		super(name);
	}

	public void testDrop_Exists() {
		TableBuilder t = editor.build("test");
		t.column("id", ColumnType.INTEGER);
		t.create();
		try {
			editor.drop("test");
		} catch (Exception e) {
			fail();
		}
	}

	public void testDrop_NotExist() {
		TableEditor editor = new TableEditor(db);
		try {
			editor.drop("table_that_doesnt_exist");
			fail();
		} catch (Exception e) {
			// Good.
		}
	}

	public void testDropIfExists_NotExist() {
		try {
			editor.dropIfExists("table_that_doesnt_exist");
		} catch (Exception e) {
			fail();
		}
	}

	public void testDropIfExists_Exists() {
		TableBuilder t = editor.build("test");
		t.column("id", ColumnType.INTEGER);
		t.create();
		editor.dropIfExists("test");
		try {
			db.execSQL("SELECT * FROM test");
			fail();
		} catch (Exception e) {
			// Good.
		}
	}

	public void testCreateSQL() throws Exception {
		TableBuilder t = editor.build("test");
		t.integer("id").primaryKey(ConflictAlgorithm.ABORT).notNull().desc();
		t.real("vec").defaultValue(1.5);
		t.integer("bool").defaultValue(false);
		assertEquals("CREATE TABLE test ("
				+ "id INTEGER PRIMARY KEY DESC ON CONFLICT ABORT NOT NULL"
				+ ", vec REAL DEFAULT '1.5', bool INTEGER DEFAULT 0)",
				t.toCreateSQL());
	}

	public void testCreateSQL_Constraint() {
		TableBuilder t = editor.build("test");
		t.integer("id").primaryKey();
		t.text("name");
		UniqueConstraint constraint = t.uniqueConstraint();
		constraint.column("id").desc();
		constraint.column("name");
		assertEquals(
				"CREATE TABLE test (id INTEGER PRIMARY KEY ASC, name TEXT)"
						+ " UNIQUE (id DESC, name ASC)", t.toCreateSQL());
	}

	public void testCreate() throws Exception {
		TableBuilder t = editor.build("test");
		t.integer("id").primaryKey(ConflictAlgorithm.ABORT).notNull().desc();
		t.real("vec").defaultValue(1.5);
		t.integer("bool").defaultValue(false);
		t.create();
		db.execSQL("SELECT id, vec FROM test");
	}
	
	public void testCopy() {
		TableBuilder t = editor.build("test");
		t.integer("col1");
		t.integer("col2");
		t.create();

		t = editor.build("test2");
		t.integer("col1");
		t.create();

		db.execSQL("INSERT INTO test (col1, col2) VALUES (1, 2)");
		db.execSQL("INSERT INTO test (col1, col2) VALUES (2, 4)");
		
		editor.copy("test", "test2");
		
		Cursor cur = db.execQuerySQL("SELECT col1 FROM test2");
		try {
			assertTrue(cur.moveToNext());
			assertEquals(1, cur.getInt(0));
			assertTrue(cur.moveToNext());
			assertEquals(2, cur.getInt(0));
			assertFalse(cur.moveToNext());
		} finally {
			cur.close();
		}
	}
}