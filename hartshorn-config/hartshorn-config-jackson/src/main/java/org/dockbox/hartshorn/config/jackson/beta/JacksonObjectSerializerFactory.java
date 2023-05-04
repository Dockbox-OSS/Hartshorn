package org.dockbox.hartshorn.config.jackson.beta;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.config.FileFormat;
import org.dockbox.hartshorn.config.beta.ObjectSerializer;
import org.dockbox.hartshorn.config.beta.ObjectSerializerFactory;
import org.dockbox.hartshorn.config.beta.SerializerConfigurer;
import org.dockbox.hartshorn.config.jackson.JacksonObjectMapperConfigurator;
import org.dockbox.hartshorn.util.introspect.Introspector;

public class JacksonObjectSerializerFactory implements ObjectSerializerFactory {

    private final JacksonObjectMapperConfigurator configurator;
    private final ApplicationContext applicationContext;
    private final Introspector introspector;

    public JacksonObjectSerializerFactory(final JacksonObjectMapperConfigurator configurator,
                                          final ApplicationContext applicationContext,
                                          final Introspector introspector) {
        this.configurator = configurator;
        this.applicationContext = applicationContext;
        this.introspector = introspector;
    }

    @Override
    public ObjectSerializer create(final FileFormat fileFormat) {
        return new JacksonObjectSerializer(fileFormat,
                this.applicationContext,
                this.configurator);
    }

    @Override
    public ObjectSerializer create(final SerializerConfigurer configurer) {
        final StandardJacksonObjectMapperConfigurator configurator = new StandardJacksonObjectMapperConfigurator(this.introspector, configurer);
        return new JacksonObjectSerializer(configurer.defaultFormat(),
                this.applicationContext,
                configurator);
    }
}
