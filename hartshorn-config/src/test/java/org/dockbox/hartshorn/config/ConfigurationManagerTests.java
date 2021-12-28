/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.config.annotations.UseConfigurations;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.data.FileFormats;
import org.dockbox.hartshorn.data.mapping.ObjectMapper;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;

import lombok.Getter;

@HartshornTest
@UseConfigurations
public class ConfigurationManagerTests {

    @Inject
    @Getter
    private ApplicationContext applicationContext;

    @Test
    void testClassPathConfigurations() {
        // Configuration is read from resources/junit.yml
        final DemoClasspathConfiguration configuration = this.applicationContext().get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.classPathValue());
        Assertions.assertEquals("This is a value", configuration.classPathValue());
    }

    @Test
    void testDefaultValuesAreUsedIfPropertyIsAbsent() {
        final DemoClasspathConfiguration configuration = this.applicationContext().get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.classPathValueWithDefault());
        Assertions.assertEquals("myDefaultValue", configuration.classPathValueWithDefault());
    }

    @Test
    void testNumberValuesAreParsed() {
        final DemoClasspathConfiguration configuration = this.applicationContext().get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertEquals(1, configuration.number());
    }

    @Test
    void testCollectionsAreParsed() {
        final DemoClasspathConfiguration configuration = this.applicationContext().get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.list());
        Assertions.assertEquals(3, configuration.list().size());
    }

    @Test
    void testCollectionsAreSorted() {
        final DemoClasspathConfiguration configuration = this.applicationContext().get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.copyOnWriteArrayList());
        Assertions.assertEquals(1, (int) configuration.copyOnWriteArrayList().get(0));
        Assertions.assertEquals(5, (int) configuration.copyOnWriteArrayList().get(1));
        Assertions.assertEquals(3, (int) configuration.copyOnWriteArrayList().get(2));
    }

    @Test
    void testCustomCollectionsAreConverted() {
        final DemoClasspathConfiguration configuration = this.applicationContext().get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.copyOnWriteArrayList());
        Assertions.assertEquals(3, configuration.copyOnWriteArrayList().size());
        Assertions.assertTrue(configuration.copyOnWriteArrayList() instanceof CopyOnWriteArrayList);
    }

    @Test
    void testFsConfigurations() {
        final Path file = FileFormats.YAML.asPath(this.applicationContext().environment().manager().applicationPath(), "junit");
        final ObjectMapper objectMapper = this.applicationContext().get(ObjectMapper.class);
        objectMapper.write(file, """
                junit:
                    fs: "This is a value"
                    """);

        new ConfigurationServicePreProcessor().process(this.applicationContext(), Key.of(DemoFSConfiguration.class));

        final DemoFSConfiguration configuration = this.applicationContext().get(DemoFSConfiguration.class);
        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.fileSystemValue());
        Assertions.assertEquals("This is a value", configuration.fileSystemValue());
    }

    @Test
    void testNormalValuesAreAccessible() {
        this.applicationContext().property("demo", "Hartshorn");
        final ValueTyped typed = this.applicationContext().get(ValueTyped.class);

        Assertions.assertNotNull(typed.string());
        Assertions.assertEquals("Hartshorn", typed.string());
    }

    @Test
    void testNestedValuesAreAccessible() {
        this.applicationContext().property("nested.demo", "Hartshorn");
        final ValueTyped typed = this.applicationContext().get(ValueTyped.class);

        Assertions.assertNotNull(typed);
        Assertions.assertNotNull(typed.nestedString());
        Assertions.assertEquals("Hartshorn", typed.nestedString());
    }
}
