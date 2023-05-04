package org.dockbox.hartshorn.config.beta;

import org.dockbox.hartshorn.util.GenericType;

public interface ObjectDeserializer {
    <T> DeserializerFunction<T> deserialize(Class<T> type);

    <T> DeserializerFunction<T> deserialize(GenericType<T> type);
}
