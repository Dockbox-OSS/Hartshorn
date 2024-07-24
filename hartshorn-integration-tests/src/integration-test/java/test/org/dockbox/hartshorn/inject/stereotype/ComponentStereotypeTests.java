package test.org.dockbox.hartshorn.inject.stereotype;

import org.dockbox.hartshorn.inject.ComponentResolutionException;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@HartshornIntegrationTest(includeBasePackages = false)
public class ComponentStereotypeTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    @TestComponents(components = EmptyService.class)
    void servicesAreSingletonsByDefault() {
        EmptyService emptyService = this.applicationContext.get(EmptyService.class);
        EmptyService emptyService2 = this.applicationContext.get(EmptyService.class);
        Assertions.assertSame(emptyService, emptyService2);
    }

    @Test
    void testNonComponentsAreNotProxied() {
        Assertions.assertThrows(ComponentResolutionException.class, () -> this.applicationContext.get(NonComponentType.class));
    }

    @Test
    @TestComponents(components = ComponentType.class)
    void testPermittedComponentsAreProxiedWhenRegularProvisionFails() {
        ComponentType instance = this.applicationContext.get(ComponentType.class);
        Assertions.assertNotNull(instance);
        Assertions.assertTrue(this.applicationContext.environment().proxyOrchestrator().isProxy(instance));
    }

    @Test
    @TestComponents(components = NonProxyComponentType.class)
    void testNonPermittedComponentsAreNotProxied() {
        Assertions.assertThrows(ComponentResolutionException.class, () -> this.applicationContext.get(NonProxyComponentType.class));
    }
}
