package org.dockbox.hartshorn.config.jackson.beta;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.config.FileFormat;
import org.dockbox.hartshorn.config.beta.ObjectSerializer;
import org.dockbox.hartshorn.config.beta.SerializerFunction;
import org.dockbox.hartshorn.config.jackson.JacksonDataMapper;
import org.dockbox.hartshorn.config.jackson.JacksonObjectMapperConfigurator;

public class JacksonObjectSerializer implements ObjectSerializer {

    private final FileFormat defaultFormat;
    private final ApplicationContext applicationContext;
    private final JacksonObjectMapperConfigurator objectMapperConfigurator;

    public JacksonObjectSerializer(final FileFormat defaultFormat,
                                   final ApplicationContext applicationContext,
                                   final JacksonObjectMapperConfigurator objectMapperConfigurator) {
        this.defaultFormat = defaultFormat;
        this.applicationContext = applicationContext;
        this.objectMapperConfigurator = objectMapperConfigurator;
    }

    @Override
    public <T> SerializerFunction serialize(final T object) {
        return this.serialize(object, this.defaultFormat());
    }

    @Override
    public <T> SerializerFunction serialize(final T object, final FileFormat format) {
        final ComponentKey<JacksonDataMapper> componentKey = ComponentKey.of(JacksonDataMapper.class, format.extension());
        final JacksonDataMapper dataMapper = this.applicationContext.get(componentKey);

        return new JacksonSerializerFunction(this.objectMapperConfigurator,
                dataMapper, object, format);
    }

    @Override
    public FileFormat defaultFormat() {
        return this.defaultFormat;
    }
}
