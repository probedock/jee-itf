package io.probedock.jee.itf.model;

import io.probedock.jee.itf.TestGroup;
import io.probedock.jee.itf.annotations.Test;
import io.probedock.jee.itf.annotations.TestSetup;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The test group definition contains the test methods, the setup methods
 * and different information about the test group.
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class TestGroupDefinition {
	private static final Log LOGGER = LogFactory.getLog(TestGroupDefinition.class);
	
	/**
	 * The name of the test group
	 */
	private String name;
	
	/**
	 * Test group
	 */
	private TestGroup group;

	/**
	 * Test methods
	 */
	private List<Description> testMethods = new ArrayList<>();
	private List<Description> setupedMethods = new ArrayList<>();
	
	/**
	 * After and before all test in the group
	 */
	private List<SetupMethod> afterAll = new ArrayList<>();
	private List<SetupMethod> beforeAll = new ArrayList<>();
	
	/**
	 * After and before each test in the group in the same transaction
	 * than the test method.
	 */
	private List<SetupMethod> afterEachInMainTx = new ArrayList<>();
	private List<SetupMethod> beforeEachInMainTx = new ArrayList<>();
	
	/**
	 * After and before each test in the group in a different transaction
	 * than the test method. This means it will not be rollbacked.
	 */
	private List<SetupMethod> afterEachOutMainTx = new ArrayList<>();
	private List<SetupMethod> beforeEachOutMainTx = new ArrayList<>();
	
	/**
	 * After and before a dedicated test in the group in the same transaction
	 * than the test method.
	 */
	private Map<String, List<SetupMethod>> afterInMainTx = new HashMap<>();
	private Map<String, List<SetupMethod>> beforeInMainTx = new HashMap<>();
	
	/**
	 * After and before a dedicated test in the group in a different transaction
	 * than the test method. This means it will be rollbacked.
	 */
	private Map<String, List<SetupMethod>> afterOutMainTx = new HashMap<>();
	private Map<String, List<SetupMethod>> beforeOutMainTx = new HashMap<>();
	
	private Random rand;
	
	/**
	 * Constructor
	 * @param group The test group to configure
	 */
	public TestGroupDefinition(TestGroup group, Random rand) {
		if (rand == null) {
			throw new IllegalArgumentException("You must provide a valid Random instance");
		}
		
		this.rand = rand;
		
		name = group.getClass().getCanonicalName();
		this.group = group;
		
		// Check the methods
		checkMethods();
		
		/**
		 * Register test methods first to be sure refKey from setup configuration
		 * is present for the check done later.
		 */
		registerTestMethod();
		registerSetupMethod();
	}

	public List<SetupMethod> getAfterAll() {
		return getOrderedList(afterAll);
	}
	
	public List<SetupMethod> getBeforeAll() {
		return getOrderedList(beforeAll);
	}
	
	public List<SetupMethod> getAfterEachInMainTx() {
		return getOrderedList(afterEachInMainTx);
	}
	
	public List<SetupMethod> getBeforeEachInMainTx() {
		return getOrderedList(beforeEachInMainTx);
	}
	
	public List<SetupMethod> getAfterEachOutMainTx() {
		return getOrderedList(afterEachOutMainTx);
	}
	
	public List<SetupMethod> getBeforeEachOutMainTx() {
		return getOrderedList(beforeEachOutMainTx);
	}
	
	public Map<String, List<SetupMethod>> getAfterInMainTx() {
		return afterInMainTx;
	}
	
	public List<SetupMethod> getAfterInMainTx(Description description) {
		return getOrderedList(getMethods(description, afterInMainTx));
	}
	
	public Map<String, List<SetupMethod>> getBeforeInMainTx() {
		return beforeInMainTx;
	}
	
	public List<SetupMethod> getBeforeInMainTx(Description description) {
		return getOrderedList(getMethods(description, beforeInMainTx));
	}

	public Map<String, List<SetupMethod>> getAfterOutMainTx() {
		return afterOutMainTx;
	}
	
	public List<SetupMethod> getAfterOutMainTx(Description description) {
		return getOrderedList(getMethods(description, afterOutMainTx));
	}
	
	public Map<String, List<SetupMethod>> getBeforeOutMainTx() {
		return beforeOutMainTx;
	}
	
	public List<SetupMethod> getBeforeOutMainTx(Description description) {
		return getOrderedList(getMethods(description, beforeOutMainTx));
	}
	
	public String getName() {
		return name;
	}
	
	public TestGroup getTestGroup() {
		return group;
	}

	public List<Description> getTestMethods() {
		return testMethods;
	}
	
	/**
	 * Retrieve the list of methods corresponding to a method
	 * 
	 * @param description The description
	 * @param methods The methods map to extract setup methods
	 * @return The extracted setup methods
	 */
	private List<SetupMethod> getMethods(Description description, Map<String,List<SetupMethod>> methods) {
		List<SetupMethod> extractedMethods = new ArrayList<>();
		
		// Get the test methods by method name
		if (methods.containsKey(description.getSimpleName())) {
			extractedMethods.addAll(methods.get(description.getSimpleName()));
		}
		
		// Get the setup methods by reference key
		if (methods.containsKey(description.getTestAnnotation().setupKey())) {
			extractedMethods.addAll(methods.get(description.getTestAnnotation().setupKey()));
		}
		
		return extractedMethods;
	}
	
	/**
	 * Check if there are methods that are annotated with both annotations
	 */
	private void checkMethods() {
		for (Method method : group.getClass().getDeclaredMethods()) {
			Test testAnnotation = method.getAnnotation(Test.class);
			TestSetup setupAnnotation = method.getAnnotation(TestSetup.class);
			
			if (testAnnotation != null && setupAnnotation != null) {
				LOGGER.warn(
					"The test [" + name + "." + method.getName() + "] is not correctly configured. " +
					"The method cannot be annotated with both annotation " + Test.class.getName() + " and " +
					TestSetup.class.getName());
			}
		}
	}
	
	/**
	 * Register a test method
	 */
	private void registerTestMethod() {
		for (Method method : group.getClass().getDeclaredMethods()) {
			Test testAnnotation = method.getAnnotation(Test.class);
			TestSetup setupAnnotation = method.getAnnotation(TestSetup.class);
	
			if (testAnnotation != null && setupAnnotation == null) {
				boolean testValid = true;

				// Check if the test method has the right args to run
				if (method.getParameterTypes().length == 0 || method.getParameterTypes().length > 1 || method.getParameterTypes()[0] != Description.class) {
					LOGGER.warn("The test method " + name + "." + method.getName() + "] should not have Description as argument");
					testValid = false;
				} 

				// Check if the return type is the one expected
				if (method.getReturnType() != Description.class) {
					LOGGER.warn("The test method [" + name + "." + method.getName() + "] must have [" + Description.class.getCanonicalName() + "] as result type");
					testValid = false;
				}

				if (testValid) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("The test method: " + name + "." + method.getName() + " is registered.");
					}
					
					Description description = new Description(name, testAnnotation, group.getClass(), method);
					
					testMethods.add(description);
					
					// Track the methods that have some setup configuration
					if (testAnnotation.setupKey() != null && !testAnnotation.setupKey().isEmpty()) {
						setupedMethods.add(description);
					}
				}
			}
		}
		
		Collections.shuffle(testMethods, rand);
	}
	
	/**
	 * Register a setup method to wrap tests
	 */
	private void registerSetupMethod() {
		for (Method method : group.getClass().getDeclaredMethods()) {
			Test testAnnotation = method.getAnnotation(Test.class);
			TestSetup setupAnnotation = method.getAnnotation(TestSetup.class);

			if (testAnnotation == null && setupAnnotation != null) {
				boolean shouldContinue = true;

				// Check if the setup method has no args to be run
				if (method.getParameterTypes().length > 0) {
					LOGGER.warn("The setup method " + name + "." + method.getName() + "] should not have any parameter");
					shouldContinue = false;
				} 

				// Check if the return type is the one expected
				if (!method.getReturnType().equals(Void.TYPE)) {
					LOGGER.warn("The setup method [" + name + "." + method.getName() + "] return value must be void.");
					shouldContinue = false;
				}

				if (shouldContinue) {
					switch (setupAnnotation.value()) {
						case AFTER_ALL:
							addToList(afterAll, setupAnnotation, method);
							break;

						case BEFORE_ALL:
							addToList(beforeAll, setupAnnotation, method);
							break;

						case AFTER_EACH_IN_MAIN_TX:
							addToList(afterEachInMainTx, setupAnnotation, method);
							break;

						case AFTER_EACH_OUT_MAIN_TX:
							addToList(afterEachOutMainTx, setupAnnotation, method);
							break;

						case BEFORE_EACH_IN_MAIN_TX:
							addToList(beforeEachInMainTx, setupAnnotation, method);
							break;

						case BEFORE_EACH_OUT_MAIN_TX:
							addToList(beforeEachOutMainTx, setupAnnotation, method);
							break;

						case BEFORE_IN_MAIN_TX:
							registerSetupMethod(setupAnnotation, method, beforeInMainTx);
							break;

						case BEFORE_OUT_MAIN_TX:
							registerSetupMethod(setupAnnotation, method, beforeOutMainTx);
							break;

						case AFTER_IN_MAIN_TX:
							registerSetupMethod(setupAnnotation, method, afterInMainTx);
							break;

						case AFTER_OUT_MAIN_TX:
							registerSetupMethod(setupAnnotation, method, afterOutMainTx);
							break;
					}
				}
			}
		}
	}
	
	/**
	 * Register a setup method for a specific test method
	 * @param setupAnnotation The setup annotation for the configuration
	 * @param setupMethod The setup method to register
	 * @param setupMethods The registered methods to update
	 */
	private void registerSetupMethod(TestSetup setupAnnotation, Method setupMethod, Map<String, List<SetupMethod>> setupMethods) {
		// Check if the pointed method is configured
		if (setupAnnotation.refSetupKey() == null || setupAnnotation.refSetupKey().length == 0) {
			LOGGER.warn("The refSetupKey configuration is missing on " + name + "." + setupMethod.getName());
		}

		else {
			// Check every ref setup key
			for (String refSetupKey : setupAnnotation.refSetupKey()) {
				Method testMethod = null;

				// Try to find a test method that is refered by the ref key
				for (Description description : setupedMethods) {
					if (refSetupKey.equals(description.getTestAnnotation().setupKey())) {
						testMethod = description.getMethod();
						break;
					}
				}

				// If no test method is found, try to retrieve the method through its method name
				if (testMethod == null) {
					try {
						testMethod = group.getClass().getDeclaredMethod(setupMethod.getName());
					}
					catch (NoSuchMethodException nsme) { }
				}

				// If a test method is found, register it
				if (testMethod != null) {
					if (!setupMethods.containsKey(refSetupKey)) {
						setupMethods.put(refSetupKey, new ArrayList<SetupMethod>());
					}

					// Register the setup method
					addToList(setupMethods.get(refSetupKey), setupAnnotation, setupMethod);
				}
				else {
					LOGGER.warn("Unable to find a test method that correspond to " + refSetupKey);
				}
			}
		}
	}
	
	/**
	 * Add a setup method to a specific list
	 * 
	 * @param list List to add the method
	 * @param setup The setup annotation
	 * @param method The method to add
	 */
	private void addToList(List<SetupMethod> list, TestSetup setup, Method method) {
		if (setup.index() > 0) {
			list.add(new SetupMethod(method, setup.index()));
		}
		else {
			list.add(new SetupMethod(method));
		}
	}

	/**
	 * Order the list
	 * 
	 * @param list The list to order
	 * @return The ordered list
	 */
	private List<SetupMethod> getOrderedList(List<SetupMethod> list) {
		Collections.sort(list);
		return list;
	}
	
	/**
	 * Allow to order the setup methods
	 */
	public class SetupMethod implements  Comparable<SetupMethod> {
		private Method method;
		private int index;

		/**
		 * Constructor
		 * 
		 * @param method Setup method
		 */
		public SetupMethod(Method method) {
			this.method = method;
		}
		
		/**
		 * Constructor
		 * 
		 * @param method Setup method
		 * @param index Index of the method
		 */
		public SetupMethod(Method method, int index) {
			this(method);
			this.index = index;
		}

		public Method getMethod() {
			return method;
		}

		@Override
		public int compareTo(SetupMethod o) {
			if (this.index < o.index) {
				return -1;
			}
			else if (this.index > o.index) {
				return 1;
			}
			else {
				return 0;
			}
		}
	}
}
