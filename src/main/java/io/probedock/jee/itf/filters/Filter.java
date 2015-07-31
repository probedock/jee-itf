package io.probedock.jee.itf.filters;

import io.probedock.jee.itf.model.Description;

/**
 * Defining a filter allows to run only part of the tests.
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public interface Filter {
	/**
	 * Determine if a test should be run or not
	 * @param description The description to get the data
	 * @return True if the test is runnable, false otherwise
	 */
	boolean isRunnable(Description description);
}
