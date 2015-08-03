package io.probedock.jee.itf.listeners;

import io.probedock.jee.itf.model.Description;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A default implementation of {@link Listener}
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class DefaultListener implements Listener {

	private static final Log LOGGER = LogFactory.getLog(DefaultListener.class);

	private String listenerName;

	/**
	 * Start and end dates of the test run
	 */
	protected long startDate;
	protected long endDate;

	public DefaultListener(String listenerName) {
		this.listenerName = listenerName;
	}

	@Override
	public void testRunStart() {
		startDate = System.currentTimeMillis();
		LOGGER.info("[" + listenerName + "] Test run started.");
	}

	@Override
	public void testRunEnd() {
		endDate = System.currentTimeMillis();
		LOGGER.info("[" + listenerName + "] Test run ended in " + (endDate - startDate) + "ms.");
	}

	@Override
	public void testStart(Description description) {
		description.setStartDate(System.currentTimeMillis());
		LOGGER.info("[" + listenerName + "] Test " + description.getName() + " started.");
	}

	@Override
	public void testEnd(Description description) {
		description.setEndDate(System.currentTimeMillis());
		description.setDuration(description.getEndDate() - description.getStartDate());
		LOGGER.info("[" + listenerName + "] Test " + description.getName() + " ended in " + description.getDuration() + "ms.");
	}

	@Override
	public void fail(Description description) {
		LOGGER.info("[" + listenerName + "] Test " + description.getName() + " failed with message: " + description.getMessage());
	}

	@Override
	public void success(Description description) {
		if (description.getMessage() != null) {
			LOGGER.info("[" + listenerName + "] Test " + description.getName() + " succeed with message: " + description.getMessage());
		}
		else {
			LOGGER.info("[" + listenerName + "] Test " + description.getName() + " succeed.");
		}
	}
}
