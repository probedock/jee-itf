package io.probedock.jee.itf.test.utils.groups;

import io.probedock.jee.itf.TestGroup;
import io.probedock.jee.itf.annotations.Test;
import io.probedock.jee.itf.annotations.TestSetup;
import io.probedock.jee.itf.annotations.TestSetupType;
import io.probedock.jee.itf.model.Description;

/**
 * Dummy test group to use in the unit tests
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class DummyTestGroupOrderedSetup implements TestGroup {
	@TestSetup(value = TestSetupType.BEFORE_ALL, index = 2)
	public void beforeAll_1() {}

	@TestSetup(value = TestSetupType.BEFORE_ALL, index = 0)
	public void beforeAll_2() {}

	@TestSetup(value = TestSetupType.BEFORE_ALL, index = 1)
	public void beforeAll_3() {}
	
////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@TestSetup(value = TestSetupType.AFTER_ALL, index = 1)
	public void afterAll_1() {}

	@TestSetup(value = TestSetupType.AFTER_ALL, index = 2)
	public void afterAll_2() {}
	
	@TestSetup(value = TestSetupType.AFTER_ALL, index = 0)
	public void afterAll_3() {}

////////////////////////////////////////////////////////////////////////////////////////////////////

	@TestSetup(value = TestSetupType.BEFORE_EACH_OUT_MAIN_TX, index = 2)
	public void beforeEachOutTx_1() {}

	@TestSetup(value = TestSetupType.BEFORE_EACH_OUT_MAIN_TX, index = 1)
	public void beforeEachOutTx_2() {}
	
	@TestSetup(value = TestSetupType.BEFORE_EACH_OUT_MAIN_TX, index = 0)
	public void beforeEachOutTx_3() {}

////////////////////////////////////////////////////////////////////////////////////////////////////

	@TestSetup(value = TestSetupType.AFTER_EACH_OUT_MAIN_TX, index = 2)
	public void afterEachOutTx_1() {}

	@TestSetup(value = TestSetupType.AFTER_EACH_OUT_MAIN_TX, index = 1)
	public void afterEachOutTx_2() {}
	
	@TestSetup(value = TestSetupType.AFTER_EACH_OUT_MAIN_TX, index = 0)
	public void afterEachOutTx_3() {}

////////////////////////////////////////////////////////////////////////////////////////////////////

	@TestSetup(value = TestSetupType.BEFORE_EACH_IN_MAIN_TX, index = 2)
	public void beforeEachInTx_1() {}

	@TestSetup(value = TestSetupType.BEFORE_EACH_IN_MAIN_TX, index = 0)
	public void beforeEachInTx_2() {}
	
	@TestSetup(value = TestSetupType.BEFORE_EACH_IN_MAIN_TX, index = 1)
	public void beforeEachInTx_3() {}

////////////////////////////////////////////////////////////////////////////////////////////////////

	@TestSetup(value = TestSetupType.AFTER_EACH_IN_MAIN_TX, index = 2)
	public void afterEachInTx_1() {}

	@TestSetup(value = TestSetupType.AFTER_EACH_IN_MAIN_TX, index = 0)
	public void afterEachInTx_2() {}

	@TestSetup(value = TestSetupType.AFTER_EACH_IN_MAIN_TX, index = 1)
	public void afterEachInTx_3() {}

////////////////////////////////////////////////////////////////////////////////////////////////////

	@TestSetup(value = TestSetupType.BEFORE_OUT_MAIN_TX, refSetupKey = "thisIsARefKey", index = 2)
	public void beforeOutTx_1() {}

	@TestSetup(value = TestSetupType.BEFORE_OUT_MAIN_TX, refSetupKey = "thisIsARefKey", index = 1)
	public void beforeOutTx_2() {}
	
	@TestSetup(value = TestSetupType.BEFORE_OUT_MAIN_TX, refSetupKey = "thisIsARefKey", index = 0)
	public void beforeOutTx_3() {}

////////////////////////////////////////////////////////////////////////////////////////////////////

	@TestSetup(value = TestSetupType.AFTER_OUT_MAIN_TX, refSetupKey = "thisIsARefKey", index = 2)
	public void afterOutTx_1() {}
	
	@TestSetup(value = TestSetupType.AFTER_OUT_MAIN_TX, refSetupKey = "thisIsARefKey", index = 1)
	public void afterOutTx_2() {}

	@TestSetup(value = TestSetupType.AFTER_OUT_MAIN_TX, refSetupKey = "thisIsARefKey", index = 0)
	public void afterOutTx_3() {}

////////////////////////////////////////////////////////////////////////////////////////////////////

	@TestSetup(value = TestSetupType.BEFORE_IN_MAIN_TX, refSetupKey = "thisIsARefKey", index = 2)
	public void beforeInTx_1() {}
	
	@TestSetup(value = TestSetupType.BEFORE_IN_MAIN_TX, refSetupKey = "thisIsARefKey", index = 1)
	public void beforeInTx_2() {}
	
	@TestSetup(value = TestSetupType.BEFORE_IN_MAIN_TX, refSetupKey = "thisIsARefKey", index = 0)
	public void beforeInTx_3() {}

////////////////////////////////////////////////////////////////////////////////////////////////////

	@TestSetup(value = TestSetupType.AFTER_IN_MAIN_TX, refSetupKey = "thisIsARefKey", index = 2)
	public void afterInTx_1() {}
	
	@TestSetup(value = TestSetupType.AFTER_IN_MAIN_TX, refSetupKey = "thisIsARefKey", index = 1)
	public void afterInTx_2() {}
	
	@TestSetup(value = TestSetupType.AFTER_IN_MAIN_TX, refSetupKey = "thisIsARefKey", index = 0)
	public void afterInTx_3() {}

////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test(setupKey = "thisIsARefKey")
	public Description testMethodOne(Description description) {
		return description.pass();
	}

	@Override
	public TestGroup getTestGroup() {
		return this;
	}
}