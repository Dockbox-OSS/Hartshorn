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

package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.util.introspect.ProxyLookup;
import org.dockbox.hartshorn.util.option.Option;

public interface StandardProxyLookup extends ProxyLookup {

    @Override
    default <T> Option<Class<T>> unproxy(final T instance) {
        if (instance instanceof Proxy<?> proxy) {
            return Option.of((Class<T>) proxy.manager().targetClass());
        }
        return Option.of(instance)
                .map(Object::getClass)
                .map(type -> (Class<T>) type);
    }

    @Override
    default boolean isProxy(final Object instance) {
        return instance != null && this.isProxy(instance.getClass());
    }
}
