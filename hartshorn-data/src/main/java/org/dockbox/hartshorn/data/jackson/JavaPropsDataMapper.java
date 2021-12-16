package org.dockbox.hartshorn.data.jackson;

import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;

import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.data.FileFormat;
import org.dockbox.hartshorn.data.FileFormats;

import javax.inject.Named;

@Binds(value = JacksonDataMapper.class, named = @Named("properties"))
public class JavaPropsDataMapper implements JacksonDataMapper {
    @Override
    public FileFormat fileFormat() {
        return FileFormats.PROPERTIES;
    }

    @Override
    public MapperBuilder<?, ?> get() {
        return JavaPropsMapper.builder();
    }
}
