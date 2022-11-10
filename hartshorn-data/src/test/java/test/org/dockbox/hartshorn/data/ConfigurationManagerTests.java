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

package test.org.dockbox.hartshorn.data;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.data.ConfigurationServicePreProcessor;
import org.dockbox.hartshorn.data.FileFormats;
import org.dockbox.hartshorn.data.annotations.UseConfigurations;
import org.dockbox.hartshorn.data.config.PropertyHolder;
import org.dockbox.hartshorn.data.mapping.ObjectMapper;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.concurrent.CopyOnWriteArrayList;

import jakarta.inject.Inject;

@HartshornTest(includeBasePackages = false)
@UseConfigurations
public class ConfigurationManagerTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    @TestComponents(DemoClasspathConfiguration.class)
    void testClassPathConfigurations() {
        // Configuration is read from resources/junit.yml
        final DemoClasspathConfiguration configuration = this.applicationContext.get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.classPathValue());
        Assertions.assertEquals("This is a value", configuration.classPathValue());
    }

    @Test
    @TestComponents(DemoClasspathConfiguration.class)
    void testDefaultValuesAreUsedIfPropertyIsAbsent() {
        final DemoClasspathConfiguration configuration = this.applicationContext.get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.classPathValueWithDefault());
        Assertions.assertEquals("myDefaultValue", configuration.classPathValueWithDefault());
    }

    @Test
    @TestComponents(DemoClasspathConfiguration.class)
    void testNumberValuesAreParsed() {
        final DemoClasspathConfiguration configuration = this.applicationContext.get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertEquals(1, configuration.number());
    }

    @Test
    @TestComponents(DemoClasspathConfiguration.class)
    void testCollectionsAreParsed() {
        final DemoClasspathConfiguration configuration = this.applicationContext.get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.list());
        Assertions.assertEquals(3, configuration.list().size());
    }

    @Test
    @TestComponents(DemoClasspathConfiguration.class)
    void testCollectionsAreSorted() {
        final DemoClasspathConfiguration configuration = this.applicationContext.get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.copyOnWriteArrayList());
        Assertions.assertEquals(1, (int) configuration.copyOnWriteArrayList().get(0));
        Assertions.assertEquals(5, (int) configuration.copyOnWriteArrayList().get(1));
        Assertions.assertEquals(3, (int) configuration.copyOnWriteArrayList().get(2));
    }

    @Test
    @TestComponents(DemoClasspathConfiguration.class)
    void testCustomCollectionsAreConverted() {
        final DemoClasspathConfiguration configuration = this.applicationContext.get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.copyOnWriteArrayList());
        Assertions.assertEquals(3, configuration.copyOnWriteArrayList().size());
        Assertions.assertTrue(configuration.copyOnWriteArrayList() instanceof CopyOnWriteArrayList);
    }

    @Test
    @TestComponents(DemoFSConfiguration.class)
    void testFsConfigurations() {
        final Path file = FileFormats.YAML.asPath(this.applicationContext.environment().applicationPath(), "junit");
        final ObjectMapper objectMapper = this.applicationContext.get(ObjectMapper.class);
        objectMapper.write(file, """
                junit:
                    fs: "This is a value"
                    """);

        final ComponentProcessingContext<DemoFSConfiguration> processingContext = new ComponentProcessingContext<>(this.applicationContext, Key.of(DemoFSConfiguration.class), null);
        new ConfigurationServicePreProcessor().process(this.applicationContext, processingContext);

        final DemoFSConfiguration configuration = this.applicationContext.get(DemoFSConfiguration.class);
        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.fileSystemValue());
        Assertions.assertEquals("This is a value", configuration.fileSystemValue());
    }

    @Test
    @TestComponents(ValueTyped.class)
    void testNormalValuesAreAccessible() {
        this.applicationContext.get(PropertyHolder.class).set("demo", "Hartshorn");
        final ValueTyped typed = this.applicationContext.get(ValueTyped.class);

        Assertions.assertNotNull(typed.string());
        Assertions.assertEquals("Hartshorn", typed.string());
    }

    @Test
    @TestComponents(ValueTyped.class)
    void testNestedValuesAreAccessible() {
        this.applicationContext.get(PropertyHolder.class).set("nested.demo", "Hartshorn");
        final ValueTyped typed = this.applicationContext.get(ValueTyped.class);

        Assertions.assertNotNull(typed);
        Assertions.assertNotNull(typed.nestedString());
        Assertions.assertEquals("Hartshorn", typed.nestedString());
    }

    @Test
    @TestComponents(SampleConfigurationObject.class)
    void testConfigurationObjects() {
        final PropertyHolder propertyHolder = this.applicationContext.get(PropertyHolder.class);
        propertyHolder.set("user.name", "Hartshorn");
        propertyHolder.set("user.age", 21);

        final SampleConfigurationObject configurationObject = this.applicationContext.get(SampleConfigurationObject.class);
        Assertions.assertNotNull(configurationObject);
        Assertions.assertEquals("Hartshorn", configurationObject.name());
        Assertions.assertEquals(21, configurationObject.age());
    }

    @Test
    @TestComponents(SampleSetterConfigurationObject.class)
    void testSetterConfigurationObjects() {
        final PropertyHolder propertyHolder = this.applicationContext.get(PropertyHolder.class);
        propertyHolder.set("user.name", "Hartshorn");
        propertyHolder.set("user.age", 21);

        final SampleSetterConfigurationObject configurationObject = this.applicationContext.get(SampleSetterConfigurationObject.class);
        Assertions.assertNotNull(configurationObject);
        Assertions.assertEquals("Hartshorn!", configurationObject.name(), "Bean-style setter (public)");
        Assertions.assertEquals(31, configurationObject.age(), "Fluent-style setter (private)");
    }
}