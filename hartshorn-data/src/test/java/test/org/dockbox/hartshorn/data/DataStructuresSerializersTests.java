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

import org.dockbox.hartshorn.util.GenericType;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.data.annotations.UsePersistence;
import org.dockbox.hartshorn.data.mapping.ObjectMapper;
import org.dockbox.hartshorn.data.registry.Registry;
import org.dockbox.hartshorn.data.registry.RegistryColumn;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import jakarta.inject.Inject;

@HartshornTest(includeBasePackages = false)
@UsePersistence
public class DataStructuresSerializersTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    public void testThatRegistryCanBeSerialized() {
        Assertions.assertDoesNotThrow(() -> {
            final File copy = File.createTempFile("tmp", null);
            final Path tempFile = copy.toPath();

            final ObjectMapper objectMapper = this.applicationContext.get(ObjectMapper.class);
            objectMapper.write(this.buildTestRegistry());
        });
    }

    private Registry<Registry<String>> buildTestRegistry() {
        return new Registry<Registry<String>>()
                .addColumn(
                        TestIdentifier.BRICK,
                        new Registry<String>()
                                .addColumn(TestIdentifier.FULLBLOCK, "Brick Fullblock1", "Brick Fullblock2")
                                .addColumn(TestIdentifier.STAIR, "Brick Stair1")
                                .addColumn(TestIdentifier.SLAB, "Brick Slab1"))
                .addColumn(
                        TestIdentifier.SANDSTONE,
                        new Registry<String>()
                                .addColumn(TestIdentifier.FULLBLOCK, "Sandstone Fullblock1")
                                .addColumn(TestIdentifier.STAIR, "Sandstone Stair1"))
                .addColumn(
                        TestIdentifier.COBBLESTONE,
                        new Registry<String>().addColumn(TestIdentifier.FULLBLOCK, "Cobblestone Fullblock1"));
    }

    @Test
    public void testThatRegistryCanBeDeserialized() throws IOException {
        final File copy = File.createTempFile("tmp", null);
        final Path tempFile = copy.toPath();

        final ObjectMapper objectMapper = this.applicationContext.get(ObjectMapper.class);

        final Result<String> serializedRegistry = objectMapper.write(this.buildTestRegistry());
        Assertions.assertTrue(serializedRegistry.present());

        final String serialized = serializedRegistry.get();
        final Result<Registry<Registry<String>>> registry = objectMapper.read(serializedRegistry.get(), new GenericType<>() {});

        Assertions.assertTrue(registry.present());

        final Registry<Registry<String>> reg = registry.get();
        final RegistryColumn<RegistryColumn<String>> result = reg.matchingColumns(TestIdentifier.BRICK)
                .mapTo(r -> r.matchingColumns(TestIdentifier.FULLBLOCK));

        Assertions.assertTrue(result.first().get().contains("Brick Fullblock1"));
        Assertions.assertTrue(result.first().get().contains("Brick Fullblock2"));
    }
}
