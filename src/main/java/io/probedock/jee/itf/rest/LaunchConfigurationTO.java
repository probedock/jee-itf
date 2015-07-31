package io.probedock.jee.itf.rest;

import java.util.List;

/**
 * Launch configuration to allow running the test with filters
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class LaunchConfigurationTO {
    private Long seed;

    private List<FilterDefinitionTO> filters;

    public Long getSeed() {
        return seed;
    }

    public void setSeed(Long seed) {
        this.seed = seed;
    }

    public List<FilterDefinitionTO> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterDefinitionTO> filters) {
        this.filters = filters;
    }

    @Override
    public String toString() {
        StringBuilder message = new StringBuilder("Launch configuration: {");

        if (filters != null) {
            message.append("Filters [");

            StringBuilder filterString = new StringBuilder();

            for (FilterDefinitionTO filter : filters) {
                filterString.append(filter.toString()).append(", ");
            }

            message.append(filterString.toString().replaceAll(", $", "")).append("]");
        }

        if (seed != null) {
            message.append("Seed [").append(seed).append("]");
        }

        return message.append("}").toString();
    }
}
