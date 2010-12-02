package com.yalon.norm.persist;

public interface Persistable {

	public abstract boolean equals(Object obj);

	public abstract boolean hasId();

	public abstract Long getId();

}