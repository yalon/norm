package com.yalon.norm.test;

import junit.framework.TestCase;

import com.yalon.norm.annotations.Column;
import com.yalon.norm.annotations.Entity;
import com.yalon.norm.mapper.EntityMap;

public class TestEntityMapper extends TestCase {
	@Entity(polymorphic = Entity.Polyphormic.YES)
	static class Foo {
		@Column
		protected int col1;
		
		@Column
		protected long[] longArray;
	}

	@Entity
	static class Foo2 extends Foo {
		@Column
		protected int col2;
	}

	public TestEntityMapper(String name) {
		super(name);
	}

	public void testSimpleMapping() throws Exception {
		EntityMap entityMap = new EntityMap();
		entityMap.putIfNotExists(Foo2.class);
		System.out.println(entityMap.get(Foo2.class));
	}
}