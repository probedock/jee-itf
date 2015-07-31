package io.probedock.jee.itf.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * To annotate a method to be a test
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
	/**
	 * @return Define a key to retrieve the test based on a key that does
	 * not change when a method name is refactored
	 */
	String setupKey() default "";
}
