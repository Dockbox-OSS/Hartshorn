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

import org.dockbox.hartshorn.data.registry.Registry;
import org.dockbox.hartshorn.data.registry.RegistryColumn;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RegistryTests {

    @Test
    public void testThatRegistryCanGetCorrectMatchingColumns() {
        final Registry<Registry<String>> testRegistry = this.buildTestRegistry();

        final RegistryColumn<RegistryColumn<String>> result = testRegistry
                .matchingColumns(TestIdentifier.BRICK)
                .mapTo(r -> r.matchingColumns(TestIdentifier.FULLBLOCK));

        Assertions.assertTrue(result.first().get().contains("Brick Fullblock1"));
        Assertions.assertTrue(result.first().get().contains("Brick Fullblock2"));
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
    public void testThatRegistryCanAddData() throws NullPointerException {
        final Registry<Registry<String>> testRegistry = this.buildTestRegistry();

        testRegistry
                .get(TestIdentifier.COBBLESTONE)
                .first()
                .peek(r -> {
                    r.add(TestIdentifier.FULLBLOCK, "Cobblestone Fullblock2");
                    r.add(TestIdentifier.STAIR, "Cobblestone Stair1");
                });

        final Option<Registry<String>> eCobblestoneRegistry =
                testRegistry.matchingColumns(TestIdentifier.COBBLESTONE).first();

        final Registry<String> cobblestoneRegistry = eCobblestoneRegistry.get();
        final RegistryColumn<String> fullblocks =
                cobblestoneRegistry.matchingColumns(TestIdentifier.FULLBLOCK);

        Assertions.assertTrue(cobblestoneRegistry.containsColumns(TestIdentifier.STAIR));
        Assertions.assertTrue(fullblocks.contains("Cobblestone Fullblock2"));
        Assertions.assertEquals(2, fullblocks.size());
    }

    @Test
    public void testThatRegistryCanRemoveColumns() {
        final Registry<Registry<String>> testRegistry = this.buildTestRegistry();

        testRegistry.removeColumns(TestIdentifier.BRICK, TestIdentifier.SANDSTONE);

        Assertions.assertFalse(testRegistry.containsColumns(TestIdentifier.BRICK));
        Assertions.assertFalse(testRegistry.containsColumns(TestIdentifier.SANDSTONE));
    }

    @Test
    public void testThatRegistryCanBeFilteredByColumn() {
        final Registry<Registry<String>> testRegistry = this.buildTestRegistry();

        final Registry<Registry<String>> result = testRegistry.removeColumnsIf(TestIdentifier.BRICK::same);

        Assertions.assertTrue(result.containsColumns(TestIdentifier.SANDSTONE, TestIdentifier.COBBLESTONE));
        Assertions.assertFalse(result.containsColumns(TestIdentifier.BRICK));
    }

    @Test
    public void testThatRegistryCanBeFilteredByValue() {
        final Registry<Registry<String>> testRegistry = this.buildTestRegistry();

        final Registry<Registry<String>> result = testRegistry.removeValuesIf(r -> 2 <= r.size());
        final int brickColumnSize = result.matchingColumns(TestIdentifier.BRICK).size();

        Assertions.assertTrue(result.containsColumns(TestIdentifier.BRICK, TestIdentifier.SANDSTONE, TestIdentifier.COBBLESTONE));
        Assertions.assertEquals(0, brickColumnSize);
    }

    @Test
    public void testGetOrCreateRegistryColumn() {
        final Registry<Registry<String>> testRegistry = this.buildTestRegistry();

        final RegistryColumn<Registry<String>> column = testRegistry.getColumnOrCreate(TestIdentifier.WOOD, new Registry<>());

        Assertions.assertEquals(1, column.size());
        Assertions.assertTrue(testRegistry.containsColumns(TestIdentifier.WOOD));
    }

    @Test
    public void testThatRegistryCanBeAdded() {
        final Registry<Registry<String>> testRegistry = this.buildTestRegistry();

        final Registry<Registry<String>> secondRegistry = new Registry<Registry<String>>()
                .addColumn(
                        TestIdentifier.SANDSTONE,
                        new Registry<String>().addColumn(TestIdentifier.STAIR, "Sandstone Stair2"))
                .addColumn(
                        TestIdentifier.WOOD,
                        new Registry<String>().addColumn(TestIdentifier.STAIR, "Wooden Stair1"));

        testRegistry.addRegistry(secondRegistry);
        final RegistryColumn<Object> result = testRegistry
                .matchingColumns(TestIdentifier.SANDSTONE, TestIdentifier.WOOD)
                .mapTo(r -> r.matchingColumns(TestIdentifier.STAIR).safe(0).orNull());

        Assertions.assertTrue(result.contains("Sandstone Stair1"));
        Assertions.assertTrue(result.contains("Sandstone Stair2"));
        Assertions.assertTrue(result.contains("Wooden Stair1"));
    }
}
