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

package org.dockbox.hartshorn.data.hibernate;

import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.core.boot.ApplicationManager;
import org.dockbox.hartshorn.core.boot.HartshornApplicationManager;
import org.dockbox.hartshorn.core.boot.JavassistApplicationProxier;
import org.dockbox.hartshorn.core.boot.LifecycleObserver;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.data.annotations.UsePersistence;

@Service(activators = UsePersistence.class)
public class HibernateProxyLookupInitializer implements LifecycleObserver {

    @Override
    public void onStarted(final ApplicationContext applicationContext) {
        final ApplicationManager manager = applicationContext.environment().manager();
        if (manager instanceof HartshornApplicationManager applicationManager) {
            if (applicationManager.applicationProxier() instanceof JavassistApplicationProxier applicationProxier) {
                applicationProxier.registerProxyLookup(new HibernateProxyLookup());
            }
        }
    }

    @Override
    public void onExit(final ApplicationContext applicationContext) {
        // Nothing happens
    }
}
