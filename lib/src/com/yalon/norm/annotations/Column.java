package com.yalon.norm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
	/**
	 * Name of the column in the database. Auto computed from the field name if empty. 
	 */
	String name() default "";
	
	/**
	 * Alias when selecting the column from the database (e.g. x AS my_x).
	 * Used to alias rowid to _id so Android's default adapters will work with PersistentObject without a hitch. 
	 */
	String nameAlias() default "";
	boolean dbToObjectOnly() default false;
}