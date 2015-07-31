package io.probedock.jee.itf.test.utils.groups;

import io.probedock.jee.itf.TestGroup;
import io.probedock.jee.itf.annotations.NoRollback;
import io.probedock.jee.itf.annotations.Test;
import io.probedock.jee.itf.annotations.TestSetup;
import io.probedock.jee.itf.annotations.TestSetupType;
import io.probedock.jee.itf.model.Description;

/**
 * Dummy test group to use in the unit tests
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class DummyTestGroup implements TestGroup {
	@TestSetup(TestSetupType.BEFORE_ALL)
	public void beforeAll() {}

	@TestSetup(TestSetupType.AFTER_ALL)
	public void afterAll() {}

	@TestSetup(TestSetupType.BEFORE_EACH_OUT_MAIN_TX)
	public void beforeEachOutTx() {}

	@TestSetup(TestSetupType.AFTER_EACH_OUT_MAIN_TX)
	public void afterEachOutTx() {}

	@TestSetup(TestSetupType.BEFORE_EACH_IN_MAIN_TX)
	public void beforeEachInTx() {}

	@TestSetup(TestSetupType.AFTER_EACH_IN_MAIN_TX)
	public void afterEachInTx() {}

	@TestSetup(value = TestSetupType.BEFORE_OUT_MAIN_TX, refSetupKey = "testMethod")
	public void beforeOutTx() {}

	@TestSetup(value = TestSetupType.AFTER_OUT_MAIN_TX, refSetupKey = "testMethod")
	public void afterOutTx() {}

	@TestSetup(value = TestSetupType.BEFORE_IN_MAIN_TX, refSetupKey = "testMethod")
	public void beforeInTx() {}

	@TestSetup(value = TestSetupType.AFTER_IN_MAIN_TX, refSetupKey = "testMethod")
	public void afterInTx() {}

	@Test
	public Description testMethod(Description description) {
		return description.pass();
	}

	@Test
	@NoRollback
	public Description testMethodTwo(Description description) {
		return description.fail("This method should not pass");
	}

	@Override
	public TestGroup getTestGroup() {
		return this;
	}
}