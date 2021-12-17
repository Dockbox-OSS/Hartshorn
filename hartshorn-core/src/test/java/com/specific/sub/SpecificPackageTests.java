package com.specific.sub;

import org.dockbox.hartshorn.core.annotations.activate.Activator;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.junit.jupiter.api.Assertions;

/**
 * This test is associated with <a href="https://github.com/GuusLieben/Hartshorn/issues/609">#609</a>. It tests that
 * overly specific packages like {@code com.specific.sub} are not processed twice if a broader package like
 * {@code com.specific} is bound to the same application context.
 */
@Demo
@HartshornTest
@Activator(scanPackages = "com.specific")
public class SpecificPackageTests {

    @InjectTest
    public void someTest(final ApplicationContext applicationContext) {
        Assertions.assertNotNull(applicationContext);
        final DemoServicePreProcessor processor = applicationContext.get(DemoServicePreProcessor.class);
        Assertions.assertEquals(1, processor.processed());
    }
}
