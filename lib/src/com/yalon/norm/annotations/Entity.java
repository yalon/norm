package com.yalon.norm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Entity {
	public enum Polyphormic {		
		AUTO,
		YES,
		NO
	}
	String table() default "";
	Polyphormic polymorphic() default Polyphormic.AUTO;
	String polyColumn() default "";
}