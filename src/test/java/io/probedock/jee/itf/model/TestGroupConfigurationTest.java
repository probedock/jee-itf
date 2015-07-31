package io.probedock.jee.itf.model;

import io.probedock.jee.itf.TestGroup;
import io.probedock.jee.itf.test.utils.ItfTestHelper;
import io.probedock.client.annotations.ProbeTest;
import io.probedock.client.annotations.ProbeTestClass;
import java.lang.reflect.Method;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for {@link TestGroupConfiguration}
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
@ProbeTestClass(tags = "test-group-configuration")
public class TestGroupConfigurationTest {

	Description description;
	TestGroup testGroup;
	TestGroupDefinition testGroupDefinition;
	
	@Before
	public void setup() {
		testGroup = ItfTestHelper.createDefaultTestGroup();
		testGroupDefinition = new TestGroupDefinition(testGroup, new Random());

		try {
			Method m = testGroup.getClass().getMethod("testMethod", Description.class);
			io.probedock.jee.itf.annotations.Test testAnnotation = m.getAnnotation(io.probedock.jee.itf.annotations.Test.class);
			description = new Description(testGroup.getClass().getName(), testAnnotation, testGroup.getClass(), m);
		}
		catch (NoSuchMethodException nme) {}
		catch (SecurityException se) {}
	}
	
	@Test
	@ProbeTest(key = "09d8e9c18177")
	public void theTestGroupNameShouldBeTheClassName() {
		assertEquals("The name of the group should be the complete class name itself", testGroupDefinition.getName(), testGroup.getClass().getCanonicalName());
	}
	
	@Test
	@ProbeTest(key = "0256374e537e")
	public void gettingTheTestGroupFromTheGroupShouldReturnItself() {
		assertEquals("Getting the test group should return itself", testGroupDefinition.getTestGroup(), testGroup);
	}
	
	@Test
	@ProbeTest(key = "282de281a828")
	public void twoTestsMethodShouldBeReturnedWhenTwoArePresent() {
		assertEquals("Two test methods should be present", testGroupDefinition.getTestMethods().size(), 2);
	}
	
	@Test
	@ProbeTest(key = "9f918f86ae0e")
	public void oneBeforeAllMethodShouldBeReturnedWhenOnlyOneIsPresent() {
		assertEquals("One before all method should be present", testGroupDefinition.getBeforeAll().size(), 1);
	}

	@Test
	@ProbeTest(key = "46b35f45dc80")
	public void oneAfterAllMethodShouldBeReturnedWhenOnlyOneIsPresent() {
		assertEquals("One after all method should be present", testGroupDefinition.getAfterAll().size(), 1);
	}

	@Test
	@ProbeTest(key = "08fde925b7b7")
	public void oneBeforeEachOutTxMethodShouldBeReturnedWhenOnlyOneIsPresent() {
		assertEquals("One before each (out tx) method should be present", testGroupDefinition.getBeforeEachOutMainTx().size(), 1);
	}

	@Test
	@ProbeTest(key = "9b7ce662ea92")
	public void oneAfterEachOutTxMethodShouldBeReturnedWhenOnlyOneIsPresent() {
		assertEquals("One after each (out tx) method should be present", testGroupDefinition.getAfterEachOutMainTx().size(), 1);
	}

	@Test
	@ProbeTest(key = "03b4d52a62bc")
	public void oneBeforeEachInTxMethodShouldBeReturnedWhenOnlyOneIsPresent() {
		assertEquals("One before each (in tx) method should be present", testGroupDefinition.getBeforeEachInMainTx().size(), 1);
	}

	@Test
	@ProbeTest(key = "5b424873353c")
	public void oneAfterEachInTxMethodShouldBeReturnedWhenOnlyOneIsPresent() {
		assertEquals("One after each (in tx) method should be present", testGroupDefinition.getAfterEachInMainTx().size(), 1);
	}

	@Test
	@ProbeTest(key = "cd249a568606")
	public void oneBeforeOutTxMethodShouldBeReturnedWhenOnlyOneIsPresent() {
		assertEquals("One before (out tx) method should be present", testGroupDefinition.getBeforeOutMainTx().size(), 1);
	}

	@Test
	@ProbeTest(key = "d26f56325b27")
	public void oneAfterOutTxMethodShouldBeReturnedWhenOnlyOneIsPresent() {
		assertEquals("One after (out tx) method should be present", testGroupDefinition.getAfterOutMainTx().size(), 1);
	}

	@Test
	@ProbeTest(key = "a0dc1ad1a945")
	public void oneBeforeInTxMethodShouldBeReturnedWhenOnlyOneIsPresent() {
		assertEquals("One before (in tx) method should be present", testGroupDefinition.getBeforeInMainTx().size(), 1);
	}

	@Test
	@ProbeTest(key = "df0430b2a632")
	public void oneAfterInTxMethodShouldBeReturnedWhenOnlyOneIsPresent() {
		assertEquals("One after (in tx) method should be present", testGroupDefinition.getAfterInMainTx().size(), 1);
	}	
	
	@Test
	@ProbeTest(key = "e1118e5e00b9")
	public void oneBeforeOutTxMethodShouldBeReturnedWhenOnlyOneIsPresentForSpecificMethod() {
		assertEquals("One before (out tx) method should be present for specific method", testGroupDefinition.getBeforeOutMainTx(description).size(), 1);
	}

	@Test
	@ProbeTest(key = "1fb0f4c5bd21")
	public void oneAfterOutTxMethodShouldBeReturnedWhenOnlyOneIsPresentForSpecificMethod() {
		assertEquals("One after (out tx) method should be present for specific method", testGroupDefinition.getAfterOutMainTx(description).size(), 1);
	}

	@Test
	@ProbeTest(key = "c43abad93409")
	public void oneBeforeInTxMethodShouldBeReturnedWhenOnlyOneIsPresentForSpecificMethod() {
		assertEquals("One before (in tx) method should be present for specific method", testGroupDefinition.getBeforeInMainTx(description).size(), 1);
	}

	@Test
	@ProbeTest(key = "3d1446ac4ca5")
	public void oneAfterInTxMethodShouldBeReturnedWhenOnlyOneIsPresentForSpecificMethod() {
		assertEquals("One after (in tx) method should be present for specific method", testGroupDefinition.getAfterInMainTx(description).size(), 1);
	}
}
