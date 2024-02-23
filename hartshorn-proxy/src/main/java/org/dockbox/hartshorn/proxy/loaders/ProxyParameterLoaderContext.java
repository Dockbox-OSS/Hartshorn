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

package org.dockbox.hartshorn.proxy.loaders;

import org.dockbox.hartshorn.proxy.ProxyOrchestrator;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderContext;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;

/**
 * A parameter loader context that provides access to the {@link ProxyOrchestrator} instance.
 *
 * @since 0.4.12
 * @author Guus Lieben
 */
public class ProxyParameterLoaderContext extends ParameterLoaderContext {

    private final ProxyOrchestrator proxyOrchestrator;

    public ProxyParameterLoaderContext(ExecutableElementView<?> executable, Object instance, ProxyOrchestrator proxyOrchestrator) {
        super(executable, instance);
        this.proxyOrchestrator = proxyOrchestrator;
    }

    /**
     * Returns the {@link ProxyOrchestrator} instance that owns the proxy that is being created or used.
     *
     * @return the proxy orchestrator
     */
    public ProxyOrchestrator proxyOrchestrator() {
        return this.proxyOrchestrator;
    }
}
