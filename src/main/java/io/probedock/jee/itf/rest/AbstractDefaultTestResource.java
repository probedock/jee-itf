package io.probedock.jee.itf.rest;

import io.probedock.jee.itf.TestController;
import io.probedock.jee.itf.filters.DefaultFilter;
import io.probedock.jee.itf.filters.Filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Expose the method to start the integration tests through a REST service.
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public abstract class AbstractDefaultTestResource {
    private static final Log LOGGER = LogFactory.getLog(AbstractDefaultTestResource.class);

    /**
     * Start the test through the integration test controller
     *
     * @param configuration The configuration to launch the test run
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response run(LaunchConfigurationTO configuration) {
        // Logging
        LOGGER.info(configuration.toString());

        // Get the controller
        TestController testController = getController();

        Map<String, Filter> itfFilters = new HashMap<>();
        itfFilters.put("nameFilter", new DefaultFilter(configuration.getFilters() == null ? new ArrayList<FilterDefinitionTO>() : configuration.getFilters()));

        // Run the integration tests
        LOGGER.info("Generator seed: " + testController.run(itfFilters, null, configuration.getSeed()));

        return Response.ok().build();
    }

    /**
     * @return Retrieve the integration test controller
     */
    public abstract TestController getController();
}
