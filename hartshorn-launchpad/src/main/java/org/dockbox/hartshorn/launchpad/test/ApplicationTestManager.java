package org.dockbox.hartshorn.launchpad.test;

import org.dockbox.hartshorn.inject.DefaultInjectionApplicationAwareContext;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.context.ApplicationContextCarrier;

/**
 * Component carrying the {@link ApplicationContext} for a test application. This component should
 * only ever be present when an application is bootstrapped through the Hartshorn Test Suite.
 *
 * @see RequiresTestApplication
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class ApplicationTestManager extends DefaultInjectionApplicationAwareContext
        implements ApplicationContextCarrier {

    protected ApplicationTestManager(ApplicationContext application) {
        super(application);
    }

    @Override
    public ApplicationContext application() {
        return (ApplicationContext) super.application();
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.application();
    }
}
