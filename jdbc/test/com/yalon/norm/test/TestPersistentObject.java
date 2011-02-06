package com.yalon.norm.test;

import java.io.File;
import java.util.Arrays;

import junit.framework.TestCase;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import com.yalon.norm.Cursor;
import com.yalon.norm.Database;
import com.yalon.norm.adapter.jdbc.SqliteJDBCDatabase;
import com.yalon.norm.annotations.Column;
import com.yalon.norm.annotations.Entity;
import com.yalon.norm.persist.DatabaseConnection;
import com.yalon.norm.persist.EntityCursor;
import com.yalon.norm.persist.PersistencyManager;
import com.yalon.norm.persist.PersistentObject;

public class TestPersistentObject extends TestCase {
	@Entity
	static class Foo extends PersistentObject {
		@Column
		int col1;

		@Column
		String col2;

		@Column
		long[] longArray;

		@Column
		boolean[] boolArray;
	}

	@Entity
	static class Bar extends PersistentObject {
		static enum State {
			A, B, C, D
		};

		@Column
		State state;
	}

	public TestPersistentObject(String name) {
		super(name);
	}

	@Override
	protected void setUp() {
		Logger.getRootLogger().setLevel(Level.DEBUG);
		Logger.getRootLogger().removeAllAppenders();
		Logger.getRootLogger().addAppender(new ConsoleAppender(new SimpleLayout()));
		new File("unittest.db").delete();
		Database db = new SqliteJDBCDatabase("unittest.db");
		DatabaseConnection.open(db);

		db.execSQL("CREATE TABLE foos (col1 INTEGER, col2 TEXT, long_array TEXT, bool_array TEXT)");
		db.execSQL("CREATE TABLE bars (state INTEGER)");
		PersistencyManager.register(Foo.class);
		PersistencyManager.register(Bar.class);
	}

	@Override
	protected void tearDown() {
		DatabaseConnection.close();
	}

	public void testInsert() {
		DatabaseConnection.get().beginTransaction();
		Foo foo = new Foo();
		foo.col1 = 42;
		foo.col2 = "this is a test";
		foo.save();
		assertTrue(foo.hasId());
		assertEquals(new Long(1), foo.getId());

		Foo foo2 = new Foo();
		foo2.col1 = 424;
		foo2.col2 = "this is a test2";
		foo2.longArray = new long[] { 1, 2, 3 };
		foo2.boolArray = new boolean[] { true, false, true };
		foo2.save();
		assertEquals(new Long(2), foo2.getId());
		assertFalse(foo2.equals(foo));
		DatabaseConnection.get().setTransactionSuccessful();
		DatabaseConnection.get().endTransaction();

		Cursor cur = DatabaseConnection.get().execQuerySQL("SELECT ROWID, * FROM foos");
		assertTrue(cur.moveToNext());
		assertEquals(1, cur.getLong(cur.getColumnIndex("ROWID")));
		assertEquals(42, cur.getLong(cur.getColumnIndex("col1")));
		assertEquals("this is a test", cur.getString(cur.getColumnIndex("col2")));

		assertTrue(cur.moveToNext());
		assertEquals(2, cur.getLong(cur.getColumnIndex("ROWID")));
		assertEquals(424, cur.getLong(cur.getColumnIndex("col1")));
		assertEquals("this is a test2", cur.getString(cur.getColumnIndex("col2")));
		assertEquals("3,1,2,3", cur.getString(cur.getColumnIndex("long_array")));
		assertEquals("3,true,false,true", cur.getString(cur.getColumnIndex("bool_array")));
	}

	public void testUpdate() {
		Foo foo = new Foo();
		foo.col1 = 42;
		foo.col2 = "this is a test";
		foo.save();

		Cursor cur = DatabaseConnection.get().execQuerySQL("SELECT ROWID, * FROM foos");
		assertTrue(cur.moveToNext());
		assertEquals(1, cur.getLong(cur.getColumnIndex("ROWID")));
		assertEquals(42, cur.getLong(cur.getColumnIndex("col1")));
		assertEquals("this is a test", cur.getString(cur.getColumnIndex("col2")));

		foo.col1 = 84;
		foo.save();

		cur = DatabaseConnection.get().execQuerySQL("SELECT ROWID, * FROM foos");
		assertTrue(cur.moveToNext());
		assertEquals(1, cur.getLong(cur.getColumnIndex("ROWID")));
		assertEquals(84, cur.getLong(cur.getColumnIndex("col1")));
	}

	public void testDestroy() {
		Foo foo = new Foo();
		foo.col1 = 42;
		foo.col2 = "this is a test";
		foo.save();

		Cursor cur = DatabaseConnection.get().execQuerySQL("SELECT ROWID,* FROM foos WHERE rowid=1");
		assertTrue(cur.moveToNext());

		foo.destroy();
		cur = DatabaseConnection.get().execQuerySQL("SELECT * FROM foos WHERE rowid=1");
		assertFalse(cur.moveToNext());
	}

	public void testFindById() {
		Foo foo = new Foo();
		foo.col1 = 42;
		foo.col2 = "this is a test";
		foo.longArray = new long[] { 3, 1, 4, 1, 5 };
		foo.boolArray = new boolean[] { true, false };
		foo.save();

		Foo foundFoo = PersistencyManager.findById(Foo.class, 1);
		assertEquals(foo, foundFoo);
		assertTrue(Arrays.equals(foo.longArray, foundFoo.longArray));
		assertTrue(Arrays.equals(foo.boolArray, foundFoo.boolArray));
	}

	public void testFindAll() {
		Foo foo = new Foo();
		foo.col1 = 42;
		foo.col2 = "this is a test";
		foo.save();

		EntityCursor<Foo> result = PersistencyManager.findAll(Foo.class, "$col1 = ?").bindFieldType("col1", 42)
				.execute();
		assertTrue(result.moveToNext());
		assertEquals(foo, result.getEntity());
		assertFalse(result.moveToNext());
		result.close();

		result = PersistencyManager.findAll(Foo.class, "$col1 < ? AND $col2 LIKE ?").bindFieldType("col1", 54)
				.bind("%is a%").execute();
		assertTrue(result.moveToNext());
		assertEquals(foo, result.getEntity());
		assertFalse(result.moveToNext());
		result.close();
	}

	public void testFindAllEnumIn() {
		Bar bar = new Bar();
		bar.state = Bar.State.A;
		bar.save();

		Bar bar2 = new Bar();
		bar2.state = Bar.State.B;
		bar2.save();

		Bar bar3 = new Bar();
		bar3.state = Bar.State.C;
		bar3.save();

		EntityCursor<Bar> result = PersistencyManager.findAll(Bar.class, "$state IN (?, ?)")
				.bindFieldType("state", Bar.State.A).bindFieldType("state", Bar.State.B).execute();
		assertTrue(result.moveToNext());
		assertTrue(result.moveToNext());
		assertFalse(result.moveToNext());
		result.close();
	}
}