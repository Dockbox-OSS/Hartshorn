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

public final class DefaultProxyOrchestratorLoader {

    private DefaultProxyOrchestratorLoader() {
        throw new UnsupportedOperationException();
    }

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

    public static class Configurer {
        // No-op, may be used in the future
    }
}
