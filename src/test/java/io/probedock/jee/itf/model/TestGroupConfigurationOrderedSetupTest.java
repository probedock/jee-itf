package io.probedock.jee.itf.model;

import io.probedock.jee.itf.TestGroup;
import io.probedock.jee.itf.model.TestGroupDefinition.SetupMethod;
import io.probedock.jee.itf.test.utils.ItfTestHelper;
import io.probedock.jee.itf.test.utils.groups.DummyTestGroupOrderedSetup;
import io.probedock.client.annotations.ProbeTest;
import io.probedock.client.annotations.ProbeTestClass;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for {@link TestGroupConfiguration}
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
@ProbeTestClass(tags = "test-group-configuration-ordered-setup")
public class TestGroupConfigurationOrderedSetupTest {

	Description descriptionTestOne;
	Description descriptionTestTwo;
	Description descriptionTestThree;
	TestGroup testGroup;
	TestGroupDefinition testGroupConfiguration;
	
	@Before
	public void setup() {
		testGroup = ItfTestHelper.createDefaultTestGroupOrderedSetup();
		testGroupConfiguration = new TestGroupDefinition(testGroup, new Random());

		try {
			Method m = testGroup.getClass().getMethod("testMethodOne", Description.class);
			io.probedock.jee.itf.annotations.Test testAnnotation = m.getAnnotation(io.probedock.jee.itf.annotations.Test.class);
			descriptionTestOne = new Description(testGroup.getClass().getName(), testAnnotation, testGroup.getClass(), m);

			m = testGroup.getClass().getMethod("testMethodTwo", Description.class);
			testAnnotation = m.getAnnotation(io.probedock.jee.itf.annotations.Test.class);
			descriptionTestTwo = new Description(testGroup.getClass().getName(), testAnnotation, testGroup.getClass(), m);
		
			m = testGroup.getClass().getMethod("testMethodThree", Description.class);
			testAnnotation = m.getAnnotation(io.probedock.jee.itf.annotations.Test.class);
			descriptionTestThree = new Description(testGroup.getClass().getName(), testAnnotation, testGroup.getClass(), m);
		}
		catch (NoSuchMethodException nme) {}
		catch (SecurityException se) {}
	}
	
	@Test
	@ProbeTest(key = "4747795744f6")
	public void threeOrderedBeforeAllMustOrderedInTheRightOrder() throws NoSuchMethodException {
		List<SetupMethod> methods = testGroupConfiguration.getBeforeAll();
		
		Method m1 = DummyTestGroupOrderedSetup.class.getMethod("beforeAll_1");
		Method m2 = DummyTestGroupOrderedSetup.class.getMethod("beforeAll_2");
		Method m3 = DummyTestGroupOrderedSetup.class.getMethod("beforeAll_3");
		
		assertEquals("beforeAll_2 should be first", m2, methods.get(0).getMethod());
		assertEquals("beforeAll_3 should be second", m3, methods.get(1).getMethod());
		assertEquals("beforeAll_1 should be third", m1, methods.get(2).getMethod());
	}

	@Test
	@ProbeTest(key = "35fcd068f081")
	public void threeOrderedAfterAllMustOrderedInTheRightOrder() throws NoSuchMethodException {
		List<SetupMethod> methods = testGroupConfiguration.getAfterAll();
		
		Method m1 = DummyTestGroupOrderedSetup.class.getMethod("afterAll_1");
		Method m2 = DummyTestGroupOrderedSetup.class.getMethod("afterAll_2");
		Method m3 = DummyTestGroupOrderedSetup.class.getMethod("afterAll_3");
		
		assertEquals("afterAll_3 should be first", m3, methods.get(0).getMethod());
		assertEquals("afterAll_1 should be second", m1, methods.get(1).getMethod());
		assertEquals("afterAll_2 should be third", m2, methods.get(2).getMethod());
	}

	@Test
	@ProbeTest(key = "eb2097a5c8fc")
	public void threeOrderedBeforeEachOutTxMustOrderedInTheRightOrder() throws NoSuchMethodException {
		List<SetupMethod> methods = testGroupConfiguration.getBeforeEachOutMainTx();
		
		Method m1 = DummyTestGroupOrderedSetup.class.getMethod("beforeEachOutTx_1");
		Method m2 = DummyTestGroupOrderedSetup.class.getMethod("beforeEachOutTx_2");
		Method m3 = DummyTestGroupOrderedSetup.class.getMethod("beforeEachOutTx_3");
		
		assertEquals("beforeEachOutTx_3 should be first", m3, methods.get(0).getMethod());
		assertEquals("beforeEachOutTx_2 should be second", m2, methods.get(1).getMethod());
		assertEquals("beforeEachOutTx_1 should be third", m1, methods.get(2).getMethod());
	}

	@Test
	@ProbeTest(key = "9db9a5493890")
	public void threeOrderedAfterEachOutTxMustOrderedInTheRightOrder() throws NoSuchMethodException {
		List<SetupMethod> methods = testGroupConfiguration.getAfterEachOutMainTx();
		
		Method m1 = DummyTestGroupOrderedSetup.class.getMethod("afterEachOutTx_1");
		Method m2 = DummyTestGroupOrderedSetup.class.getMethod("afterEachOutTx_2");
		Method m3 = DummyTestGroupOrderedSetup.class.getMethod("afterEachOutTx_3");
		
		assertEquals("afterEachOutTx_3 should be first", m3, methods.get(0).getMethod());
		assertEquals("afterEachOutTx_2 should be second", m2, methods.get(1).getMethod());
		assertEquals("afterEachOutTx_1 should be third", m1, methods.get(2).getMethod());
	}

