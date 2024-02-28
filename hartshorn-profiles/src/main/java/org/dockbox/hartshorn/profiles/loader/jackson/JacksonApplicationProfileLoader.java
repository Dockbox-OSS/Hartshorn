package org.dockbox.hartshorn.profiles.loader.jackson;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Set;

import org.dockbox.hartshorn.profiles.ProfileProperty;
import org.dockbox.hartshorn.profiles.loader.ProfilePropertiesLoader;
import org.dockbox.hartshorn.util.ApplicationException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JacksonApplicationProfileLoader implements ProfilePropertiesLoader {

    protected abstract ObjectMapper createObjectMapper();

    @Override
    public Set<ProfileProperty> loadProperties(URI uri) throws ApplicationException {
        try {
            ObjectMapper mapper = this.createObjectMapper();
            InputStream inputStream = uri.toURL().openStream();
            JsonNode node = mapper.readTree(inputStream);
            return loadProperties(node);
        }
        catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    private Set<ProfileProperty> loadProperties(JsonNode node) {
        return new JsonNodeProfilePropertyCollector().collectProperties(node);
    }
}
