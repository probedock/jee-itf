package io.probedock.jee.itf;

import io.probedock.jee.itf.filters.Filter;
import io.probedock.jee.itf.listeners.Listener;
import io.probedock.jee.itf.model.Description;
import io.probedock.jee.itf.test.utils.ItfTestHelper;
import io.probedock.jee.itf.test.utils.controllers.DummyTestControllerWithFullSetup;
import io.probedock.jee.itf.test.utils.controllers.DummyTestControllerWithTestBeforeAfterAll;
import io.probedock.jee.itf.test.utils.controllers.DummyTestControllerWithTestBeforeAfterEachInTx;
import io.probedock.jee.itf.test.utils.controllers.DummyTestControllerWithTestBeforeAfterEachOutTx;
import io.probedock.jee.itf.test.utils.controllers.DummyTestControllerWithTestBeforeAfterInTx;
import io.probedock.jee.itf.test.utils.controllers.DummyTestControllerWithTestBeforeAfterOutTx;
import io.probedock.jee.itf.test.utils.controllers.DummyTestControllerWithTestException;
import io.probedock.jee.itf.test.utils.controllers.DummyTestControllerWithTestMethodCommit;
import io.probedock.jee.itf.test.utils.controllers.DummyTestControllerWithTestMethodRollback;
import io.probedock.client.annotations.ProbeTest;
import io.probedock.client.annotations.ProbeTestClass;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link AbstractTestController}
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
@ProbeTestClass(tags = "test-controller")
public class TestControllerTest {
	@Mock
	private SessionContext sessionContext;
	
	@InjectMocks
  private DummyTestControllerWithoutAnnotation testControllerWithoutAnnotation;

	@InjectMocks
  private DummyTestControllerWithIncorrectAnnotation testControllerWithIncorrectAnnotation;
	
	@InjectMocks
  private DummyTestControllerWithAnnotation testControllerWithAnnotation;

	@Before
	public void setup() {
		testControllerWithoutAnnotation = new DummyTestControllerWithoutAnnotation();
		testControllerWithIncorrectAnnotation = new DummyTestControllerWithIncorrectAnnotation();
		testControllerWithAnnotation = new DummyTestControllerWithAnnotation();
		
		MockitoAnnotations.initMocks(this);
		
		when(sessionContext.getUserTransaction()).thenReturn(
			new UserTransaction() {
				@Override public void begin() throws NotSupportedException, SystemException {}
				@Override public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {}
				@Override public void rollback() throws IllegalStateException, SecurityException, SystemException {}
				@Override public void setRollbackOnly() throws IllegalStateException, SystemException {}
				@Override public int getStatus() throws SystemException { return 1; }
				@Override public void setTransactionTimeout(int i) throws SystemException {}
			}
		);
	}
	
	@Test
	@ProbeTest(key = "60ef3c465c71")
	public void testControllerWithoutTransactionManagementAnnotationShouldThrowRuntimeAnnotationWhenRunMethodIsCalled() {
		try {
			testControllerWithoutAnnotation.run(null, null, null);
		}
		catch (RuntimeException re) {
			if (!re.getMessage().contains("The TransactionManagement annotation is missing")) {
				fail("When no annotation for transaction management is present on the test controller, an exception should be thrown mentioning it.");
			}
		}
	}
	
	@Test 
	@ProbeTest(key = "ff1d4865c9da")
	public void testControllerWithIncorrectTransactionManagementAnnotationShouldThrowRuntimeAnnotationWhenRunMethodIsCalled() {
		try {
			testControllerWithIncorrectAnnotation.run(null, null, null);
		}
		catch (RuntimeException re) {
			if (!re.getMessage().contains("Your test controller is annotated with TransactionManagement but you should use TransactionManagementType.BEAN as the value for the annotation.")) {
				fail("When annotation for transaction management is present on the test controller but wrongly configured, an exception should be thrown mentioning it.");
			}
		}
	}
	
	@Test
	@ProbeTest(key = "affdaded8ef5")
	public void testControllerWithCorrectTransactionManagementAnnotationShouldNotThrowRuntimeAnnotationWhenRunMethodIsCalled() {
		testControllerWithAnnotation.run(null, null, null);
	}

	@Test
	@ProbeTest(key = "3f80fe843aee")
	public void testShouldBeRollbedBack() {
		SessionContext sc = mock(SessionContext.class);
		UserTransaction ut = mock(UserTransaction.class);
		
		TestController testController = new DummyTestControllerWithTestMethodRollback();
		
		when(sc.getUserTransaction()).thenReturn(ut);
		
		Whitebox.setInternalState(testController, "sessionContext", sc);
		
		testController.run(null, null, null);
		
		try {
			verify(ut).begin();
			verify(ut).rollback();
			verify(ut, never()).commit();
		}
		catch (Exception e) {}
	}
	
