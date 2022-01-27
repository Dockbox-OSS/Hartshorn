/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.data.hibernate;

import org.dockbox.hartshorn.core.annotations.stereotype.Service;
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
