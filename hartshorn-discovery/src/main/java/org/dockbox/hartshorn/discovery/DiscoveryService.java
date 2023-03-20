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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DiscoveryService {

    private static final DiscoveryService DISCOVERY_SERVICE = new DiscoveryService();

    private final Map<Class<?>, Class<?>> types = new ConcurrentHashMap<>();

    private DiscoveryService() {
        if (DISCOVERY_SERVICE != null) throw new IllegalStateException("Already instantiated");
        // Check that the class is being created by itself
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length < 3 || !stackTrace[2].getClassName().equals(DiscoveryService.class.getName())) {
            throw new IllegalStateException("Cannot instantiate outside of class");
        }
    }

    @NonNull
    public static DiscoveryService instance() {
        return DISCOVERY_SERVICE;
    }

    public boolean contains(final Class<?> type) {
        return this.types.containsKey(type);
    }

    @NonNull
    public <T> T discover(final Class<T> type) throws ServiceDiscoveryException {
        final String name = type.getName();

        final Class<?> implementationType = this.types.containsKey(type)
                ? this.types.get(type)
                : this.tryLoadDiscoveryFile(type, name);

        if (implementationType != null) {
            return this.loadServiceInstance(type, implementationType);
        }
        throw new ServiceDiscoveryException("No implementation found for type " + name);
    }

    private <T> Class<?> tryLoadDiscoveryFile(final Class<T> type, final String name) {
        final InputStream resource = this.getClass().getClassLoader().getResourceAsStream(getDiscoveryFileName(name));

        if (resource != null) {
            try {
                final String resourceString = new String(resource.readAllBytes());
                final Class<?> implementationType = Class.forName(resourceString);

                this.verifyRegistration(type, implementationType);
                this.types.put(type, implementationType);

                return implementationType;
            }
            catch (final IOException e) {
                throw new ServiceDiscoveryException("Failed to read discovery file for type " + name, e);
            }
            catch (final ClassNotFoundException e) {
                throw new ServiceDiscoveryException("Failed to load implementation class for type " + name, e);
            }
        }
        return null;
    }

    private void verifyRegistration(final Class<?> type, final Class<?> implementation) {
        if (!type.isAssignableFrom(implementation)) {
            throw new IllegalArgumentException("Implementation " + implementation.getName() + " is not assignable from type " + type.getName());
        }
        try {
            implementation.getConstructor();
        }
        catch (final NoSuchMethodException e) {
            throw new IllegalArgumentException("Implementation " + implementation.getName() + " does not have a default constructor");
        }
    }

    private <T> T loadServiceInstance(final Class<T> type, final Class<?> implementationClass) {
        try {
            final Constructor<?> constructor = implementationClass.getConstructor();

            constructor.setAccessible(true);
            final Object instance = constructor.newInstance();
            constructor.setAccessible(false);

            return type.cast(instance);
        }
        catch (final InstantiationException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    static String getDiscoveryFileName(final String name) {
        return "META-INF/" + name + ".disco";
    }
}
