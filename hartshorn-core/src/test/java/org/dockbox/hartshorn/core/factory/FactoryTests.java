package org.dockbox.hartshorn.core.factory;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.junit.jupiter.api.Assertions;

@HartshornTest
public class FactoryTests {

    @InjectTest
    void testFactoryRespectsPriorities(final ApplicationContext applicationContext) {
        final FactoryService factoryService = applicationContext.get(FactoryService.class);
        final FactoryProvided provided = factoryService.provide("");
        Assertions.assertNotNull(provided);
        Assertions.assertTrue(provided instanceof HighPriorityFactoryBound);
    }
}
