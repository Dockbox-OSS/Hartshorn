package org.dockbox.hartshorn.data.jackson;

import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.json.JsonMapper;

import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.data.FileFormat;
import org.dockbox.hartshorn.data.FileFormats;

import javax.inject.Named;

@Binds(value = JacksonDataMapper.class, named = @Named("json"))
public class JsonDataMapper implements JacksonDataMapper{

    @Override
    public FileFormat fileFormat() {
        return FileFormats.JSON;
    }

    @Override
    public MapperBuilder<?, ?> get() {
        return JsonMapper.builder();
    }
}
