package io.probedock.jee.itf.rest.provider;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Customization of Jackson serialization.
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
@Provider
@Produces("application/json")
public class JsonObjectMapperContextResolver implements ContextResolver<ObjectMapper> {
	/**
	 * Jackson object mapper with custom serialization configuration.
	 */
	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * Constructs a new context resolver.
	 */
	public JsonObjectMapperContextResolver() {
		mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
		mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, false);
	}

	@Override
	public ObjectMapper getContext(Class<?> arg0) {
		return mapper;
	}
}