	@Test
	@ProbeTest(key = "0dfe7130b29d")
	public void threeOrderedBeforeEachInTxMustOrderedInTheRightOrder() throws NoSuchMethodException {
		List<SetupMethod> methods = testGroupConfiguration.getBeforeEachInMainTx();
		
		Method m1 = DummyTestGroupOrderedSetup.class.getMethod("beforeEachInTx_1");
		Method m2 = DummyTestGroupOrderedSetup.class.getMethod("beforeEachInTx_2");
		Method m3 = DummyTestGroupOrderedSetup.class.getMethod("beforeEachInTx_3");
		
		assertEquals("beforeEachInTx_2 should be first", m2, methods.get(0).getMethod());
		assertEquals("beforeEachInTx_3 should be second", m3, methods.get(1).getMethod());
		assertEquals("beforeEachInTx_1 should be third", m1, methods.get(2).getMethod());
	}

	@Test
	@ProbeTest(key = "a8077d9988d5")
	public void threeOrderedAfterEachInTxMustOrderedInTheRightOrder() throws NoSuchMethodException {
		List<SetupMethod> methods = testGroupConfiguration.getAfterEachInMainTx();
		
		Method m1 = DummyTestGroupOrderedSetup.class.getMethod("afterEachInTx_1");
		Method m2 = DummyTestGroupOrderedSetup.class.getMethod("afterEachInTx_2");
		Method m3 = DummyTestGroupOrderedSetup.class.getMethod("afterEachInTx_3");
		
		assertEquals("afterEachInTx_2 should be first", m2, methods.get(0).getMethod());
		assertEquals("afterEachInTx_3 should be second", m3, methods.get(1).getMethod());
		assertEquals("afterEachInTx_1 should be third", m1, methods.get(2).getMethod());
	}

	@Test
	@ProbeTest(key = "579820c80fb7")
	public void threeOrderedBeforeOutTxMustOrderedInTheRightOrder() throws NoSuchMethodException {
		List<SetupMethod> methods = testGroupConfiguration.getBeforeOutMainTx(descriptionTestOne);
		
		Method m1 = DummyTestGroupOrderedSetup.class.getMethod("beforeOutTx_1");
		Method m2 = DummyTestGroupOrderedSetup.class.getMethod("beforeOutTx_2");
		Method m3 = DummyTestGroupOrderedSetup.class.getMethod("beforeOutTx_3");
		
		assertEquals("beforeOutTx_3 should be first", m3, methods.get(0).getMethod());
		assertEquals("beforeOutTx_2 should be second", m2, methods.get(1).getMethod());
		assertEquals("beforeOutTx_1 should be third", m1, methods.get(2).getMethod());
	}
	
	@Test
	@ProbeTest(key = "b389d5b3cf02")
	public void threeOrderedAfterOutTxMustOrderedInTheRightOrder() throws NoSuchMethodException {
		List<SetupMethod> methods = testGroupConfiguration.getAfterOutMainTx(descriptionTestOne);
		
		Method m1 = DummyTestGroupOrderedSetup.class.getMethod("afterOutTx_1");
		Method m2 = DummyTestGroupOrderedSetup.class.getMethod("afterOutTx_2");
		Method m3 = DummyTestGroupOrderedSetup.class.getMethod("afterOutTx_3");
		
		assertEquals("afterOutTx_3 should be first", m3, methods.get(0).getMethod());
		assertEquals("afterOutTx_2 should be second", m2, methods.get(1).getMethod());
		assertEquals("afterOutTx_1 should be third", m1, methods.get(2).getMethod());
	}

	@Test
	@ProbeTest(key = "5101319daafc")
	public void threeOrderedBeforeInTxMustOrderedInTheRightOrder() throws NoSuchMethodException {
		List<SetupMethod> methods = testGroupConfiguration.getBeforeInMainTx(descriptionTestOne);
		
		Method m1 = DummyTestGroupOrderedSetup.class.getMethod("beforeInTx_1");
		Method m2 = DummyTestGroupOrderedSetup.class.getMethod("beforeInTx_2");
		Method m3 = DummyTestGroupOrderedSetup.class.getMethod("beforeInTx_3");
		
		assertEquals("beforeInTx_3 should be first", m3, methods.get(0).getMethod());
		assertEquals("beforeInTx_2 should be second", m2, methods.get(1).getMethod());
		assertEquals("beforeInTx_1 should be third", m1, methods.get(2).getMethod());
	}
	
	@Test
	@ProbeTest(key = "c03e73ad023d")
	public void threeOrderedAfterInTxMustOrderedInTheRightOrder() throws NoSuchMethodException {
		List<SetupMethod> methods = testGroupConfiguration.getAfterInMainTx(descriptionTestOne);
		
		Method m1 = DummyTestGroupOrderedSetup.class.getMethod("afterInTx_1");
		Method m2 = DummyTestGroupOrderedSetup.class.getMethod("afterInTx_2");
		Method m3 = DummyTestGroupOrderedSetup.class.getMethod("afterInTx_3");
		
		assertEquals("afterInTx_3 should be first", m3, methods.get(0).getMethod());
		assertEquals("afterInTx_2 should be second", m2, methods.get(1).getMethod());
		assertEquals("afterInTx_1 should be third", m1, methods.get(2).getMethod());
	}
}
