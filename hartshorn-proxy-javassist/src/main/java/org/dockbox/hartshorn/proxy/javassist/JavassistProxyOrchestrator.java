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

package org.dockbox.hartshorn.proxy.javassist;

import org.dockbox.hartshorn.proxy.AbstractProxyOrchestrator;
import org.dockbox.hartshorn.proxy.lookup.StateAwareProxyFactory;
import org.dockbox.hartshorn.util.introspect.Introspector;

/**
 * A proxy orchestrator that uses Javassist to create proxies. This adds support for {@link JavassistProxyLookup}s,
 * and uses {@link JavassistProxyFactory} to construct new proxy instances.
 *
 * @see JavassistProxyLookup
 * @see JavassistProxyFactory
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class JavassistProxyOrchestrator extends AbstractProxyOrchestrator {

    public JavassistProxyOrchestrator(Introspector introspector) {
        super(introspector);
        this.registerProxyLookup(new JavassistProxyLookup());
    }

    @Override
    public <T> StateAwareProxyFactory<T> factory(Class<T> type) {
        return new JavassistProxyFactory<>(type, this);
    }

}
