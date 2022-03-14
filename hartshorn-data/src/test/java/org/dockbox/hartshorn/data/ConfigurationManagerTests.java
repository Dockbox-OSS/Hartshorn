/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.data;

import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.data.annotations.UseConfigurations;
import org.dockbox.hartshorn.data.mapping.ObjectMapper;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;

@HartshornTest
@UseConfigurations
public class ConfigurationManagerTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void testClassPathConfigurations() {
        // Configuration is read from resources/junit.yml
        final DemoClasspathConfiguration configuration = this.applicationContext.get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.classPathValue());
        Assertions.assertEquals("This is a value", configuration.classPathValue());
    }

    @Test
    void testDefaultValuesAreUsedIfPropertyIsAbsent() {
        final DemoClasspathConfiguration configuration = this.applicationContext.get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.classPathValueWithDefault());
        Assertions.assertEquals("myDefaultValue", configuration.classPathValueWithDefault());
    }

    @Test
    void testNumberValuesAreParsed() {
        final DemoClasspathConfiguration configuration = this.applicationContext.get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertEquals(1, configuration.number());
    }

    @Test
    void testCollectionsAreParsed() {
        final DemoClasspathConfiguration configuration = this.applicationContext.get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.list());
        Assertions.assertEquals(3, configuration.list().size());
    }

    @Test
    void testCollectionsAreSorted() {
        final DemoClasspathConfiguration configuration = this.applicationContext.get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.copyOnWriteArrayList());
        Assertions.assertEquals(1, (int) configuration.copyOnWriteArrayList().get(0));
        Assertions.assertEquals(5, (int) configuration.copyOnWriteArrayList().get(1));
        Assertions.assertEquals(3, (int) configuration.copyOnWriteArrayList().get(2));
    }

    @Test
    void testCustomCollectionsAreConverted() {
        final DemoClasspathConfiguration configuration = this.applicationContext.get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.copyOnWriteArrayList());
        Assertions.assertEquals(3, configuration.copyOnWriteArrayList().size());
        Assertions.assertTrue(configuration.copyOnWriteArrayList() instanceof CopyOnWriteArrayList);
    }

    @Test
    void testFsConfigurations() {
        final Path file = FileFormats.YAML.asPath(this.applicationContext.environment().manager().applicationPath(), "junit");
        final ObjectMapper objectMapper = this.applicationContext.get(ObjectMapper.class);
        objectMapper.write(file, """
                junit:
                    fs: "This is a value"
                    """);

        new ConfigurationServicePreProcessor().process(this.applicationContext, Key.of(DemoFSConfiguration.class));

        final DemoFSConfiguration configuration = this.applicationContext.get(DemoFSConfiguration.class);
        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.fileSystemValue());
        Assertions.assertEquals("This is a value", configuration.fileSystemValue());
    }

    @Test
    void testNormalValuesAreAccessible() {
        this.applicationContext.property("demo", "Hartshorn");
        final ValueTyped typed = this.applicationContext.get(ValueTyped.class);

        Assertions.assertNotNull(typed.string());
        Assertions.assertEquals("Hartshorn", typed.string());
    }

    @Test
    void testNestedValuesAreAccessible() {
        this.applicationContext.property("nested.demo", "Hartshorn");
        final ValueTyped typed = this.applicationContext.get(ValueTyped.class);

        Assertions.assertNotNull(typed);
        Assertions.assertNotNull(typed.nestedString());
        Assertions.assertEquals("Hartshorn", typed.nestedString());
    }
}
