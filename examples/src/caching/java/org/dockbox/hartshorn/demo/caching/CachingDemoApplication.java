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

package org.dockbox.hartshorn.demo.caching;

import org.dockbox.hartshorn.core.annotations.activate.ServiceActivator;
import org.dockbox.hartshorn.core.boot.HartshornApplication;
import org.dockbox.hartshorn.cache.annotations.UseCaching;
import org.dockbox.hartshorn.core.annotations.activate.Activator;
import org.dockbox.hartshorn.core.services.ServicePreProcessor;
import org.dockbox.hartshorn.events.annotations.UseEvents;


/**
 * A simple application starter, with specific {@link ServiceActivator service activators}
 * configured to enable only the required {@link ServicePreProcessor service processors}.
 * <p>For readability, each activator has been documented with a short description below.
 */
@UseEvents /* Activates the EventServiceProcessor, enabling event listening */
@UseCaching /* Activates the CacheServiceModifier, enabling method caching */

/*
 * Indicates this type is an application activator, providing required metadata for your application. In this example only the
 * bootstrap is indicated, ProxyApplicationBootstrap is the default recommended bootstrap, but it is possible to override functionality
 * and use your own implementation here directly
 */
@Activator
public final class CachingDemoApplication {

    private CachingDemoApplication() {}

    public static void main(final String[] args) {
        HartshornApplication.create(CachingDemoApplication.class, args);
    }
}
