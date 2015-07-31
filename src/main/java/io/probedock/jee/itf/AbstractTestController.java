package io.probedock.jee.itf;

import io.probedock.jee.itf.filters.Filter;
import io.probedock.jee.itf.listeners.Listener;
import io.probedock.jee.itf.model.Description;
import io.probedock.jee.itf.model.TestGroupDefinition;
import io.probedock.jee.itf.model.TestGroupDefinition.SetupMethod;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * In order to run the integration test, a general test controller is required to manage the injected session beans.
 * <p/>
 * The abstract controller centralized the integration test management to run every tests. The transaction management is
 * also done to allow commits and rollbacks for the tests.
 * <p/>
 * A best practice is to keep test independent from the others. To enforce that best practice, the test groups and the
 * tests in the groups are shuffled. Therefore, the order of test run are not the same between two different run. It
 * means that you cannot write tests that depends on one another test because you cannot be sure that the dependency
 * order is respected between runs.
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public abstract class AbstractTestController implements TestController {
    private static final Log LOGGER = LogFactory.getLog(AbstractTestController.class);

    @Resource
    private SessionContext sessionContext;

    @Override
    public Long run(Map<String, Filter> filters, Map<String, Listener> listeners, Long seed) {

        // Ensure the random generator has a valid seed
        Long internalSeed;
        if (seed == null || seed < 0) {
            internalSeed = System.currentTimeMillis();
        } else {
            internalSeed = seed;
        }

        // Validate that the TestController is correctly annotated and configured
        TestControllerConfiguration configuration = createConfiguration(filters, listeners, internalSeed);

        // Populate the test groups
        popuplateTestGroups(configuration);

        testRunStart(configuration);

		/*
         * Run the test with the setup and teardown methods
		 */
        for (TestGroupDefinition testGroupDefinition : configuration.getTestGroupDefinitions()) {
            runBeforeAll(testGroupDefinition);
            runTests(configuration, testGroupDefinition);
            runAfterAll(testGroupDefinition);
        }

        testRunEnd(configuration);

        return internalSeed;
    }

    /**
     * Check that the current test controller is well configured and can run the integration test in correct conditions.
     * Try to fix missing elements.
     *
     * @param filters The filters to configure
     * @param listeners The listeners to configure
     * @param seed The random generator seed to use
     */
    private TestControllerConfiguration createConfiguration(Map<String, Filter> filters, Map<String, Listener> listeners, Long seed) {
        Annotation beanAnnotation = this.getClass().getAnnotation(TransactionManagement.class);
        if (beanAnnotation == null) {
            throw new RuntimeException("The TransactionManagement annotation is missing. You should annotate your test controller with: " +
                "@TransactionManagement(TransactionManagementType.BEAN).");
        } else {
            TransactionManagement transactionManangement = (TransactionManagement) beanAnnotation;
            if (transactionManangement.value() != TransactionManagementType.BEAN) {
                throw new RuntimeException("Your test controller is annotated with TransactionManagement but you should use " +
                    "TransactionManagementType.BEAN as the value for the annotation.");
            }
        }

        // Create the configuration
        TestControllerConfiguration configuration = new TestControllerConfiguration();

        // Create the random generator and set it to the configuration
        configuration.setRand(new Random(seed));

        // Register filters
        if (filters != null && !filters.isEmpty()) {
            for (Entry<String, Filter> entry : filters.entrySet()) {
                configuration.register(entry.getKey(), entry.getValue());
            }
        }

        // Register listeners
        if (listeners != null && !listeners.isEmpty()) {
            for (Entry<String, Listener> entry : listeners.entrySet()) {
                configuration.register(entry.getKey(), entry.getValue());
            }
        }

        configuration.ensure();

        return configuration;
    }

    /**
     * Populate the test groups to get all the methods (setup + test)
     *
     * @param configuration The configuration to handle the test of the tests
     */
    private void popuplateTestGroups(TestControllerConfiguration configuration) {
		/*
		 * Retrieve the fields from the test controller and verify which
		 * are session beans.
		 */
        for (Field field : getClass().getDeclaredFields()) {

            Annotation ejbAnnotation = field.getAnnotation(EJB.class);

            if (ejbAnnotation != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Test group " + field.getName() + " found (type: " + field.getType().getCanonicalName() + ").");
                }

                TestGroup testGroup = getTestGroup(field);
                if (testGroup != null) {
                    configuration.addTestGroupDefinition(new TestGroupDefinition(testGroup, configuration.getRand()));
                }
            } else {
                LOGGER.warn("An attribute in the TestController seems to not be a valid Session Bean [" + field.getName() + "]");
            }
        }

        configuration.shuffleTestGroupDefinitions();
    }

    /**
     * Retrieve the EJB representation for a field discovered in the class that extends the current one.
     * <p/>
     * WARNING: No check is done to know if the field is an EJB or not, it is expected to be already checked before
     * calling this method.
     *
     * @param field The field to get the EJB representation
     * @return The EJB representation that contains the proxy and the concrete class
     */
    private TestGroup getTestGroup(Field field) {
        try {
            return ((TestGroup) field.get(this)).getTestGroup();
        }

        // The test group is not defined as public member
        catch (IllegalAccessException iae) {
            LOGGER.error("The test class [{" + field.getName() + "}] must be set to public.");
        }

        // The EJB does not extends the IIntegrationTestGroup interface
        catch (ClassCastException cce) {
            LOGGER.error("The " + field.getType().getCanonicalName() + " does not extends " + TestGroup.class.getCanonicalName() +
                ". You must extends this interface to define an integration test.");
        }

        return null;
    }

    /**
     * Run the setup methods for all the test in a test group
     *
     * @param testGroupDefinition The test group definition
     */
    private void runBeforeAll(TestGroupDefinition testGroupDefinition) {
        runMethodsInTx(testGroupDefinition.getBeforeAll(), testGroupDefinition.getTestGroup(), testGroupDefinition.getName(), "beforeAll");
    }

    /**
     * Run the setup methods for all test in the test group in separate transaction than the one used for the test
     * method itself.
     *
     * @param testGroupDefinition The test group definition
     */
    private void runBeforeEachOutOfMainTx(TestGroupDefinition testGroupDefinition) {
        runMethodsInTx(testGroupDefinition.getBeforeEachOutMainTx(), testGroupDefinition.getTestGroup(), testGroupDefinition.getName(), "beforeEachOutOfMainTx");
    }

    /**
     * Run the setup methods for a specific test in a the test group in a separate transaction than the one used for the
     * test method itself.
     *
     * @param testGroupDefinition The test group definition
     * @param description The description that contains the test
     */
    private void runBeforeOutOfMainTx(TestGroupDefinition testGroupDefinition, Description description) {
        runMethodsInTx(testGroupDefinition.getBeforeOutMainTx(description), testGroupDefinition.getTestGroup(),
            testGroupDefinition.getName(), "beforeOutOfMainTx:" + description.getSimpleName());
    }

    /**
     * Run the setup methods for all test in the test group in the same transaction than the one used for the test
     * method itself.
     *
     * @param testGroupDefinition The test group definition
     */
    private void runBeforeEachInMainTx(TestGroupDefinition testGroupDefinition) {
        runMethodsOutOfTx(testGroupDefinition.getBeforeEachInMainTx(), testGroupDefinition.getTestGroup(), testGroupDefinition.getName(), "beforeEachInMainTx");
    }

    /**
     * Run the setup methods for a specific test in a the test group in the same transaction than the one used for the
     * test method itself.
     *
     * @param testGroupDefinition The test group definition
     * @param description The description that contains the test
     */
    private void runBeforeInMainTx(TestGroupDefinition testGroupDefinition, Description description) {
        runMethodsOutOfTx(testGroupDefinition.getBeforeInMainTx(description), testGroupDefinition.getTestGroup(),
            testGroupDefinition.getName(), "beforeEachInMainTx:" + description.getSimpleName());
    }

    /**
     * Run the test methods of the test group
     *
     * @param configuration The test controller configuration
     * @param testGroupDefinition The test group definition
     */
    private void runTests(TestControllerConfiguration configuration, TestGroupDefinition testGroupDefinition) {
        // Run each test
        for (Description description : testGroupDefinition.getTestMethods()) {
            if (isRunnable(configuration, description)) {
                // Setup methods to apply for each test method
                runBeforeEachOutOfMainTx(testGroupDefinition);
                runBeforeOutOfMainTx(testGroupDefinition, description);

                testStart(configuration, description);

                // Run the test
                runTest(testGroupDefinition, description);

                if (description.isPassed()) {
                    success(configuration, description);
                } else {
                    fail(configuration, description);
                }

                testEnd(configuration, description);

                // Teardown methods to apply for each test method
                runAfterOutOfMainTx(testGroupDefinition, description);
                runAfterEachOutOfMainTx(testGroupDefinition);
            } else {
                LOGGER.info("Test " + description.getName() + " will not run");
            }
        }
    }

    /**
     * Run the setup methods for a specific test in a the test group in the same transaction than the one used for the
     * test method itself.
     *
     * @param testGroupDefinition The test group definition
     * @param description The method configuration
     */
    private void runAfterInMainTx(TestGroupDefinition testGroupDefinition, Description description) {
        runMethodsOutOfTx(testGroupDefinition.getAfterInMainTx(description), testGroupDefinition.getTestGroup(),
            testGroupDefinition.getName(), "afterInMainTx:" + description.getSimpleName());
    }

    /**
     * Run the setup methods for all test in the test group in the same transaction than the one used for the test
     * method itself.
     *
     * @param testGroupDefinition The test group definition
     */
    private void runAfterEachInMainTx(TestGroupDefinition testGroupDefinition) {
        runMethodsOutOfTx(testGroupDefinition.getAfterEachInMainTx(), testGroupDefinition.getTestGroup(), testGroupDefinition.getName(), "afterEachInMainTx");
    }

    /**
     * Run the setup methods for a specific test in a the test group in a separate transaction than the one used for the
     * test method itself.
     *
     * @param testGroupDefinition The test group definition
     * @param description The method configuration
     */
    private void runAfterOutOfMainTx(TestGroupDefinition testGroupDefinition, Description description) {
        runMethodsInTx(testGroupDefinition.getAfterOutMainTx(description), testGroupDefinition.getTestGroup(),
            testGroupDefinition.getName(), "afterOutMainTx:" + description.getSimpleName());
    }

    /**
     * Run the setup methods for all test in the test group in separate transaction than the one used for the test
     * method itself.
     *
     * @param testGroupDefinition The test group definition
     */
    private void runAfterEachOutOfMainTx(TestGroupDefinition testGroupDefinition) {
        runMethodsInTx(testGroupDefinition.getAfterEachOutMainTx(), testGroupDefinition.getTestGroup(), testGroupDefinition.getName(), "afterOutMainTx");
    }

    /**
     * Run the setup methods for all the test in a test group
     *
     * @param testGroupDefinition The test group definition
     */
    private void runAfterAll(TestGroupDefinition testGroupDefinition) {
        runMethodsInTx(testGroupDefinition.getAfterAll(), testGroupDefinition.getTestGroup(), testGroupDefinition.getName(), "afterAll");
    }

    /**
     * Run methods into a dedicated transaction
     *
     * @param methods The methods to run into the same transaction
     * @param testGroup The integration test group where the method are defined
     * @param name The name of the test group
     * @param type The type of methods to run (kind of setup)
     */
    private void runMethodsInTx(List<SetupMethod> methods, TestGroup testGroup, String name, String type) {
        UserTransaction utx = sessionContext.getUserTransaction();

        if (methods != null && !methods.isEmpty()) {
            try {
                // Start a new transaction
                utx.begin();
            } catch (Exception e) {
                LOGGER.error("Unable to start TX for test group [" + name + "]{setup:" + type + "} because: " + e.getMessage(), e);
            }

            // Run the setup method
            for (SetupMethod setupMethod : methods) {
                Method method = setupMethod.getMethod();
                try {
                    method.invoke(testGroup);
                } catch (Exception e) {
                    LOGGER.error("Unable to run [" + name + "." + method.getName() + "]{setup:" + type + "} because: " + e.getMessage(), e);
                }
            }

            // Rollback the transaction, commit it if the annotation to avoid rollback is present
            try {
                utx.commit();
            } catch (Exception e) {
                LOGGER.error("Unable to commit TX for test group [" + name + "]{setup:" + type + "} because: " + e.getMessage(), e);
            }
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No setup methods to run for test group [{" + name + "}]'{'setup:{" + type + "}'}'");
            }
        }
    }

    /**
     * Run methods without any transaction (let the caller to handle the transaction)
     *
     * @param methods The methods to run into the same transaction
     * @param testGroup The integration test group where the method are defined
     * @param name The name of the test group
     * @param type The type of methods to run (kind of setup)
     */
    private void runMethodsOutOfTx(List<SetupMethod> methods, TestGroup testGroup, String name, String type) {
        // Run the setup method
        if (methods != null) {
            for (SetupMethod setupMethod : methods) {
                Method method = setupMethod.getMethod();
                try {
                    method.invoke(testGroup);
                } catch (Exception e) {
                    LOGGER.error("Unable to run [" + name + "." + method.getName() + "]{setup:" + type + "} because: " + e.getMessage(), e);
                }
            }
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No setup methods to run for test group [{" + name + "}]'{'setup:{" + type + "}'}'");
            }
        }
    }

    /**
     * Run a test method
     *
     * @param testGroupDefinition The test group definition
     * @param description The test description
     */
    private void runTest(TestGroupDefinition testGroupDefinition, Description description) {
        // Run the test, when an exception is thrown in the test, consider test as failed
        UserTransaction utx = sessionContext.getUserTransaction();

        try {
            // Start a new transaction
            utx.begin();
        } catch (Exception e) {
            throw new RuntimeException("Unable to start the transaction for test " + description.getName());
        }

        // Setup methods for the test in the same transaction
        runBeforeEachInMainTx(testGroupDefinition);
        runBeforeInMainTx(testGroupDefinition, description);

        // Run the test
        try {
            description = (Description) description.getMethod().invoke(testGroupDefinition.getTestGroup(), description);
        } catch (Exception e) {
            // Build the message
            Writer writer = new StringWriter();
            e.getCause().printStackTrace(new PrintWriter(writer));

            description.fail("Invocation Target Exception: Message[" + e.getCause().getMessage() + "]\n" + writer.toString());

            LOGGER.error("Unable to run the test " + description.getName(), e.getCause());
        }

        // Teardown methods for the test in the same transaction
        runAfterInMainTx(testGroupDefinition, description);
        runAfterEachInMainTx(testGroupDefinition);

        // Rollback the transaction, commit it if the annotation to avoid rollback is present
        try {
            if (description.isRollbackable()) {
                utx.commit();
            } else {
                utx.rollback();
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Test transaction is: " + (description.isRollbackable() ? "commit" : "rollback"));
            }
        } catch (Exception e) {
            LOGGER.error("Unable to " + (description.isRollbackable() ? "commit" : "rollback") +
                " the transaction for test " + description.getName() + " because: " + e.getMessage(), e);
        }
    }

    /**
     * Check if a test is runnable or not
     *
     * @param configuration The configuration to get the filters
     * @param description Description where to get info to take a decision
     * @return True if the test could be run
     */
    private boolean isRunnable(TestControllerConfiguration configuration, Description description) {
        for (Filter filter : configuration.getFilters()) {
            if (!filter.isRunnable(description)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Notify that the test run started
     *
     * @param configuration The configuration to get the filters
     */
    private void testRunStart(TestControllerConfiguration configuration) {
        for (Listener listener : configuration.getListeners()) {
            listener.testRunStart();
        }
    }

    /**
     * Notify that the test run ended
     *
     * @param configuration The configuration to get the filters
     */
    private void testRunEnd(TestControllerConfiguration configuration) {
        for (Listener listener : configuration.getListeners()) {
            listener.testRunEnd();
        }
    }

    /**
     * Notify that the test started
     *
     * @param configuration The configuration to get the filters
     * @param description Test description
     */
    private void testStart(TestControllerConfiguration configuration, Description description) {
        for (Listener listener : configuration.getListeners()) {
            listener.testStart(description);
        }
    }

    /**
     * Notify that the test ended
     *
     * @param configuration The configuration to get the filters
     * @param description Test description
     */
    private void testEnd(TestControllerConfiguration configuration, Description description) {
        for (Listener listener : configuration.getListeners()) {
            listener.testEnd(description);
        }
    }

    /**
     * Notify that the test failed
     *
     * @param configuration The configuration to get the filters
     * @param description Test description
     */
    private void fail(TestControllerConfiguration configuration, Description description) {
        for (Listener listener : configuration.getListeners()) {
            listener.fail(description);
        }
    }

    /**
     * Notify that the test succeed
     *
     * @param configuration The configuration to get the filters
     * @param description Test description
     */
    private void success(TestControllerConfiguration configuration, Description description) {
        for (Listener listener : configuration.getListeners()) {
            listener.success(description);
        }
    }
}
