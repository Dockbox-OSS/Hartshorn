package test.org.dockbox.hartshorn.inject.provided;

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@HartshornIntegrationTest(includeBasePackages = false)
public class ProvidedMethodTests {

    @Test
    @TestComponents(components = ProviderService.class)
    void testProviderService(ProviderService service, ApplicationContext applicationContext) {
        applicationContext.bind(String.class).singleton("Hello World");

        Assertions.assertNotNull(service);
        Assertions.assertInstanceOf(Proxy.class, service);

        String message = service.get();
        Assertions.assertNotNull(message);
        Assertions.assertEquals("Hello World", message);
    }
}
