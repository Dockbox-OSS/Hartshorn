/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.application;

import org.dockbox.hartshorn.application.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.component.Configuration;
import org.dockbox.hartshorn.inject.annotations.Named;
import org.dockbox.hartshorn.inject.annotations.Singleton;

/**
 * Registers lifecycle observers to the application context. This configuration is intentionally
 * un-conditioned, as these observers are required for the application to function correctly.
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
public class LifecycleObserversConfiguration {

    @Singleton
    @Named("runtimeHookLifecycleObserver")
    public LifecycleObserver runtimeHookLifecycleObserver() {
        return new RuntimeHookLifecycleObserver();
    }

    @Singleton
    @Named("componentActivatorObserver")
    public LifecycleObserver componentActivatorObserver() {
        return new ComponentActivatorObserver();
    }
}