	@Test
	@ProbeTest(key = "e979a40f9644")
	public void testShouldBeCommited() {
		SessionContext sc = mock(SessionContext.class);
		UserTransaction ut = mock(UserTransaction.class);
		
		TestController testController = new DummyTestControllerWithTestMethodCommit();
		
		when(sc.getUserTransaction()).thenReturn(ut);
		
		Whitebox.setInternalState(testController, "sessionContext", sc);
		
		testController.run(null, null, null);
		
		try {
			verify(ut).begin();
			verify(ut).commit();
			verify(ut, never()).rollback();
		}
		catch (Exception e) {}
	}
	
	@Test
	@ProbeTest(key = "5e34bfb93bca")
	public void beforeAndAfterAllOnControllerWithTwoTestShouldBeCommited() {
		SessionContext sc = mock(SessionContext.class);
		UserTransaction ut = mock(UserTransaction.class);
		
		TestController testController = new DummyTestControllerWithTestBeforeAfterAll();
		
		when(sc.getUserTransaction()).thenReturn(ut);
		
		Whitebox.setInternalState(testController, "sessionContext", sc);
		
		testController.run(null, null, null);
		
		try {
			verify(ut, times(4)).begin(); // four transactions started
			verify(ut, times(2)).commit(); // two commited
			verify(ut, times(2)).rollback(); // two rollbacked
		}
		catch (Exception e) {}
	}
	
	@Test
	@ProbeTest(key = "d5c8bf9ff987")
	public void beforeAndAfterEachOutTxOnControllerWithTwoTestShouldBeCommited() {
		SessionContext sc = mock(SessionContext.class);
		UserTransaction ut = mock(UserTransaction.class);
		
		TestController testController = new DummyTestControllerWithTestBeforeAfterEachOutTx();
		
		when(sc.getUserTransaction()).thenReturn(ut);
		
		Whitebox.setInternalState(testController, "sessionContext", sc);
		
		testController.run(null, null, null);
		
		try {
			verify(ut, times(6)).begin(); // six transactions started
			verify(ut, times(4)).commit(); // four commited
			verify(ut, times(2)).rollback(); // two rollbacked
		}
		catch (Exception e) {}
	}
	
	@Test
	@ProbeTest(key = "7909134d5c9f")
	public void beforeAndAfterEachInTxOnControllerWithTwoTestShouldBeCommited() {
		SessionContext sc = mock(SessionContext.class);
		UserTransaction ut = mock(UserTransaction.class);
		
		TestController testController = new DummyTestControllerWithTestBeforeAfterEachInTx();
		
		when(sc.getUserTransaction()).thenReturn(ut);
		
		Whitebox.setInternalState(testController, "sessionContext", sc);
		
		testController.run(null, null, null);
		
		try {
			verify(ut, times(2)).begin(); // two transactions started
			verify(ut, never()).commit(); // never commited
			verify(ut, times(2)).rollback(); // two rollbacked
		}
		catch (Exception e) {}
	}
	
	@Test
	@ProbeTest(key = "110e3d10deb4")
	public void beforeAndAfterOutTxOnControllerWithTwoTestShouldBeCommited() {
		SessionContext sc = mock(SessionContext.class);
		UserTransaction ut = mock(UserTransaction.class);
		
		TestController testController = new DummyTestControllerWithTestBeforeAfterOutTx();
		
		when(sc.getUserTransaction()).thenReturn(ut);
		
		Whitebox.setInternalState(testController, "sessionContext", sc);
		
		testController.run(null, null, null);
		
		try {
			verify(ut, times(4)).begin(); // four transactions started
			verify(ut, times(2)).commit(); // two commited
			verify(ut, times(2)).rollback(); // two rollbacked
		}
		catch (Exception e) {}
	}

	@Test
	@ProbeTest(key = "3a88b8abdd1b")
	public void beforeAndAfterInTxOnControllerWithTwoTestShouldBeCommited() {
		SessionContext sc = mock(SessionContext.class);
		UserTransaction ut = mock(UserTransaction.class);
		
		TestController testController = new DummyTestControllerWithTestBeforeAfterInTx();
		
		when(sc.getUserTransaction()).thenReturn(ut);
		
		Whitebox.setInternalState(testController, "sessionContext", sc);
		
		testController.run(null, null, null);
		
		try {
			verify(ut, times(2)).begin(); // two transactions started
			verify(ut, never()).commit(); // never commited
			verify(ut, times(2)).rollback(); // two rollbacked
		}
		catch (Exception e) {}
	}

	@Test
	@ProbeTest(key = "db065140185b")
	public void commitAndRollbackShouldBeWellManagedWhenFullSetupIsUsed() {
		SessionContext sc = mock(SessionContext.class);
		UserTransaction ut = mock(UserTransaction.class);
		
		TestController testController = new DummyTestControllerWithFullSetup();
		
		when(sc.getUserTransaction()).thenReturn(ut);
		
		Whitebox.setInternalState(testController, "sessionContext", sc);
		
		testController.run(null, null, null);
		
		try {
			verify(ut, times(10)).begin(); // ten transactions started
			verify(ut, times(8)).commit(); // eight commited
			verify(ut, times(2)).rollback(); // two rollbacked
		}
		catch (Exception e) {}
	}
	
