package com.nonregistered;

import org.dockbox.hartshorn.core.boot.ApplicationFactory;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.testsuite.HartshornFactory;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.junit.jupiter.api.Assertions;

@HartshornTest
public class NonRegisteredComponentTests {

    @HartshornFactory
    public static ApplicationFactory<?, ?> factory(final ApplicationFactory<?, ?> factory) {
        return factory.componentLocator(ThrowingComponentLocatorImpl::new);
    }

    @InjectTest
    void testComponents(final ApplicationContext applicationContext) {
        Assertions.assertThrows(ApplicationException.class, () -> applicationContext.get(DemoComponent.class));
    }
}
