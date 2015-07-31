package io.probedock.jee.itf.test.utils;

import io.probedock.client.annotations.ProbeTest;
import io.probedock.jee.itf.test.utils.groups.DummyTestGroup;
import io.probedock.jee.itf.TestGroup;
import io.probedock.jee.itf.annotations.Test;
import io.probedock.jee.itf.model.Description;
import io.probedock.jee.itf.test.utils.groups.DummyTestGroupOrderedSetup;
import io.probedock.jee.itf.test.utils.groups.DummyTestGroupWithRefKeys;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Method utilities to test the Integration Test Framework
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class ItfTestHelper {
	/**
	 * Create a default test group
	 * @return The test group created
	 */
	public static TestGroup createDefaultTestGroup() {
		return new DummyTestGroup();
	}
	
	/**
	 * Create a default test group that has ref keys
	 * @return The test group created
	 */
	public static TestGroup createDefaultTestGoupWithRefKeys() {
		return new DummyTestGroupWithRefKeys();
	}
	
	/**
	 * Create a default test group that has ordered setup
	 * @return The test group created
	 */
	public static TestGroup createDefaultTestGroupOrderedSetup() {
		return new DummyTestGroupOrderedSetup();
	}
	
	/**
	 * Create a default description
	 * @return The description created
	 */
	public static Description createDefaultDescription() {
		try {
			Method m = ItfTestHelper.class.getMethod("dummyMethod", Description.class);
			return new Description("groupName", m.getAnnotation(Test.class), ItfTestHelper.class, m);
		}
		catch (NoSuchMethodException nme) {}
		catch (SecurityException se) {}

		return null;
	}
	
	/**
	 * Allow to set a static attribute with a custom value
	 * @param field The attribute to modify
	 * @param newValue The new value to set
	 * @throws Exception Any exception that occurs
	 */
	public static void setFinalStatic(Field field, Object newValue) throws Exception {
		field.setAccessible(true);

		// remove final modifier from field
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

		field.set(null, newValue);
	}		
	
	/**
	 * This method is never run. It is used only to create
	 * the description object to test the Filter that
	 * allows to run test by key, tag, ticket or name.
	 */
	@Test
	@ProbeTest(key = "dummyKey", tags = "dummyTag", tickets = "dummyTicket")
	public Description dummyMethod(Description description) {
		return description;
	}
}
