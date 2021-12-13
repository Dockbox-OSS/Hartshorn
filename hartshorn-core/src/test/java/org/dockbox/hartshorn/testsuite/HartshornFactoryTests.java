package org.dockbox.hartshorn.testsuite;

import org.dockbox.hartshorn.core.boot.ApplicationFactory;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.junit.jupiter.api.Assertions;

@HartshornTest
public class HartshornFactoryTests {

    @HartshornFactory
    public static ApplicationFactory<?, ?> factory(final ApplicationFactory<?, ?> factory) {
        return factory.argument("-Hfactory.modified=true");
    }

    @InjectTest
    void testFactoryWasModified(final ApplicationContext applicationContext) {
        final Exceptional<Object> property = applicationContext.property("factory.modified");
        Assertions.assertTrue(property.present());
        Assertions.assertEquals("true", property.get());
    }
}
