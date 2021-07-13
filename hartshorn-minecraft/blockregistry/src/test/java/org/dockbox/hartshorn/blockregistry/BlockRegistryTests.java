package org.dockbox.hartshorn.blockregistry;

import org.dockbox.hartshorn.test.HartshornRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

public class BlockRegistryTests
{
    @Test
    @ExtendWith(HartshornRunner.class)
    public void constructBlockRegistry() {
        BlockDataInitialisationManager.constructBlockRegistry();
    }
}
