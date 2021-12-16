package org.dockbox.hartshorn.data.jackson;

import com.fasterxml.jackson.databind.cfg.MapperBuilder;

import org.dockbox.hartshorn.data.FileFormat;

public interface JacksonDataMapper {
    FileFormat fileFormat();
    MapperBuilder<?, ?> get();
}
