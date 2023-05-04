package org.dockbox.hartshorn.config.beta;

import org.dockbox.hartshorn.config.FileFormat;

public interface ObjectSerializer {
    <T> SerializerFunction serialize(T object);

    <T> SerializerFunction serialize(T object, FileFormat format);

    FileFormat defaultFormat();
}
