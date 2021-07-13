package org.dockbox.hartshorn.blockregistry;

import org.dockbox.hartshorn.test.HartshornRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(HartshornRunner.class)
public class BlockRegistryTests
{
    @Test
    public void constructBlockRegistry() {
        BlockDataInitialisationManager.constructBlockRegistry();
    }
}
