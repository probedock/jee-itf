package io.probedock.jee.itf.model;

import io.probedock.jee.itf.annotations.NoRollback;
import io.probedock.jee.itf.annotations.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Test description contains the information that allows to run the test in correct conditions.
 * <p/>
 * The description should be enriched with the test result during the test execution.
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class Description {
    /**
     * Configuration for the test
     */
    private Test testAnnotation;

    /**
     * The name of the test group
     */
    private String groupName;

    /**
     * Define if the test is runnable or not
     */
    private boolean runnable = true;

    /**
     * Test class
     */
    private Class testClass;

    /**
     * The test method
     */
    private Method method;

    /**
     * Determine if the test passed or not
     */
    private boolean passed = false;

    /**
     * Start and end dates of the test execution
     */
    private long startDate;
    private long endDate;

    /**
     * Duration of the test execution
     */
    private long duration;

    /**
     * A message to add more valuable information to a test
     */
    private String message;

    /**
     * Additional data to enrich a test
     */
    private Map<String, String> data = new HashMap<>();

    /**
     * Constructor
     *
     * @param groupName The group name where the method is part of
     * @param testAnnotation The test annotation
     * @param testClass The class where the test method is defined
     * @param method The method that represent the test
     */
    public Description(String groupName, Test testAnnotation, Class testClass, Method method) {
        this.groupName = groupName;
        this.testAnnotation = testAnnotation;
        this.method = method;
        this.testClass = testClass;
    }

    public Method getMethod() {
        return method;
    }

    public Class getTestClass() {
        return testClass;
    }

    public boolean isRunnable() {
        return runnable;
    }

    public void setRunnable(boolean runnable) {
        this.runnable = runnable;
    }

    public boolean isRollbackable() {
        return method.isAnnotationPresent(NoRollback.class);
    }

    public boolean isPassed() {
        return passed;
    }

    public String getMessage() {
        return message;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Test getTestAnnotation() {
        return testAnnotation;
    }

    public Map<String, String> getData() {
        return data;
    }

    /**
     * Add new data
     *
     * @param key Data key
     * @param value Data value
     */
    public void addData(String key, String value) {
        data.put(key, value);
    }

    /**
     * Retrieve the name of the method without the class name
     *
     * @return The simple name
     */
    public String getSimpleName() {
        return method.getName();
    }

    /**
     * Build the test name composed from class name of the member and the test method name
     *
     * @return The complete name
     */
    public String getName() {
        return groupName + "." + method.getName();
    }

    /**
     * Mark the test as passed without message
     *
     * @return this
     */
    public Description pass() {
        this.passed = true;
        return this;
    }

    /**
     * Mark the test as passed with a message
     *
     * @param message A friendly message
     * @return this
     */
    public Description pass(String message) {
        this.passed = true;
        this.message = message;
        return this;
    }

    /**
     * Mark the as not passed with a message
     *
     * @param message A friendly message
     * @return this
     */
    public Description fail(String message) {
        this.passed = false;
        this.message = message;
        return this;
    }
}

