package test.org.dockbox.hartshorn.inject.binding.defaults;

import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import test.org.dockbox.hartshorn.launchpad.context.ApplicationContextTests;

@HartshornIntegrationTest(includeBasePackages = false)
public class DefaultBindingsTests {

    @Inject
    private Logger loggerField;

    @Test
    void loggerCanBeInjected(Logger loggerParameter) {
        Assertions.assertNotNull(loggerParameter);
        // Name should match the consuming class' name, and not the name of the configuration that uses it
        Assertions.assertEquals(loggerParameter.getName(), ApplicationContextTests.class.getName());

        Assertions.assertNotNull(this.loggerField);
        Assertions.assertEquals(this.loggerField.getName(), ApplicationContextTests.class.getName());
    }
}
