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
public class DummyTestControllerWithTestBeforeAfterAll extends AbstractTestController {
	@EJB
	public TestGroup testGroup = new DummyTestGroup();

	public class DummyTestGroup implements TestGroup {
		@TestSetup(TestSetupType.BEFORE_ALL)
		public void beforeAll() {}
		
		@TestSetup(TestSetupType.AFTER_ALL)
		public void afterAll() {}
		
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