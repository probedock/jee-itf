package io.probedock.jee.itf;

import io.probedock.client.annotations.ProbeTest;
import io.probedock.client.annotations.ProbeTestClass;
import io.probedock.jee.itf.filters.DefaultFilter;
import io.probedock.jee.itf.filters.Filter;
import io.probedock.jee.itf.listeners.DefaultListener;
import io.probedock.jee.itf.listeners.Listener;
import io.probedock.jee.itf.model.Description;
import io.probedock.jee.itf.model.TestGroupDefinition;
import io.probedock.jee.itf.test.utils.groups.DummyTestGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.ejb.EJB;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.junit.Before;

import static org.junit.Assert.*;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

/**
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
@ProbeTestClass(tags = "test-controller-configuration")
public class TestControllerConfigurationTest {
	private TestControllerConfiguration configuration;

	private Map<String, Listener> listeners;
	private Map<String, Filter> filters;
	private List<TestGroupDefinition> testGroupDefinitions;

	@InjectMocks
  private TestControllerConfigurationTest.DummyTestControllerWithPrivateTestGroup testControllerWithPrivateTestGroup;

	
	@Before
	public void setup() {
		configuration = new TestControllerConfiguration();
		
		testControllerWithPrivateTestGroup = new TestControllerConfigurationTest.DummyTestControllerWithPrivateTestGroup();
		
		MockitoAnnotations.initMocks(this);
		
		listeners = new HashMap<String, Listener>();
		filters = new HashMap<String, Filter>();
		testGroupDefinitions = new ArrayList<TestGroupDefinition>();
		
		Whitebox.setInternalState(configuration, "listeners", listeners);
		Whitebox.setInternalState(configuration, "filters", filters);
		Whitebox.setInternalState(configuration, "testGroupDefinitions", testGroupDefinitions);
	}
	
	@Test
	@ProbeTest(key = "b3972882c88e")
	public void defaultListenerShouldBeCreatedWhenNoOtherListenerIsRegistered() {
		configuration.ensure();
		
		assertEquals("A listener should be present", 1, listeners.size());
		assertNotNull("The default listener should be present", listeners.get("defaultListener"));
		assertEquals("A default listener should be created", DefaultListener.class, listeners.get("defaultListener").getClass());
	}
	
	@Test
	@ProbeTest(key = "93e35d29d3ca")
	public void defaultFilterShouldBeCreatedWhenNoOtherFilterIsRegistered() {
		configuration.ensure();
		
		assertEquals("A filter should be present", 1, filters.size());
		assertNotNull("The default filter should be present", filters.get("defaultFilter"));
		assertEquals("A default filter should be created", DefaultFilter.class, filters.get("defaultFilter").getClass());
	}
	
	@Test
	@ProbeTest(key = "08c80d2e0e1c")
	public void oneTestGroupShouldBeRegisteredInTheTestControllerWhenOneIsInjectedInTheTestController() {
		configuration.addTestGroupDefinition(new TestGroupDefinition(new DummyTestGroup(), new Random()));
		
		assertEquals("The test controller should contain one test group", testGroupDefinitions.size(), 1);
		assertEquals("The test group should be the one expected", DummyTestGroup.class, testGroupDefinitions.get(0).getTestGroup().getClass());
	}

	@Test
	@ProbeTest(key = "c1d1350185ac")
	public void testGroupShouldHavePublicModifierToBeRegistered() {
		testControllerWithPrivateTestGroup.run(null, null, null);
	}
	
	@TransactionManagement(TransactionManagementType.BEAN)
	private class DummyTestControllerWithPrivateTestGroup extends AbstractTestController {
		@EJB
		private TestGroup testGroup = new TestGroup() {

			@Override
			public TestGroup getTestGroup() {
				return this;
			}
			
			@io.probedock.jee.itf.annotations.Test
			public Description test(Description description) {
				fail("When a test group is private, it should not be registered for tests");
				return description;
			}
		};
	}

}
