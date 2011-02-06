package com.yalon.norm.test;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import junit.framework.TestCase;

import com.yalon.norm.EntityDAO;
import com.yalon.norm.adapter.jdbc.SqliteJDBCDatabase;
import com.yalon.norm.annotations.Column;
import com.yalon.norm.annotations.Entity;
import com.yalon.norm.mapper.EntityMap;
import com.yalon.norm.sqlite.ddl.TableBuilder;
import com.yalon.norm.sqlite.ddl.TableEditor;

public class TestEntityDAO extends TestCase {
	@Entity(polymorphic = Entity.Polymorphic.YES)
	static class Foo {
		@Column
		protected int col1;

		protected Foo() {
		}
		
		public Foo(int col1) {
			this.col1 = col1;
		}

		public String toString() {
			return "Foo(col1=" + col1 + ")";
		}
	}

	@Entity
	static class Foo2 extends Foo {
		@Column
		protected int col2;

		protected Foo2() {
			super();
		}
		
		public Foo2(int col1, int col2) {
			super(col1);
			this.col2 = col2;
		}

		public String toString() {
			return "Foo2(col1=" + col1 + ", col2=" + col2 + ")";
		}
	}

	SqliteJDBCDatabase db;
	TableEditor editor;
	EntityMap entityMap;

	@Override
	protected void setUp() throws Exception {
		new File("unittest.db").delete();
		db = new SqliteJDBCDatabase("unittest.db");
		editor = new TableEditor(db);
		entityMap = new EntityMap();
	}

	@Override
	protected void tearDown() throws SQLException {
		db.close();
	}

	public void testInsert() {
		TableBuilder t = editor.build("foos");
		t.integer("col1");
		t.integer("col2");
		t.text("type");
		t.create();

		entityMap.putIfNotExists(Foo.class);	// Not really needed, will get mapped implicitly due to Foo2.
		entityMap.putIfNotExists(Foo2.class);
		
		EntityDAO<Foo2> foo2DAO = new EntityDAO<Foo2>(db, entityMap, Foo2.class);
		foo2DAO.insert(new Foo2(2, 42));

		EntityDAO<Foo> fooDAO = new EntityDAO<Foo>(db, entityMap, Foo.class);
		List<Foo> objs = fooDAO.findAll();
		assertEquals(1, objs.size());
		assertTrue(objs.get(0) instanceof Foo2);
		Foo2 foo2 = (Foo2) objs.get(0);
		assertEquals(2, foo2.col1);
		assertEquals(42, foo2.col2);
	}
}
