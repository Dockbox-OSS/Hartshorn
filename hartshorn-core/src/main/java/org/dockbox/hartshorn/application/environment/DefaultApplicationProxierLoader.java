/*
 * Copyright 2019-2023 the original author or authors.
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

import org.dockbox.hartshorn.application.ApplicationConfigurer;
import org.dockbox.hartshorn.discovery.DiscoveryService;
import org.dockbox.hartshorn.proxy.ApplicationProxier;
import org.dockbox.hartshorn.proxy.ApplicationProxierLoader;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.introspect.Introspector;

public final class DefaultApplicationProxierLoader {

    private DefaultApplicationProxierLoader() {
        throw new UnsupportedOperationException();
    }

    public static ContextualInitializer<Introspector, ApplicationProxier> create(Customizer<Configurer> customizer) {
        return context -> {
            // Call, but ignore the result of the customizer for now
            customizer.configure(new Configurer());
            ApplicationProxierLoader loader = DiscoveryService.instance().discover(ApplicationProxierLoader.class);
            return loader.create(context.input());
        };
    }

    public static class Configurer extends ApplicationConfigurer {
        // No-op, may be used in the future
    }
}
