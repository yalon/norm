package com.yalon.norm.persist;

public interface Persistable {
	boolean equals(Object obj);
	boolean hasId();
	Long getId();
	void setId(Long id);
}