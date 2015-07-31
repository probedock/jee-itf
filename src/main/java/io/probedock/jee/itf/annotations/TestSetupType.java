package io.probedock.jee.itf.annotations;

/**
 * Define the behavior of method in the purpose to setup a test / test group
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public enum TestSetupType {
	/**
	 * Run the method before all the test method present in a test group
	 */
	BEFORE_ALL,
	
	/**
	 * Run the method before each test method present in a test group
	 */
	BEFORE_EACH_IN_MAIN_TX,

	/**
	 * Run the method before each test method present in a test group but not 
	 * in the same transaction that the test itself
	 */
	BEFORE_EACH_OUT_MAIN_TX,
	
	/**
	 * Run the method before the current test method of the test group
	 */
	BEFORE_IN_MAIN_TX,
	
	/**
	 * Run the method before the current test method of the test group but
	 * not in the same transaction than the test itself
	 */
	BEFORE_OUT_MAIN_TX,

	/**
	 * Run the method after the current test method of the test group but
	 * not in the same transaction than the test itself
	 */
	AFTER_OUT_MAIN_TX,

	/**
	 * Run the method after the current test method of the test group
	 */
	AFTER_IN_MAIN_TX,
	
	/**
	 * Run the method after each test method present in a test group but not 
	 * in the same transaction that the test itself
	 */
	AFTER_EACH_OUT_MAIN_TX,

	/**
	 * Run the method after each test method present in a test group
	 */
	AFTER_EACH_IN_MAIN_TX,
	
	/**
	 * Run the method after all the test method present in a test group
	 */
	AFTER_ALL;
}
