package org.dockbox.hartshorn.config.beta;

import org.dockbox.hartshorn.config.FileFormat;
import org.dockbox.hartshorn.config.IncludeRule;

public interface SerializerConfigurer {

    SerializerConfigurer includeRule(IncludeRule rule);

    IncludeRule includeRule();

    SerializerConfigurer defaultFormat(FileFormat format);

    FileFormat defaultFormat();

}
