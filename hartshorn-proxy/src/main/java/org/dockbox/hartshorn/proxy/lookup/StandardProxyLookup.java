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

package org.dockbox.hartshorn.proxy.lookup;

import org.dockbox.hartshorn.util.introspect.ProxyLookup;

/**
 * A {@link ProxyLookup} implementation which only supports checking for proxies based on their class. This
 * implementation can still support unproxying of instances.
 *
 * @since 23.1
 * @author Guus Lieben
 */
public interface StandardProxyLookup extends ProxyLookup {

    @Override
    default boolean isProxy(final Object instance) {
        return instance != null && this.isProxy(instance.getClass());
    }
}