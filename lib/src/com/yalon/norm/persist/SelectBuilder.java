package com.yalon.norm.persist;


public class SelectBuilder<T extends Persistable> extends SelectBuilderBase<T, SelectBuilder<T>> {
	public SelectBuilder(Class<T> entity, String condition) {
		super(entity, condition);
	}

	public EntityCursor<T> execute() {
		return executeQuery();
	}
}
