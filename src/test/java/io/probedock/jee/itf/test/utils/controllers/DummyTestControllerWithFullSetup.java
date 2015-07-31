package io.probedock.jee.itf.test.utils.controllers;

import io.probedock.jee.itf.AbstractTestController;
import io.probedock.jee.itf.TestGroup;
import io.probedock.jee.itf.annotations.Test;
import io.probedock.jee.itf.annotations.TestSetup;
import io.probedock.jee.itf.annotations.TestSetupType;
import io.probedock.jee.itf.model.Description;

import javax.ejb.EJB;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

/**
 * Dummy test group to use in the unit tests
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
@TransactionManagement(TransactionManagementType.BEAN)
public class DummyTestControllerWithFullSetup extends AbstractTestController {
	@EJB
	public TestGroup testGroup = new DummyTestGroup();

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

		@TestSetup(value = TestSetupType.BEFORE_OUT_MAIN_TX, refSetupKey= "testMethodOne")
		public void beforeOutTx() {}
		
		@TestSetup(value = TestSetupType.AFTER_OUT_MAIN_TX, refSetupKey = "testMethodOne")
		public void afterOutTx() {}

		@TestSetup(value = TestSetupType.BEFORE_IN_MAIN_TX, refSetupKey = "testMethodOne")
		public void beforeInTx() {}
		
		@TestSetup(value = TestSetupType.AFTER_IN_MAIN_TX, refSetupKey = "testMethodOne")
		public void afterInTx() {}

		@Test
		public Description testMethodOne(Description description) {
			return description.pass();
		}

		@Test
		public Description testMethodTwo(Description description) {
			return description.pass();
		}

		@Override
		public TestGroup getTestGroup() {
			return this;
		}
	}
}