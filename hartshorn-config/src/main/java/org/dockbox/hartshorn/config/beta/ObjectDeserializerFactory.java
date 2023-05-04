package org.dockbox.hartshorn.config.beta;

public interface ObjectDeserializerFactory {

    ObjectDeserializer create();

    ObjectDeserializer create(DeserializerConfigurer configurer);
}
