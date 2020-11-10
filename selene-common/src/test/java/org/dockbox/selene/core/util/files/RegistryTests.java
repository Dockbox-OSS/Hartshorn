package org.dockbox.selene.core.util.files;

import org.dockbox.selene.core.impl.objects.registry.Registry;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.test.object.TestIdentifier;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RegistryTests {

    private Registry<Registry<String>> buildTestRegistry() {
        return new Registry<Registry<String>>()
            .addColumn(TestIdentifier.BRICK, new Registry<String>()
                .addColumn(TestIdentifier.FULLBLOCK, "Brick Fullblock1", "Brick Fullblock2")
                .addColumn(TestIdentifier.STAIR, "Brick Stair1")
                .addColumn(TestIdentifier.SLAB, "Brick Slab1"))
            .addColumn(TestIdentifier.SANDSTONE, new Registry<String>()
                .addColumn(TestIdentifier.FULLBLOCK, "Sandstone Fullblock1")
                .addColumn(TestIdentifier.STAIR, "Sandstone Stair1"))
            .addColumn(TestIdentifier.COBBLESTONE, new Registry<String>()
                .addColumn(TestIdentifier.FULLBLOCK, "Cobblestone Fullblock1"));
    }

    @Test
    public void testThatRegistryCanGetCorrectMatchingColumns() {
        Registry<Registry<String>> testRegistry = this.buildTestRegistry();

        List<String> result = testRegistry.getMatchingColumns(TestIdentifier.BRICK)
            .mapToSingleList(r -> r.getMatchingColumns(TestIdentifier.FULLBLOCK));

        Assert.assertTrue(result.contains("Brick Fullblock1"));
        Assert.assertTrue(result.contains("Brick Fullblock2"));
    }

    @Test
    public void testThatRegistryCanAddData() throws NullPointerException {
        Registry<Registry<String>> testRegistry = this.buildTestRegistry();

        testRegistry.get(TestIdentifier.COBBLESTONE)
            .first()
            .ifPresent(r -> {
                r.addData(TestIdentifier.FULLBLOCK, "Cobblestone Fullblock2");
                r.addData(TestIdentifier.STAIR, "Cobblestone Stair1");
            });

        Exceptional<Registry<String>> eCobblestoneRegistry = testRegistry
            .getMatchingColumns(TestIdentifier.COBBLESTONE)
            .first();

        Registry<String> cobblestoneRegistry = eCobblestoneRegistry.get();
        List<String> fullblocks = cobblestoneRegistry.getMatchingColumns(TestIdentifier.FULLBLOCK);

        Assert.assertTrue(cobblestoneRegistry.containsColumns(TestIdentifier.STAIR));
        Assert.assertTrue(fullblocks.contains("Cobblestone Fullblock2"));
        Assert.assertEquals(2, fullblocks.size());
    }

    @Test
    public void testThatRegistryCanRemoveColumns() {
        Registry<Registry<String>> testRegistry = this.buildTestRegistry();

        testRegistry.removeColumns(TestIdentifier.BRICK, TestIdentifier.SANDSTONE);

        Assert.assertFalse(testRegistry.containsColumns(TestIdentifier.BRICK));
        Assert.assertFalse(testRegistry.containsColumns(TestIdentifier.SANDSTONE));
    }

    @Test
    public void testThatRegistryCanBeFilteredByColumn() {
        Registry<Registry<String>> testRegistry = this.buildTestRegistry();

        Registry<Registry<String>> result = testRegistry.removeColumnsIf(i -> TestIdentifier.BRICK == i);

        Assert.assertTrue(result.containsColumns(TestIdentifier.SANDSTONE, TestIdentifier.COBBLESTONE));
        Assert.assertFalse(result.containsColumns(TestIdentifier.BRICK));
    }

    @Test
    public void testThatRegistryCanBeFilteredByValue() {
        Registry<Registry<String>> testRegistry = this.buildTestRegistry();

        Registry<Registry<String>> result = testRegistry.removeValuesIf(r -> 2 <= r.size());
        int brickColumnSize = result.getMatchingColumns(TestIdentifier.BRICK).size();

        Assert.assertTrue(result.containsColumns(TestIdentifier.BRICK, TestIdentifier.SANDSTONE, TestIdentifier.COBBLESTONE));
        Assert.assertEquals(0, brickColumnSize);
    }

    @Test
    public void testThatRegistryCanBeAdded() {
        Registry<Registry<String>> testRegistry = this.buildTestRegistry();

        Registry<Registry<String>> secondRegistry = new Registry<Registry<String>>()
            .addColumn(TestIdentifier.SANDSTONE, new Registry<String>()
                .addColumn(TestIdentifier.STAIR, "Sandstone Stair2"))
            .addColumn(TestIdentifier.WOOD, new Registry<String>()
                .addColumn(TestIdentifier.STAIR, "Wooden Stair1"));

        testRegistry.addRegistry(secondRegistry);
        List<String> result = testRegistry
            .getMatchingColumns(TestIdentifier.SANDSTONE, TestIdentifier.WOOD)
            .mapToSingleList(r -> r.getMatchingColumns(TestIdentifier.STAIR));

        Assert.assertTrue(result.contains("Sandstone Stair1"));
        Assert.assertTrue(result.contains("Sandstone Stair2"));
        Assert.assertTrue(result.contains("Wooden Stair1"));
    }
}
