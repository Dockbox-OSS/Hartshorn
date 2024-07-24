package test.org.dockbox.hartshorn.inject.processing;

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import test.org.dockbox.hartshorn.inject.provider.NonProcessableType;
import test.org.dockbox.hartshorn.inject.provider.NonProcessableTypeProcessor;

@HartshornIntegrationTest(includeBasePackages = false, processors = NonProcessableTypeProcessor.class)
public class ComponentProcessorTests {

    @Test
    @TestComponents(components = NonProcessableType.class)
    void testNonProcessableComponent(ApplicationContext applicationContext) {
        NonProcessableType nonProcessableType = applicationContext.get(NonProcessableType.class);
        Assertions.assertNotNull(nonProcessableType);
        Assertions.assertNull(nonProcessableType.nonNullIfProcessed());
    }

}
