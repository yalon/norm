package com.yalon.norm.test;

import java.io.File;

import junit.framework.TestCase;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import com.yalon.norm.Database;
import com.yalon.norm.adapter.jdbc.SqliteJDBCDatabase;
import com.yalon.norm.annotations.Column;
import com.yalon.norm.annotations.Entity;
import com.yalon.norm.persist.DatabaseConnection;
import com.yalon.norm.persist.PersistencyManager;
import com.yalon.norm.persist.PersistentObject;

public class TestPersistentObject extends TestCase {
	@Entity
	static class Foo extends PersistentObject {
		@Column
		int col1;

		@Column
		String col2;
	}

	public TestPersistentObject(String name) {
		super(name);
	}

	@Override
	protected void setUp() {
		Logger.getRootLogger().setLevel(Level.DEBUG);
		Logger.getRootLogger().addAppender(new ConsoleAppender(new SimpleLayout()));
		new File("unittest.db").delete();
		Database db = new SqliteJDBCDatabase("unittest.db");
		DatabaseConnection.open(db);

		db.execSQL("CREATE TABLE foos (id INTEGER PRIMARY KEY AUTOINCREMENT, col1 INTEGER, col2 TEXT)");
		PersistencyManager.register(Foo.class);
	}

	@Override
	protected void tearDown() {
		DatabaseConnection.close();
	}
	
	public void testInsert() {
		Foo foo = new Foo();
		foo.col1 = 42;
		foo.col2 = "this is a test";
		foo.save();
		assertTrue(foo.hasId());
		assertEquals(new Long(1), foo.getId());

		Foo foo2 = new Foo();
		foo2.col1 = 424;
		foo2.col2 = "this is a test2";
		foo2.save();
		assertEquals(new Long(2), foo2.getId());
		assertFalse(foo2.equals(foo));
	}
	
	public void testUpdate() {
		DatabaseConnection.get().beginTransaction();
		Foo foo = new Foo();
		foo.col1 = 42;
		foo.col2 = "this is a test";
		foo.save();
		DatabaseConnection.get().setTransactionSuccessful();
		DatabaseConnection.get().endTransaction();
		
		DatabaseConnection.get().beginTransaction();
		foo.col1 = 84;
		foo.save();
		DatabaseConnection.get().endTransaction();	
	}
}