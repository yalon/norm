package com.yalon.norm.persist;

public interface EntitySelectFilter<T extends Persistable> {
	boolean test(T obj);
}
