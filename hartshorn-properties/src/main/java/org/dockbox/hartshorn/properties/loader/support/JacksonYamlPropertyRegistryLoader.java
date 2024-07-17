package org.dockbox.hartshorn.properties.loader.support;

import java.nio.file.Path;

import org.dockbox.hartshorn.properties.loader.StandardPropertyPathFormatter;
import org.dockbox.hartshorn.properties.loader.path.PropertyPathFormatter;
import org.dockbox.hartshorn.util.Customizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class JacksonYamlPropertyRegistryLoader extends JacksonPropertyRegistryLoader {

    private final Customizer<YAMLMapper.Builder> customizer;

    public JacksonYamlPropertyRegistryLoader() {
        this(new StandardPropertyPathFormatter());
    }

    public JacksonYamlPropertyRegistryLoader(PropertyPathFormatter formatter) {
        this(formatter, Customizer.useDefaults());
    }

    public JacksonYamlPropertyRegistryLoader(PropertyPathFormatter formatter, Customizer<YAMLMapper.Builder> customizer) {
        super(formatter);
        this.customizer = customizer;
    }

    @Override
    protected ObjectMapper createObjectMapper() {
        YAMLMapper.Builder builder = YAMLMapper.builder();
        this.customizer.configure(builder);
        return builder.build();
    }

    @Override
    public boolean isCompatible(Path path) {
        return path.toString().endsWith(".yaml") || path.toString().endsWith(".yml");
    }
}
