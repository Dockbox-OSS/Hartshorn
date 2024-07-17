package org.dockbox.hartshorn.properties.loader.support;

import java.nio.file.Path;

import org.dockbox.hartshorn.properties.loader.StandardPropertyPathFormatter;
import org.dockbox.hartshorn.properties.loader.path.PropertyPathFormatter;
import org.dockbox.hartshorn.util.Customizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;

public class JacksonJavaPropsPropertyRegistryLoader extends JacksonPropertyRegistryLoader {

    private final Customizer<JavaPropsMapper.Builder> customizer;

    public JacksonJavaPropsPropertyRegistryLoader() {
        this(new StandardPropertyPathFormatter());
    }

    public JacksonJavaPropsPropertyRegistryLoader(PropertyPathFormatter formatter) {
        this(formatter, Customizer.useDefaults());
    }

    public JacksonJavaPropsPropertyRegistryLoader(PropertyPathFormatter formatter, Customizer<JavaPropsMapper.Builder> customizer) {
        super(formatter);
        this.customizer = customizer;
    }

    @Override
    protected ObjectMapper createObjectMapper() {
        JavaPropsMapper.Builder builder = JavaPropsMapper.builder();
        this.customizer.configure(builder);
        return builder.build();
    }

    @Override
    public boolean isCompatible(Path path) {
        return path.toString().endsWith(".properties");
    }
}
