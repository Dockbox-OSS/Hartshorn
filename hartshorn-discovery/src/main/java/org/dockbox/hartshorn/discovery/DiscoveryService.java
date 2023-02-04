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

package org.dockbox.hartshorn.discovery;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.SynchronizedMultiMap.SynchronizedHashSetMultiMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public final class DiscoveryService {

    private static final DiscoveryService instance = new DiscoveryService();

    private final MultiMap<Class<?>, Class<?>> types = new SynchronizedHashSetMultiMap<>();

    private DiscoveryService() {
        if (instance != null) throw new IllegalStateException("Already instantiated");
        // Check that the class is being created by itself
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length < 3 || !stackTrace[2].getClassName().equals(DiscoveryService.class.getName())) {
            throw new IllegalStateException("Cannot instantiate outside of class");
        }
    }

    @NonNull
    public static DiscoveryService instance() {
        return instance;
    }

    public void register(final Class<?> type, final Class<?> implementation) {
        this.verifyRegistration(type, implementation);
        this.types.put(type, implementation);
    }

    private void verifyRegistration(final Class<?> type, final Class<?> implementation) {
        if (!type.isAssignableFrom(implementation)) {
            throw new IllegalArgumentException("Implementation " + implementation.getName() + " is not assignable from type " + type.getName());
        }
        try {
            final Constructor<?> constructor = implementation.getConstructor();
            if (!constructor.canAccess(this)) constructor.setAccessible(true);
        }
        catch (final NoSuchMethodException e) {
            throw new IllegalArgumentException("Implementation " + implementation.getName() + " does not have a default constructor");
        }
    }

    public MultiMap<Class<?>, Class<?>> types() {
        return this.types;
    }

    public void clear() {
        this.types.clear();
    }

    public boolean contains(final Class<?> type) {
        return this.types.containsKey(type);
    }

    @NonNull
    public <T> T discover(final Class<T> type) throws ServiceDiscoveryException {
        if (this.types().containsKey(type)) {
            final Optional<T> optional = this.types().get(type).stream().findFirst()
                    .map(implementation -> this.loadServiceInstance(type, implementation));

            if (optional.isPresent()) return optional.get();
        }
        throw new ServiceDiscoveryException("No implementation found for type " + type.getName());
    }

    private <T> T loadServiceInstance(final Class<T> type, final Class<?> implementationClass) {
        try {
            final Object instance = implementationClass.getConstructor().newInstance();
            return type.cast(instance);
        }
        catch (final InstantiationException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
