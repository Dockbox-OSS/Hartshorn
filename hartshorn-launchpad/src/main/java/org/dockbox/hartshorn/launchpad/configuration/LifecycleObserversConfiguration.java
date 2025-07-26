/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.launchpad.configuration;

import org.dockbox.hartshorn.inject.annotations.CompositeMember;
import org.dockbox.hartshorn.launchpad.observer.ComponentActivatorObserver;
import org.dockbox.hartshorn.launchpad.observer.RuntimeHookLifecycleObserver;
import org.dockbox.hartshorn.launchpad.annotations.UseLifecycleObservers;
import org.dockbox.hartshorn.launchpad.condition.RequiresActivator;
import org.dockbox.hartshorn.launchpad.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;
import org.dockbox.hartshorn.inject.annotations.Named;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;

/**
 * Registers lifecycle observers to the application context.
 *
 * @see LifecycleObserver
 * @see RuntimeHookLifecycleObserver
 * @see ComponentActivatorObserver
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
@Configuration
@RequiresActivator(UseLifecycleObservers.class)
public class LifecycleObserversConfiguration {

    @Singleton
    @CompositeMember
    public LifecycleObserver runtimeHookLifecycleObserver() {
        return new RuntimeHookLifecycleObserver();
    }

    @Singleton
    @CompositeMember
    public LifecycleObserver componentActivatorObserver() {
        return new ComponentActivatorObserver();
    }
}
