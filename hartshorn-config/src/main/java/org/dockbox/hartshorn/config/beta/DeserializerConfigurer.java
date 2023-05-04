package org.dockbox.hartshorn.config.beta;

import org.dockbox.hartshorn.config.FileFormat;

public interface DeserializerConfigurer {

    DeserializerConfigurer defaultFormat(FileFormat format);

}
