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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.checkerframework.checker.nullness.qual.NonNull;

public final class DiscoveryService {

    private static final DiscoveryService DISCOVERY_SERVICE = new DiscoveryService();

    private final Map<Class<?>, Class<?>> types = new ConcurrentHashMap<>();
    private final Map<String, String> overrideDiscoveryFiles = new ConcurrentHashMap<>();
    private final Set<ClassLoader> classLoaders = new HashSet<>(List.of( // List, as context class loader may be same as service's class loader
            Thread.currentThread().getContextClassLoader(),
            DiscoveryService.class.getClassLoader()
    ));

    private DiscoveryService() {
        if (DISCOVERY_SERVICE != null) {
            throw new IllegalStateException("Already instantiated");
        }
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

    public void override(final Class<?> type, final Class<?> implementation) {
        this.override(type, implementation.getName());
    }

    public void override(Class<?> type, String qualifiedName) {
        this.overrideDiscoveryFiles.put(type.getName(), qualifiedName);
    }

    public void addClassLoader(final ClassLoader classLoader) {
        this.classLoaders.add(classLoader);
    }

    private <T> Class<?> tryLoadDiscoveryFile(final Class<T> type, final String name) {
        final InputStream resource = this.getClass().getClassLoader().getResourceAsStream(getDiscoveryFileName(name));

        final String resourceString;
        if (resource != null) {
            try {
                resourceString = new String(resource.readAllBytes());
            }
            catch (final IOException e) {
                throw new ServiceDiscoveryException("Failed to read discovery file for type " + name, e);
            }
        }
        else if (this.overrideDiscoveryFiles.containsKey(type.getName())) {
            resourceString = this.overrideDiscoveryFiles.get(type.getName());
        }
        else {
            return null;
        }

        try {
            final Class<?> implementationType = tryLoadClass(resourceString);

            this.verifyRegistration(type, implementationType);
            this.types.put(type, implementationType);

            return implementationType;
        }
        catch (final ClassNotFoundException e) {
            throw new ServiceDiscoveryException("Failed to load implementation class for type " + name, e);
        }
    }

    private Class<?> tryLoadClass(String resourceString) throws ClassNotFoundException {
        for(ClassLoader classLoader : classLoaders) {
            try {
                return Class.forName(resourceString, true, classLoader);
            }
            catch (final ClassNotFoundException e) {
                // Ignore
            }
        }
        throw new ClassNotFoundException(resourceString);
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
