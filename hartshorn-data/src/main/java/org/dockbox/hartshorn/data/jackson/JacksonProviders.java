package org.dockbox.hartshorn.data.jackson;

import org.dockbox.hartshorn.core.annotations.inject.Provider;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.data.annotations.UsePersistence;
import org.dockbox.hartshorn.data.mapping.ObjectMapper;

@Service(activators = UsePersistence.class)
public class JacksonProviders {

    @Provider("properties")
    private final JacksonDataMapper properties = new JavaPropsDataMapper();

    @Provider("json")
    private final JacksonDataMapper json = new JsonDataMapper();

    @Provider("toml")
    private final JacksonDataMapper toml = new TomlDataMapper();

    @Provider("xml")
    private final JacksonDataMapper xml = new XmlDataMapper();

    @Provider("yml")
    private final JacksonDataMapper yml = new YamlDataMapper();

    @Provider
    public ObjectMapper objectMapper() {
        if (TypeContext.lookup("com.fasterxml.jackson.databind.ObjectMapper").isVoid()) throw new IllegalStateException("Jackson is not available");
        return new JacksonObjectMapper();
    }
}
