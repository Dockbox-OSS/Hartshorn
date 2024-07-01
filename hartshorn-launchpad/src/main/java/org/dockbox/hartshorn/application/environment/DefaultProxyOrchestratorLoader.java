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

package org.dockbox.hartshorn.application.environment;

import org.dockbox.hartshorn.spi.DiscoveryService;
import org.dockbox.hartshorn.spi.ServiceDiscoveryException;
import org.dockbox.hartshorn.proxy.ProxyOrchestrator;
import org.dockbox.hartshorn.proxy.ProxyOrchestratorLoader;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.introspect.Introspector;

/**
 * Initializer for the default {@link ProxyOrchestrator} implementation provided by the {@link DiscoveryService}. This
 * is mostly a re-usable support class for any component that requires a {@link ProxyOrchestrator} to be loaded. Note
 * that this initializer is not cached, and will return a new instance of the {@link ProxyOrchestrator} on each call.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public final class DefaultProxyOrchestratorLoader {

    private DefaultProxyOrchestratorLoader() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a new {@link ContextualInitializer initializer} that will load the default {@link ProxyOrchestrator}
     * implementation from the {@link DiscoveryService}.
     *
     * @param customizer The customizer to apply to the {@link Configurer} before loading the {@link ProxyOrchestrator}
     * @return The initializer
     */
    public static ContextualInitializer<Introspector, ProxyOrchestrator> create(Customizer<Configurer> customizer) {
        return context -> {
            // Call, but ignore the result of the customizer for now
            customizer.configure(new Configurer());
            try {
                ProxyOrchestratorLoader loader = DiscoveryService.instance().discover(ProxyOrchestratorLoader.class);
                return loader.create(context.input());
            }
            catch (ServiceDiscoveryException e) {
                throw new ApplicationRuntimeException(e);
            }
        };
    }

    /**
     * A no-op class that may be used to configure the {@link ProxyOrchestrator} loader.
     *
     * @since 0.5.0
     *
     * @author Guus Lieben
     */
    public static class Configurer {
        // No-op, may be used in the future
    }
}
