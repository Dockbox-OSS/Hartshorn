package org.dockbox.hartshorn.demo.caching;

import org.dockbox.hartshorn.boot.HartshornApplication;
import org.dockbox.hartshorn.cache.annotations.UseCaching;
import org.dockbox.hartshorn.di.annotations.activate.Activator;
import org.dockbox.hartshorn.events.annotations.UseEvents;
import org.dockbox.hartshorn.proxy.ProxyApplicationBootstrap;

@UseEvents
@UseCaching
@Activator(ProxyApplicationBootstrap.class)
public class CachingDemoApplication {

    public static void main(String[] args) {
        HartshornApplication.create(CachingDemoApplication.class);
    }
}
