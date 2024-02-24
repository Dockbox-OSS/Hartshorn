/*
 * Copyright 2019-2024 the original author or authors.
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

package test.org.dockbox.hartshorn.config;

import java.nio.file.Path;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.config.ConfigurationServicePreProcessor;
import org.dockbox.hartshorn.config.FileFormats;
import org.dockbox.hartshorn.config.ObjectMapper;
import org.dockbox.hartshorn.config.ObjectMappingException;
import org.dockbox.hartshorn.config.annotations.UseConfigurations;
import org.dockbox.hartshorn.config.properties.PropertyHolder;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

/**
 * <b>Note:</b> This test requires the resources {@code junit.yml} and {@code junit.properties} to be present in the
 * classpath. These files are located in the {@code hartshorn-config} module under the {@code src/test/resources}
 * directory.
 */
@HartshornTest(includeBasePackages = false)
@UseConfigurations
public abstract class ConfigurationManagerTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    @TestComponents(components = DemoClasspathConfiguration.class)
    void testClassPathConfigurations() {
        // Configuration is read from resources/junit.yml
        DemoClasspathConfiguration configuration = this.applicationContext.get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.classPathValue());
        Assertions.assertEquals("This is a value", configuration.classPathValue());
    }

    @Test
    @TestComponents(components = DemoClasspathConfiguration.class)
    void testDefaultValuesAreUsedIfPropertyIsAbsent() {
        DemoClasspathConfiguration configuration = this.applicationContext.get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.classPathValueWithDefault());
        Assertions.assertEquals("myDefaultValue", configuration.classPathValueWithDefault());
    }

    @Test
    @TestComponents(components = DemoClasspathConfiguration.class)
    void testNumberValuesAreParsed() {
        DemoClasspathConfiguration configuration = this.applicationContext.get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertEquals(1, configuration.number());
    }

    @Test
    @TestComponents(components = DemoClasspathConfiguration.class)
    void testCollectionsAreParsed() {
        DemoClasspathConfiguration configuration = this.applicationContext.get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.list());
        Assertions.assertEquals(3, configuration.list().size());
    }

    @Test
    @TestComponents(components = DemoClasspathConfiguration.class)
    void testCollectionsAreSorted() {
        DemoClasspathConfiguration configuration = this.applicationContext.get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.copyOnWriteArrayList());
        Assertions.assertEquals(1, (int) configuration.copyOnWriteArrayList().get(0));
        Assertions.assertEquals(5, (int) configuration.copyOnWriteArrayList().get(1));
        Assertions.assertEquals(3, (int) configuration.copyOnWriteArrayList().get(2));
    }

    @Test
    @TestComponents(components = DemoClasspathConfiguration.class)
    void testCustomCollectionsAreConverted() {
        DemoClasspathConfiguration configuration = this.applicationContext.get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.copyOnWriteArrayList());
        Assertions.assertEquals(3, configuration.copyOnWriteArrayList().size());
        Assertions.assertTrue(configuration.copyOnWriteArrayList() instanceof CopyOnWriteArrayList);
    }

    @Test
    @TestComponents(components = DemoFSConfiguration.class)
    void testFsConfigurations() throws ObjectMappingException {
        Path file = FileFormats.YAML.asPath(this.applicationContext.environment().fileSystem().applicationPath(), "junit");
        ObjectMapper objectMapper = this.applicationContext.get(ObjectMapper.class);
        objectMapper.write(file, """
                junit:
                    fs: "This is a value"
                    """);

        ComponentProcessingContext<DemoFSConfiguration> processingContext = new ComponentProcessingContext<>(
                this.applicationContext,
                ComponentRequestContext.createForComponent(),
                ComponentKey.of(DemoFSConfiguration.class),
                null, false);
        new ConfigurationServicePreProcessor().process(this.applicationContext, processingContext);

        DemoFSConfiguration configuration = this.applicationContext.get(DemoFSConfiguration.class);
        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.fileSystemValue());
        Assertions.assertEquals("This is a value", configuration.fileSystemValue());
    }

    @Test
    @TestComponents(components = ValueTyped.class)
    void testNormalValuesAreAccessible() throws ObjectMappingException {
        this.applicationContext.get(PropertyHolder.class).set("demo", "Hartshorn");
        ValueTyped typed = this.applicationContext.get(ValueTyped.class);

        Assertions.assertNotNull(typed.string());
        Assertions.assertEquals("Hartshorn", typed.string());
    }

    @Test
    @TestComponents(components = ValueTyped.class)
    void testNestedValuesAreAccessible() throws ObjectMappingException {
        this.applicationContext.get(PropertyHolder.class).set("nested.demo", "Hartshorn");
        ValueTyped typed = this.applicationContext.get(ValueTyped.class);

        Assertions.assertNotNull(typed);
        Assertions.assertNotNull(typed.nestedString());
        Assertions.assertEquals("Hartshorn", typed.nestedString());
    }

    @Test
    @TestComponents(components = SampleConfigurationObject.class)
    void testConfigurationObjects() throws ObjectMappingException {
        PropertyHolder propertyHolder = this.applicationContext.get(PropertyHolder.class);
        propertyHolder.set("user.name", "Hartshorn");
        propertyHolder.set("user.age", 21);

        SampleConfigurationObject configurationObject = this.applicationContext.get(SampleConfigurationObject.class);
        Assertions.assertNotNull(configurationObject);
        Assertions.assertEquals("Hartshorn", configurationObject.name());
        Assertions.assertEquals(21, configurationObject.age());
    }

    @Test
    @TestComponents(components = SampleSetterConfigurationObject.class)
    void testSetterConfigurationObjects() throws ObjectMappingException {
        PropertyHolder propertyHolder = this.applicationContext.get(PropertyHolder.class);
        propertyHolder.set("user.name", "Hartshorn");
        propertyHolder.set("user.age", 21);

        SampleSetterConfigurationObject configurationObject = this.applicationContext.get(SampleSetterConfigurationObject.class);
        Assertions.assertNotNull(configurationObject);
        Assertions.assertEquals("Hartshorn!", configurationObject.name(), "Bean-style setter (public)");
        Assertions.assertEquals(31, configurationObject.age(), "Fluent-style setter (private)");
    }
}
