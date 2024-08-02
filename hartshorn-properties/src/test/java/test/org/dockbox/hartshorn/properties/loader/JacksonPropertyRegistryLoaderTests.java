package test.org.dockbox.hartshorn.properties.loader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import org.dockbox.hartshorn.properties.MapPropertyRegistry;
import org.dockbox.hartshorn.properties.PropertyRegistry;
import org.dockbox.hartshorn.properties.loader.support.JacksonPropertyRegistryLoader;
import org.dockbox.hartshorn.properties.loader.support.JacksonYamlPropertyRegistryLoader;
import org.dockbox.hartshorn.properties.loader.StandardPropertyPathFormatter;
import org.dockbox.hartshorn.properties.value.StandardPropertyParsers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JacksonPropertyRegistryLoaderTests {

    @Test
    void testComplexYamlConfigurationCanBeLoaded() throws IOException {
        // Given
        JacksonPropertyRegistryLoader loader = new JacksonYamlPropertyRegistryLoader(new StandardPropertyPathFormatter());
        Path path = Path.of("src/test/resources/complex-configuration.yml");

        // When: Loading registry
        PropertyRegistry registry = new MapPropertyRegistry();
        loader.loadRegistry(registry, path);

        // Then: Should contain all expected keys
        List<String> keys = registry.keys();
        Assertions.assertEquals(12, keys.size());

        // Then: Keys should be ordered
        Iterator<String> iterator = keys.iterator();
        Assertions.assertEquals(iterator.next(), "sample.complex.configuration[0].name");
        Assertions.assertEquals(iterator.next(), "sample.complex.configuration[0].value");
        Assertions.assertEquals(iterator.next(), "sample.complex.configuration[1].name");
        Assertions.assertEquals(iterator.next(), "sample.complex.configuration[1].value");
        Assertions.assertEquals(iterator.next(), "sample.complex.configuration[2].name");
        Assertions.assertEquals(iterator.next(), "sample.complex.configuration[2].value");
        Assertions.assertEquals(iterator.next(), "sample.complex.flat");
        Assertions.assertEquals(iterator.next(), "sample.complex.list[0].name");
        Assertions.assertEquals(iterator.next(), "sample.complex.list[0].value");
        Assertions.assertEquals(iterator.next(), "sample.complex.list[1].name");
        Assertions.assertEquals(iterator.next(), "sample.complex.list[1].value");
        Assertions.assertEquals(iterator.next(), "sample.complex.values");

        // Then: Property values should be loaded correctly
        registry.value("sample.complex.configuration[0].name")
                .peek(value -> Assertions.assertEquals("name1", value))
                .orElseThrow(() -> new AssertionError("Property not found"));
        registry.value("sample.complex.configuration[0].value")
                .peek(value -> Assertions.assertEquals("value1", value))
                .orElseThrow(() -> new AssertionError("Property not found"));

        registry.value("sample.complex.configuration[1].name")
                .peek(value -> Assertions.assertEquals("name2", value))
                .orElseThrow(() -> new AssertionError("Property not found"));
        registry.value("sample.complex.configuration[1].value")
                .peek(value -> Assertions.assertEquals("value2", value))
                .orElseThrow(() -> new AssertionError("Property not found"));

        registry.value("sample.complex.configuration[2].name")
                .peek(value -> Assertions.assertEquals("name3", value))
                .orElseThrow(() -> new AssertionError("Property not found"));
        registry.value("sample.complex.configuration[2].value")
                .peek(value -> Assertions.assertEquals("value3", value))
                .orElseThrow(() -> new AssertionError("Property not found"));

        registry.value("sample.complex.values", StandardPropertyParsers.STRING_LIST)
                .peek(values -> {
                    Assertions.assertEquals(3, values.length);
                    Assertions.assertEquals("value1", values[0]);
                    Assertions.assertEquals("value2", values[1]);
                    Assertions.assertEquals("value3", values[2]);
                })
                .orElseThrow(() -> new AssertionError("Property not found"));

        registry.value("sample.complex.flat")
                .peek(value -> Assertions.assertEquals("value1", value))
                .orElseThrow(() -> new AssertionError("Property not found"));

        registry.value("sample.complex.list[0].name")
                .peek(value -> Assertions.assertEquals("name1", value))
                .orElseThrow(() -> new AssertionError("Property not found"));
        registry.value("sample.complex.list[0].value")
                .peek(value -> Assertions.assertEquals("value1", value))
                .orElseThrow(() -> new AssertionError("Property not found"));

        registry.value("sample.complex.list[1].name")
                .peek(value -> Assertions.assertEquals("name2", value))
                .orElseThrow(() -> new AssertionError("Property not found"));
        registry.value("sample.complex.list[1].value")
                .peek(value -> Assertions.assertEquals("value2", value))
                .orElseThrow(() -> new AssertionError("Property not found"));
    }
}
