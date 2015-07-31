package io.probedock.jee.itf.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * To annotate a method to be part of a test at a certain 
 * phase of the test run life cycle
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestSetup {
	/**
	 * @return For BEFORE/AFTER, point a method test
	 */
	String[] refSetupKey() default "";
	
	/**
	 * Flag a method to have a setup behavior
	 */
	TestSetupType value();
	
	/**
	 * Zero based index to position the setup methods to be sure
	 * the run of setup methods are done in the specific order.
	 * 
	 * Underlying implementation use lists which provide a positioning
	 * by index.
	 * 
	 * @see List#add(int, java.lang.Object)
	 */
	int index() default -1;
}
