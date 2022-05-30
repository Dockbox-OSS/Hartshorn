/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.data.hibernate;

import org.dockbox.hartshorn.proxy.ProxyLookup;
import org.hibernate.proxy.HibernateProxy;

public class HibernateProxyLookup implements ProxyLookup {

    @Override
    public <T> Class<T> unproxy(final T instance) {
        if (instance instanceof HibernateProxy hibernateProxy) {
            return (Class<T>) hibernateProxy.getHibernateLazyInitializer().getPersistentClass();
        }
        return instance != null ? (Class<T>) instance.getClass() : null;
    }

    @Override
    public boolean isProxy(final Object instance) {
        return instance instanceof HibernateProxy;
    }

    @Override
    public boolean isProxy(final Class<?> candidate) {
        return HibernateProxy.class.isAssignableFrom(candidate);
    }
}
