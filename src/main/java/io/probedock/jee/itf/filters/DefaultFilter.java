package io.probedock.jee.itf.filters;

import io.probedock.jee.itf.model.Description;
import io.probedock.jee.itf.rest.FilterDefinitionTO;

import java.util.List;

/**
 * A default implementation of {@link Filter}
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class DefaultFilter implements Filter {
	/**
	 * Define the filters to apply
	 */
	protected List<FilterDefinitionTO> filters;
	
	public DefaultFilter(List<FilterDefinitionTO> filters) {
		this.filters = filters;
	}
	
	@Override
	public boolean isRunnable(Description description) {
		// The test is deactivated
		if (!description.isRunnable()) {
			return false;
		}
		
		// No filters defined
		else if (filters == null || filters.size() == 0) {
			return true;
		}

		// Check filters
		else {
			for (FilterDefinitionTO filter : filters) {
				if (description.getSimpleName().contains(filter.getText())) {
					return true;
				}
			}

			return false;
		}
	}
}
