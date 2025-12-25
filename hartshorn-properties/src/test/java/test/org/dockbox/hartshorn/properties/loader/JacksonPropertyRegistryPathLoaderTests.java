/*
 * Copyright 2019-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.org.dockbox.hartshorn.properties.loader;

import org.dockbox.hartshorn.properties.ConfiguredProperty;
import org.dockbox.hartshorn.properties.MapPropertyRegistry;
import org.dockbox.hartshorn.properties.PropertyRegistry;
import org.dockbox.hartshorn.properties.loader.PropertyRegistryPathLoader;
import org.dockbox.hartshorn.properties.loader.support.JacksonYamlPropertyRegistryLoader;
import org.dockbox.hartshorn.properties.value.StandardValuePropertyParsers;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JacksonPropertyRegistryPathLoaderTests {

    @Test
    void complexYamlConfigurationCanBeLoaded() throws Exception {
        // Given
        PropertyRegistryPathLoader loader = new JacksonYamlPropertyRegistryLoader();
        Path path = Path.of("src/test/resources/complex-configuration.yml");

        // When: Loading registry
        PropertyRegistry registry = new MapPropertyRegistry();
        loader.loadRegistry(registry, path.toUri());

        // Then: Should contain all expected keys
        List<ConfiguredProperty> properties = registry.find(property -> true);
        assertThat(properties).hasSize(12);

        // Then: Keys should be ordered
        Iterator<ConfiguredProperty> iterator = properties.iterator();
        assertThat(iterator.next().name()).isEqualTo("sample.complex.configuration[0].name");
        assertThat(iterator.next().name()).isEqualTo("sample.complex.configuration[0].value");
        assertThat(iterator.next().name()).isEqualTo("sample.complex.configuration[1].name");
        assertThat(iterator.next().name()).isEqualTo("sample.complex.configuration[1].value");
        assertThat(iterator.next().name()).isEqualTo("sample.complex.configuration[2].name");
        assertThat(iterator.next().name()).isEqualTo("sample.complex.configuration[2].value");
        assertThat(iterator.next().name()).isEqualTo("sample.complex.flat");
        assertThat(iterator.next().name()).isEqualTo("sample.complex.list[0].name");
        assertThat(iterator.next().name()).isEqualTo("sample.complex.list[0].value");
        assertThat(iterator.next().name()).isEqualTo("sample.complex.list[1].name");
        assertThat(iterator.next().name()).isEqualTo("sample.complex.list[1].value");
        assertThat(iterator.next().name()).isEqualTo("sample.complex.values");

        // Then: Property values should be loaded correctly
        registry.value("sample.complex.configuration[0].name")
            .peek(value -> assertThat(value).isEqualTo("name1"))
            .orElseThrow(() -> new AssertionError("Property not found"));
        registry.value("sample.complex.configuration[0].value")
            .peek(value -> assertThat(value).isEqualTo("value1"))
            .orElseThrow(() -> new AssertionError("Property not found"));

        registry.value("sample.complex.configuration[1].name")
            .peek(value -> assertThat(value).isEqualTo("name2"))
            .orElseThrow(() -> new AssertionError("Property not found"));
        registry.value("sample.complex.configuration[1].value")
            .peek(value -> assertThat(value).isEqualTo("value2"))
            .orElseThrow(() -> new AssertionError("Property not found"));

        registry.value("sample.complex.configuration[2].name")
            .peek(value -> assertThat(value).isEqualTo("name3"))
            .orElseThrow(() -> new AssertionError("Property not found"));
        registry.value("sample.complex.configuration[2].value")
            .peek(value -> assertThat(value).isEqualTo("value3"))
            .orElseThrow(() -> new AssertionError("Property not found"));

        registry.value("sample.complex.values", StandardValuePropertyParsers.STRING_LIST)
            .peek(values -> {
                assertThat(values.length).isEqualTo(3);
                assertThat(values[0]).isEqualTo("value1");
                assertThat(values[1]).isEqualTo("value2");
                assertThat(values[2]).isEqualTo("value3");
            })
            .orElseThrow(() -> new AssertionError("Property not found"));

        registry.value("sample.complex.flat")
            .peek(value -> assertThat(value).isEqualTo("value1"))
            .orElseThrow(() -> new AssertionError("Property not found"));

        registry.value("sample.complex.list[0].name")
            .peek(value -> assertThat(value).isEqualTo("name1"))
            .orElseThrow(() -> new AssertionError("Property not found"));
        registry.value("sample.complex.list[0].value")
            .peek(value -> assertThat(value).isEqualTo("value1"))
            .orElseThrow(() -> new AssertionError("Property not found"));

        registry.value("sample.complex.list[1].name")
            .peek(value -> assertThat(value).isEqualTo("name2"))
            .orElseThrow(() -> new AssertionError("Property not found"));
        registry.value("sample.complex.list[1].value")
            .peek(value -> assertThat(value).isEqualTo("value2"))
            .orElseThrow(() -> new AssertionError("Property not found"));
    }
}