	@Test
	@ProbeTest(key = "d4b5af990163")
	public void testWithExceptionShouldContainsTheStackTrace() {
		SessionContext sc = mock(SessionContext.class);
		UserTransaction ut = mock(UserTransaction.class);
		
		TestController testController = new DummyTestControllerWithTestException();
		
		when(sc.getUserTransaction()).thenReturn(ut);
		
		Whitebox.setInternalState(testController, "sessionContext", sc);
		
		testController.run(
			null, 
			createListenerMap(
				"customListener", 
				new Listener() {
					@Override public void testRunStart() {}
					@Override public void testRunEnd() {}
					@Override public void testStart(Description description) {}
					@Override public void testEnd(Description description) {}
					@Override public void success(Description description) {}

					@Override
					public void fail(Description description) {
						assertTrue("A test with an exception should contain a stack trace", description.getMessage().contains("RuntimeException"));
					}
				}
			), 
			null
		);
	}
	
	@Test
	@ProbeTest(key = "c71560eff86b")
	public void whenTwoListenersAreRegisteredBothMustBeCalledWhenTestAreRun() {
		SessionContext sc = mock(SessionContext.class);
		UserTransaction ut = mock(UserTransaction.class);
		
		TestController testController = new DummyTestControllerWithTestMethodRollback();
		
		when(sc.getUserTransaction()).thenReturn(ut);
		
		Whitebox.setInternalState(testController, "sessionContext", sc);
		
		Listener l1 = mock(Listener.class);
		Listener l2 = mock(Listener.class);
		
		Map<String, Listener> listeners = new HashMap<String, Listener>();
		
		listeners.put("l1", l1);
		listeners.put("l2", l2);
		
		testController.run(null, listeners, null);
		
		verify(l1).testRunStart();
		verify(l2).testRunStart();
		
		verify(l1).testRunEnd();
		verify(l2).testRunEnd();
		
		verify(l1).testStart(any(Description.class));
		verify(l2).testStart(any(Description.class));
		
		verify(l1).testEnd(any(Description.class));
		verify(l2).testEnd(any(Description.class));
		
		verify(l1).success(any(Description.class));
		verify(l2).success(any(Description.class));
	}
	
	@Test
	@ProbeTest(key = "af780f73a0a4")
	public void whenTwoFiltersAreRegisteredBothMustBeCalledWhenTestAreRun() {
		SessionContext sc = mock(SessionContext.class);
		UserTransaction ut = mock(UserTransaction.class);
		
		TestController testController = new DummyTestControllerWithTestMethodRollback();
		
		when(sc.getUserTransaction()).thenReturn(ut);
		
		Whitebox.setInternalState(testController, "sessionContext", sc);
		
		Filter f1 = mock(Filter.class);
		Filter f2 = mock(Filter.class);
		
		when(f1.isRunnable(any(Description.class))).thenReturn(true);
		when(f2.isRunnable(any(Description.class))).thenReturn(true);
		
		Map<String, Filter> filters = new HashMap<String, Filter>();
		
		filters.put("f1", f1);
		filters.put("f2", f2);
		
		testController.run(filters, null, null);
		
		verify(f1).isRunnable(any(Description.class));
		verify(f2).isRunnable(any(Description.class));
	}
	
	private class DummyTestControllerWithoutAnnotation extends AbstractTestController {
		@EJB
		public TestGroup testGroup = ItfTestHelper.createDefaultTestGroup();
	}
	
	@TransactionManagement(TransactionManagementType.CONTAINER)
	private class DummyTestControllerWithIncorrectAnnotation extends AbstractTestController {
		@EJB
		public TestGroup testGroup = ItfTestHelper.createDefaultTestGroup();
	}

	@TransactionManagement(TransactionManagementType.BEAN)
	private class DummyTestControllerWithAnnotation extends AbstractTestController {
		@EJB
		public TestGroup testGroup = ItfTestHelper.createDefaultTestGroup();
	}
	
	@TransactionManagement(TransactionManagementType.BEAN)
	private class DummyTestControllerWithPrivateTestGroup extends AbstractTestController {
		@EJB
		private TestGroup testGroup = ItfTestHelper.createDefaultTestGroup();
	}
	
	private Map<String, Listener> createListenerMap(String name, Listener l) {
		Map<String, Listener> map = new HashMap<String, Listener>();
		
		map.put(name, l);
		
		return map;
	}
	
	private Map<String, Filter> createFilterMap(String name, Filter f) {
		Map<String, Filter> map = new HashMap<String, Filter>();
		
		map.put(name, f);
		
		return map;
	}
}
