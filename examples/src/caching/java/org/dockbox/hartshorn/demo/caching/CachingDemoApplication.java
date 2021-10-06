package org.dockbox.hartshorn.demo.caching;

import org.dockbox.hartshorn.boot.HartshornApplication;
import org.dockbox.hartshorn.cache.annotations.UseCaching;
import org.dockbox.hartshorn.di.annotations.activate.Activator;
import org.dockbox.hartshorn.events.annotations.UseEvents;
import org.dockbox.hartshorn.proxy.ProxyApplicationBootstrap;


/**
 * A simple application starter, with specific {@link org.dockbox.hartshorn.di.annotations.service.ServiceActivator service activators}
 * configured to enable only the required {@link org.dockbox.hartshorn.di.services.ServiceProcessor service processors}.
 * <p>For readability, each activator has been documented with a short description below.
 */
@UseEvents /* Activates the EventServiceProcessor, enabling event listening */
@UseCaching /* Activates the CacheServiceModifier, enabling method caching */

/*
 * Indicates this type is an application activator, providing required metadata for your application. In this example only the
 * bootstrap is indicated, ProxyApplicationBootstrap is the default recommended bootstrap, but it is possible to override functionality
 * and use your own implementation here directly
 */
@Activator(ProxyApplicationBootstrap.class)
public class CachingDemoApplication {

    public static void main(String[] args) {
        HartshornApplication.create(CachingDemoApplication.class, args);
    }
}
