package org.dockbox.hartshorn.data.jackson;

import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;

import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.data.FileFormat;
import org.dockbox.hartshorn.data.FileFormats;

import javax.inject.Named;

@Binds(value = JacksonDataMapper.class, named = @Named("yml"))
public class YamlDataMapper implements JacksonDataMapper {
    @Override
    public FileFormat fileFormat() {
        return FileFormats.YAML;
    }

    @Override
    public MapperBuilder<?, ?> get() {
        final YAMLFactory yamlFactory = new YAMLFactory();
        yamlFactory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
        yamlFactory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        yamlFactory.disable(YAMLParser.Feature.EMPTY_STRING_AS_NULL);
        return YAMLMapper.builder(yamlFactory);
    }
}
