package io.probedock.jee.itf.listeners;

import io.probedock.jee.itf.model.Description;
import io.probedock.jee.itf.test.utils.ItfTestHelper;
import io.probedock.client.annotations.ProbeTest;
import io.probedock.client.annotations.ProbeTestClass;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
	
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link DefaultListener}
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
@ProbeTestClass(tags = "default-listener")
public class DefaultListenerTest {
	@Mock
  private Log mockLogger = LogFactory.getLog(DefaultListener.class);
    
	private Description description;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		try {
			ItfTestHelper.setFinalStatic(DefaultListener.class.getDeclaredField("LOGGER"), mockLogger);
		}
		catch (Exception e) {}
		
		description = ItfTestHelper.createDefaultDescription();
	}
	
	@After
	public void tearDown() {
		try {
			ItfTestHelper.setFinalStatic(DefaultListener.class.getDeclaredField("LOGGER"), LogFactory.getLog(DefaultListener.class));
		}
		catch (Exception e) {}
	}
	
	@Test
	@ProbeTest(key = "6afee6ec5d6e")
	public void specificMessagShouldBeLoggedWhenTestRunStartIsNotified() {
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) {
				assertEquals("The message [Test run started.] is expected", (String) invocation.getArguments()[0], "Test run started.");
				return null;
			}
		}).when(mockLogger).info(any(String.class));
		
		DefaultListener dl = new DefaultListener();
	
		dl.testRunStart();
	}

	@Test
	@ProbeTest(key = "70de791d4aa9")
	public void startDateShouldBeValidWhenTestRunStartIsNotified() {
		DefaultListener dl = new DefaultListener();
	
		dl.testRunStart();
		
		assertNotNull("The start date cannot be null when test run start notification is received", dl.startDate);
		assertTrue("The start date cannot be 0 or negative", dl.startDate > 0);
	}

	@Test
	@ProbeTest(key = "7a90e429cb33")
	public void specificMessageShouldBeLoggedWhenTestRunEndIsNotified() {
		final DefaultListener dl = new DefaultListener();

		dl.testRunStart();

		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) {
				assertEquals("The message [Test run ended in " + (dl.endDate - dl.startDate) + "ms.] is expected", 
					(String) invocation.getArguments()[0], "Test run ended in " + (dl.endDate - dl.startDate) + "ms.");
				return null;
			}
		}).when(mockLogger).info(any(String.class));
		
		dl.testRunEnd();
	}		

	@Test
	@ProbeTest(key = "79a5655f8b1a")
	public void endDateShouldBeValidWhenTestRunEndIsNotified() {
		DefaultListener dl = new DefaultListener();
	
		dl.testRunStart();
		dl.testRunEnd();
		
		assertNotNull("The end date cannot be null when test run start notification is received", dl.endDate);
		assertTrue("The end date cannot be 0 or negative", dl.endDate > 0);
		assertTrue("The end date cannot be lesser than start date", dl.endDate >= dl.startDate);
	}
	
	@Test
	@ProbeTest(key = "fb19c98fbd49")
	public void specificMessageShouldBeLoggedWhenTestStartIsNotified() {
		final DefaultListener dl = new DefaultListener();

		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) {
				assertEquals("The message [Test " + description.getName() + " started.] is expected", 
					(String) invocation.getArguments()[0], "Test " + description.getName() + " started.");
				return null;
			}
		}).when(mockLogger).info(any(String.class));
		
		dl.testStart(description);
	}		

	@Test
	@ProbeTest(key = "26f8953563f6")
	public void startDateShouldBeSetWhenTestStartIsNotified() {
		DefaultListener dl = new DefaultListener();

		dl.testStart(description);
		
		assertNotNull("The start date should be not null", description.getStartDate());
	}		

	@Test
	@ProbeTest(key = "4d6bb059864e")
	public void specificMessageShouldBeLoggedWhenTestEndIsNotified() {
		final DefaultListener dl = new DefaultListener();

		dl.testStart(description);

		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) {
				assertEquals("The message [Test " + description.getName() + " ended in " + description.getDuration() + "ms.] is expected", 
					(String) invocation.getArguments()[0], "Test " + description.getName() + " ended in " + description.getDuration() + "ms.");
				return null;
			}
		}).when(mockLogger).info(any(String.class));
		
		dl.testEnd(description);
	}		

	@Test
	@ProbeTest(key = "82154126c9f4")
	public void endDateShouldBeSetWhenTestEndIsNotified() {
		DefaultListener dl = new DefaultListener();

		dl.testStart(description);
		dl.testEnd(description);
		
		assertNotNull("The end date should be not null", description.getEndDate());
	}
	
	@Test
	@ProbeTest(key = "2058100dc7b7")
	public void durationShouldBeSetWhenTestEndIsNotified() {
		DefaultListener dl = new DefaultListener();

		dl.testStart(description);
		dl.testEnd(description);
		
		assertNotNull("The duration should be not null", description.getDuration());
		assertEquals("The duration should be the same as calculated manually", 
			description.getDuration(), description.getEndDate() - description.getStartDate());
	}
	
	@Test
	@ProbeTest(key = "d3a8e21f7dbd")
	public void specificMessageShouldBeLoggedWhenFailIsNotified() {
		final DefaultListener dl = new DefaultListener();

		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) {
				assertEquals("The message [Test " + description.getName() + " failed with message: " + description.getMessage() + "] is expected", 
					(String) invocation.getArguments()[0], "Test " + description.getName() + " failed with message: " + description.getMessage());
				return null;
			}
		}).when(mockLogger).info(any(String.class));

		dl.fail(description.fail("fail message"));
	}
	
	@Test
	@ProbeTest(key = "4642dd722f63")
	public void specificMessageShouldBeLoggedWhenPassIsNotifiedWithMessageForPassed() {
		final DefaultListener dl = new DefaultListener();

		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) {
				assertEquals("The message [Test " + description.getName() + " succeed with message: " + description.getMessage() + "] is expected", 
					(String) invocation.getArguments()[0], "Test " + description.getName() + " succeed with message: " + description.getMessage());
				return null;
			}
		}).when(mockLogger).info(any(String.class));

		dl.success(description.pass("pass message"));
	}

	@Test
	@ProbeTest(key = "9ceb8fbeabaf")
	public void specificMessageShouldBeLoggedWhenPassIsNotifiedWithoutMessageForPassed() {
		final DefaultListener dl = new DefaultListener();

		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) {
				assertEquals("The message [Test " + description.getName() + " succeed.] is expected", 
					(String) invocation.getArguments()[0], "Test " + description.getName() + " succeed.");
				return null;
			}
		}).when(mockLogger).info(any(String.class));

		dl.success(description.pass());
	}
}