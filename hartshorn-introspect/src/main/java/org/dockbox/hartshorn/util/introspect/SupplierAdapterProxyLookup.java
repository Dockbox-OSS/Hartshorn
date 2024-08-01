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

package org.dockbox.hartshorn.util.introspect;

import org.dockbox.hartshorn.util.option.Option;

import java.util.function.Supplier;

/**
 * A {@link ProxyLookup} that delegates to a lazily initialized {@link ProxyLookup}. This is useful when the
 * {@link ProxyLookup} is not available at the time of construction, but is available at the time of use.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class SupplierAdapterProxyLookup implements ProxyLookup {

    private final Supplier<ProxyLookup> delegate;

    public SupplierAdapterProxyLookup(Supplier<ProxyLookup> delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> Option<Class<T>> unproxy(T instance) {
        return this.delegate.get().unproxy(instance);
    }

    @Override
    public boolean isProxy(Object instance) {
        return this.delegate.get().isProxy(instance);
    }

    @Override
    public boolean isProxy(Class<?> candidate) {
        return this.delegate.get().isProxy(candidate);
    }

    @Override
    public <T> Option<ProxyIntrospector<T>> introspector(T instance) {
        return this.delegate.get().introspector(instance);
    }
}
