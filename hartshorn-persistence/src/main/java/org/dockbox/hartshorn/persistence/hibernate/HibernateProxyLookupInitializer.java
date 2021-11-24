package org.dockbox.hartshorn.persistence.hibernate;

import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.core.boot.ApplicationManager;
import org.dockbox.hartshorn.core.boot.HartshornApplicationManager;
import org.dockbox.hartshorn.core.boot.HartshornApplicationProxier;
import org.dockbox.hartshorn.core.boot.LifecycleObserver;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.persistence.annotations.UsePersistence;

@Service(activators = UsePersistence.class)
public class HibernateProxyLookupInitializer implements LifecycleObserver {

    @Override
    public void onStarted(final ApplicationContext applicationContext) {
        final ApplicationManager manager = applicationContext.environment().manager();
        if (manager instanceof HartshornApplicationManager applicationManager) {
            if (applicationManager.applicationProxier() instanceof HartshornApplicationProxier applicationProxier) {
                applicationProxier.registerProxyLookup(new HibernateProxyLookup());
            }
        }
    }

    @Override
    public void onExit(final ApplicationContext applicationContext) {
        // Nothing happens
    }
}
