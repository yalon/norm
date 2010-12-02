package com.yalon.norm.android.test;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;

import com.yalon.norm.NormUniqueConstraintException;
import com.yalon.norm.Statement;
import com.yalon.norm.adapter.android.AndroidDatabase;

public class TestAndroidDatabase extends AndroidTestCase {
	static class DatabaseOpenHelper extends SQLiteOpenHelper {
		public DatabaseOpenHelper(Context context, String name) {
			super(context, name, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}

	DatabaseOpenHelper dbOpenHelper;
	AndroidDatabase db;

	public void setUp() {
		getContext().getDatabasePath("unittest.db").delete();
		dbOpenHelper = new DatabaseOpenHelper(getContext(), "unittest.db");
		db = new AndroidDatabase(dbOpenHelper.getWritableDatabase());
	}

	@Override
	protected void tearDown() throws SQLException {
		db.close();
		dbOpenHelper.close();
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
