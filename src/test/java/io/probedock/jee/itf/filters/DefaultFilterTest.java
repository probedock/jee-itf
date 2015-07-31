package io.probedock.jee.itf.filters;

import io.probedock.jee.itf.model.Description;
import io.probedock.jee.itf.rest.FilterDefinitionTO;
import io.probedock.jee.itf.test.utils.ItfTestHelper;
import io.probedock.client.annotations.ProbeTest;
import io.probedock.client.annotations.ProbeTestClass;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for {@link DefaultFilter}
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
@ProbeTestClass(tags = "default-filter")
public class DefaultFilterTest {
	
	Description description;
	
	@Before
	public void createDescription() {
		description = ItfTestHelper.createDefaultDescription();
	}
	
	@Test
	@ProbeTest(key = "2ec6ce2c860b")
	public void descriptionShouldBeRunnableWhenNoFilterIsSpecified() {
		DefaultFilter df = new DefaultFilter(null);
		assertTrue("The test is not runnable when it must be (null filters)", df.isRunnable(description));

		df = new DefaultFilter(new ArrayList<FilterDefinitionTO>());
		assertTrue("The test is not runnable when it must be (empty filters)", df.isRunnable(description));
	}
	
	@Test
	@ProbeTest(key = "7472c092073a")
	public void descriptionShouldBeRunnableWhenValidFilterNameIsSpecified() {
		List<FilterDefinitionTO> filters = new ArrayList<>();
		filters.add(new FilterDefinitionTO("", "dummyMethod"));
		DefaultFilter df = new DefaultFilter(filters);
		assertTrue("The test is not runnable when it must be", df.isRunnable(description));
	}
	
	@Test
	@ProbeTest(key = "08ed5b8338ec")
	public void descriptionShouldBeRunnableWhenInvalidFilterNameIsSpecified() {
		List<FilterDefinitionTO> filters = new ArrayList<>();
		filters.add(new FilterDefinitionTO("", "noMethod"));
		DefaultFilter df = new DefaultFilter(filters);
		assertFalse("The test is runnable when it should not", df.isRunnable(description));
	}	
}
