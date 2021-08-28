package org.dockbox.hartshorn.test;

import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.junit.jupiter.api.extension.RegisterExtension;

public class ApplicationAwareTest {

    @RegisterExtension
    HartshornRunner runner = new HartshornRunner();

    protected ApplicationContext context() {
        final ApplicationContext activeContext = this.runner.activeContext();
        if (activeContext == null) {
            throw new IllegalStateException("Active state was lost, was it modified externally?");
        }
        return activeContext;
    }

}
