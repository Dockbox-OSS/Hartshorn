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
import org.dockbox.hartshorn.core.boot.Hartshorn;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.persistence.FileManager;
import org.dockbox.hartshorn.persistence.FileManagerTest;
import org.dockbox.hartshorn.persistence.FileType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

@UseConfigurations
public class ConfigurationManagerTests extends FileManagerTest {

    @Test
    void testClassPathConfigurations() {
        // Configuration is read from resources/junit.yml
        final DemoClasspathConfiguration configuration = this.context().get(DemoClasspathConfiguration.class);

        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.classPathValue());
        Assertions.assertEquals("This is a value", configuration.classPathValue());
    }

    @Test
    void testFsConfigurations() {
        // Create and populate the file, as we have no way to define local files in tests (yet)
        final FileManager files = this.context().get(FileManager.class).fileType(FileType.YAML);
        final Path file = files.configFile(Hartshorn.class, "junit");
        files.write(file, """
                junit:
                    fs: "This is a value"
                    """);

        new ConfigurationServiceProcessor().process(this.context(), TypeContext.of(DemoFSConfiguration.class));

        final DemoFSConfiguration configuration = this.context().get(DemoFSConfiguration.class);
        Assertions.assertNotNull(configuration);
        Assertions.assertNotNull(configuration.fileSystemValue());
        Assertions.assertEquals("This is a value", configuration.fileSystemValue());
    }

    @Test
    void testNormalValuesAreAccessible() {
        this.context().property("demo", "Hartshorn");
        final ValueTyped typed = this.context().get(ValueTyped.class);

        Assertions.assertNotNull(typed.string());
        Assertions.assertEquals("Hartshorn", typed.string());
    }

    @Test
    void testNestedValuesAreAccessible() {
        this.context().property("nested.demo", "Hartshorn");
        final ValueTyped typed = this.context().get(ValueTyped.class);

        Assertions.assertNotNull(typed);
        Assertions.assertNotNull(typed.nestedString());
        Assertions.assertEquals("Hartshorn", typed.nestedString());
    }
}
