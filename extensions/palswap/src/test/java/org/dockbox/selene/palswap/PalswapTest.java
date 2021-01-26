package org.dockbox.selene.palswap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PalswapTest {

    @Test
    public void nameWithoutVariantTest() {
        Assertions.assertEquals("Quartz", VariantIdentifier.getBlockNameWithoutVariant("Quartz Slab"));
        Assertions.assertEquals("Cobblestone", VariantIdentifier.getBlockNameWithoutVariant("Cobblestone Wall"));
        Assertions.assertEquals("Birch", VariantIdentifier.getBlockNameWithoutVariant("BIRCH_FENCE_GATE"));
    }
}
