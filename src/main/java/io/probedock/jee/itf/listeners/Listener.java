package io.probedock.jee.itf.listeners;

import io.probedock.jee.itf.model.Description;

/**
 * Listeners allows to apply some logic at different step
 * of the test run lifecycle
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public interface Listener {
	/**
	 * Executed at the beginning of the test run process
	 */
	void testRunStart();
	
	/**
	 * Executed at the end of the test run process
	 */
	void testRunEnd();
	
	/**
	 * Executed just before the test method is called
	 * @param description The description of the test
	 */
	void testStart(Description description);
	
	/**
	 * Executed just after the test method is called
	 * @param description The description of the test
	 */
	void testEnd(Description description);
	
	/**
	 * Executed when the result is received after the
	 * test execution. In other words, this method is called
	 * after the {@link Listener#testEnd(Description) }
	 * method. Notify a test that fails.
	 * @param description The description of the test 
	 */
	void fail(Description description);
	
	/**
	 * Executed when the result is received after the
	 * test execution. In other words, this method is called
	 * after the {@link Listener#testEnd(Description) }
	 * method. Notify a test that success.
	 * @param description The description of the test 
	 */
	void success(Description description);
}
