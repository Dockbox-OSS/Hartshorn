package org.dockbox.hartshorn.config.jackson.beta;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.condition.RequiresClass;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.config.annotations.UseConfigurations;
import org.dockbox.hartshorn.config.beta.ObjectSerializerFactory;
import org.dockbox.hartshorn.config.jackson.JacksonDataMapper;
import org.dockbox.hartshorn.config.jackson.JacksonObjectMapperConfigurator;
import org.dockbox.hartshorn.config.jackson.mapping.JavaPropsDataMapper;
import org.dockbox.hartshorn.config.jackson.mapping.JsonDataMapper;
import org.dockbox.hartshorn.config.jackson.mapping.TomlDataMapper;
import org.dockbox.hartshorn.config.jackson.mapping.XmlDataMapper;
import org.dockbox.hartshorn.config.jackson.mapping.YamlDataMapper;
import org.dockbox.hartshorn.util.introspect.Introspector;

@Service
@RequiresActivator(UseConfigurations.class)
@RequiresClass("com.fasterxml.jackson.databind.ObjectMapper")
public class JacksonBetaProviders {

    @Binds
    public ObjectSerializerFactory serializerFactory(final JacksonObjectMapperConfigurator configurator,
                                                     final ApplicationContext applicationContext,
                                                     final Introspector introspector
    ) {
        return new JacksonObjectSerializerFactory(configurator, applicationContext, introspector);
    }

    @Binds("properties")
    @RequiresClass("com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper")
    public JacksonDataMapper properties() {
        return new JavaPropsDataMapper();
    }

    @Binds("json")
    @RequiresClass("com.fasterxml.jackson.databind.json.JsonMapper")
    public JacksonDataMapper json() {
        return new JsonDataMapper();
    }

    @Binds("toml")
    @RequiresClass("com.fasterxml.jackson.dataformat.toml.TomlMapper")
    public JacksonDataMapper toml() {
        return new TomlDataMapper();
    }

    @Binds("xml")
    @RequiresClass("com.fasterxml.jackson.dataformat.xml.XmlMapper")
    public JacksonDataMapper xml() {
        return new XmlDataMapper();
    }

    @Binds("yml")
    @RequiresClass("com.fasterxml.jackson.dataformat.yaml.YAMLMapper")
    public JacksonDataMapper yml() {
        return new YamlDataMapper();
    }
}
