/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

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
