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

package org.dockbox.selene.structures;

import org.dockbox.selene.api.files.FileManager;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.structures.registry.Registry;
import org.dockbox.selene.structures.registry.RegistryColumn;
import org.dockbox.selene.structures.registry.TestIdentifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class DataStructuresSerializersTests {

    @Test
    public void testThatRegistryCanBeSerialised() {
        Assertions.assertDoesNotThrow(() -> {
            File copy = File.createTempFile("tmp", null);
            Path tempFile = copy.toPath();

            FileManager fm = new TestXStreamFileManager();

            fm.write(tempFile, this.buildTestRegistry());
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testThatRegistryCanBeDeserialised() throws IOException {
        File copy = File.createTempFile("tmp", null);
        Path tempFile = copy.toPath();

        FileManager fm = new TestXStreamFileManager();

        fm.write(tempFile, this.buildTestRegistry());
        Exceptional<Registry> registry = fm.read(tempFile, Registry.class);
        Assertions.assertTrue(registry.present());

        Registry<Registry<String>> reg = (Registry<Registry<String>>) registry.get();
        RegistryColumn<RegistryColumn<String>> result = reg.getMatchingColumns(TestIdentifier.BRICK)
                .mapTo(r -> r.getMatchingColumns(TestIdentifier.FULLBLOCK));

        Assertions.assertTrue(result.first().get().contains("Brick Fullblock1"));
        Assertions.assertTrue(result.first().get().contains("Brick Fullblock2"));
    }
}
