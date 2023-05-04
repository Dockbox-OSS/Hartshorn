package org.dockbox.hartshorn.config.beta;

import org.dockbox.hartshorn.config.FileFormat;

public interface ObjectSerializerFactory {

    ObjectSerializer create(FileFormat fileFormat);

    ObjectSerializer create(SerializerConfigurer configurer);

}
