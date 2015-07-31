package io.probedock.jee.itf.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark an integration test to not be rollback when
 * the test is run.
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NoRollback {
}
