package com.yalon.norm.test;

import junit.framework.TestCase;

import com.yalon.norm.annotations.Column;
import com.yalon.norm.annotations.Entity;
import com.yalon.norm.mapper.EntityMap;
import com.yalon.norm.mapper.EntityMapper;

public class TestEntityMapper extends TestCase {
	@Entity(polymorphic = Entity.Polyphormic.YES)
	static class Foo {
		@Column
		private int col1;
	}

	@Entity
	static class Foo2 extends Foo {
		@Column
		private int col2;
	}

	public TestEntityMapper(String name) {
		super(name);
	}

	public void testSimpleMapping() throws Exception {
		EntityMap entityMap = new EntityMap();
		entityMap.put(Foo2.class);
		System.out.println(entityMap.get(Foo2.class));
	}
}
