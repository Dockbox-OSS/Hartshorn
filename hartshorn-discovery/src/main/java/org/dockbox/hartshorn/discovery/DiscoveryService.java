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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class DiscoveryService {

    private static DiscoveryService DISCOVERY_SERVICE = new DiscoveryService();

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
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length < 3 || !stackTrace[2].getClassName().equals(DiscoveryService.class.getName())) {
            throw new IllegalStateException("Cannot instantiate outside of class");
        }
    }

    @NonNull
    public static DiscoveryService instance() {
        if (DISCOVERY_SERVICE == null) {
            DISCOVERY_SERVICE = new DiscoveryService();
        }
        return DISCOVERY_SERVICE;
    }

    public boolean contains(Class<?> type) {
        return this.types.containsKey(type) || this.overrideDiscoveryFiles.containsKey(type.getName());
    }

    @NonNull
    public <T> T discover(Class<T> type) throws ServiceDiscoveryException {
        try {
            T object = this.tryLoadDiscoveryFile(type);
            if (object != null) {
                return object;
            }
        }
        catch(NoAvailableImplementationException e) {
            throw new ServiceDiscoveryException(e.getMessage(), e);
        }
        throw new ServiceDiscoveryException("No implementation found for type " + type.getCanonicalName());
    }

    public void override(Class<?> type, Class<?> implementation) {
        this.override(type, implementation.getName());
    }

    public void override(Class<?> type, String qualifiedName) {
        this.overrideDiscoveryFiles.put(type.getName(), qualifiedName);
    }

    public void addClassLoader(ClassLoader classLoader) {
        this.classLoaders.add(classLoader);
    }

    private <T> T tryLoadDiscoveryFile(Class<T> type) throws NoAvailableImplementationException {
        Class<?> implementationClass = this.tryLoadImplementationClass(type);
        if (implementationClass != null) {
            if (type.isAssignableFrom(implementationClass)) {
                this.verifyAndStoreType(type, implementationClass);
                return this.loadServiceInstance(type, implementationClass);
            }
            else {
                throw new ServiceDiscoveryException("Implementation " + implementationClass.getName() + " is not assignable from type " + type.getName());
            }
        }
        else {
            return this.tryLoadFromSPI(type);
        }
    }

    @Nullable
    private <T> Class<?> tryLoadImplementationClass(Class<T> type) throws NoAvailableImplementationException {
        if (this.types.containsKey(type)) {
            return this.types.get(type);
        }
        else if (this.overrideDiscoveryFiles.containsKey(type.getName())) {
            String resourceString = this.overrideDiscoveryFiles.get(type.getName());
            return this.tryLoadClassFromName(resourceString);
        }
        else {
            return null;
        }
    }

    private <T> void verifyAndStoreType(Class<T> type, Class<?> implementationType) {
        this.verifyRegistration(type, implementationType);
        this.types.put(type, implementationType);
    }

    private Class<?> tryLoadClassFromName(String qualifiedName) throws NoAvailableImplementationException {
        for(ClassLoader classLoader : this.classLoaders) {
            try {
                return Class.forName(qualifiedName, true, classLoader);
            }
            catch (ClassNotFoundException e) {
                // Ignore, may be in another class loader
            }
        }
        throw new NoAvailableImplementationException("No implementation found for type " + qualifiedName);
    }

    private <T> T tryLoadFromSPI(Class<T> type) throws NoAvailableImplementationException {
        for(ClassLoader classLoader : this.classLoaders) {
            ServiceLoader<T> serviceLoader = this.getServiceLoader(type, classLoader);
            Set<? extends Provider<T>> providers = serviceLoader.stream().collect(Collectors.toSet());
            for(Provider<T> provider : providers) {
                try {
                    return provider.get();
                }
                catch(ServiceConfigurationError e) {
                    // Ignore, may be in another class loader or provider
                }
            }
        }
        throw new NoAvailableImplementationException("No implementation found for type " + type.getName());
    }

    private <T> ServiceLoader<T> getServiceLoader(Class<T> type, ClassLoader classLoader) throws NoAvailableImplementationException {
        try {
            return ServiceLoader.load(type, classLoader);
        }
        catch(ServiceConfigurationError e) {
            throw new NoAvailableImplementationException("Cannot access service loader for type " + type.getName(), e);
        }
    }

    private void verifyRegistration(Class<?> type, Class<?> implementation) {
        if (!type.isAssignableFrom(implementation)) {
            throw new IllegalArgumentException("Implementation " + implementation.getName() + " is not assignable from type " + type.getName());
        }
        try {
            implementation.getConstructor();
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Implementation " + implementation.getName() + " does not have a default constructor");
        }
    }

    private <T> T loadServiceInstance(Class<T> type, Class<?> implementationClass) {
        try {
            Constructor<?> constructor = implementationClass.getConstructor();

            constructor.setAccessible(true);
            Object instance = constructor.newInstance();
            constructor.setAccessible(false);

            return type.cast(instance);
        }
        catch (InstantiationException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
