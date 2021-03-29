/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.common.modules;

import com.google.inject.Singleton;

import org.dockbox.selene.api.annotations.module.Disabled;
import org.dockbox.selene.api.annotations.module.Module;
import org.dockbox.selene.api.module.ModuleContainer;
import org.dockbox.selene.api.module.ModuleManager;
import org.dockbox.selene.api.module.ModuleStatus;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.tuple.Tuple;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.server.SeleneInformation;
import org.dockbox.selene.api.util.Reflect;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.common.SimpleModuleContext;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class SimpleModuleManager implements ModuleManager {

    private static final Collection<ModuleContainer> moduleContainers = SeleneUtils.emptyConcurrentList();
    private static final Map<String, Object> instanceMappings = SeleneUtils.emptyConcurrentMap();

    @NotNull
    @Override
    public Exceptional<ModuleContainer> getContext(@NotNull Class<?> type) {
        for (ModuleContainer container : moduleContainers) {
            Class<?> componentClassType = container.type();
            if (componentClassType.equals(type)) return Exceptional.of(container);
        }
        return Exceptional.none();
    }

    @NotNull
    @Override
    public Exceptional<ModuleContainer> getContext(@NonNls @NotNull String id) {
        for (ModuleContainer container : moduleContainers) {
            if (container.id().equals(id)) return Exceptional.of(container);
        }
        return Exceptional.none();
    }

    @NotNull
    @Override
    public Exceptional<ModuleContainer> getContainer(@NotNull Class<?> type) {
        for (ModuleContainer moduleContainer : moduleContainers) {
            if (moduleContainer.type().equals(type)) return Exceptional.of(moduleContainer);
        }
        return Exceptional.none();
    }

    @NotNull
    @Override
    public Exceptional<ModuleContainer> getContainer(@NotNull String id) {
        for (ModuleContainer moduleContainer : moduleContainers) {
            if (moduleContainer.id().equals(id)) return Exceptional.of(moduleContainer);
        }
        return Exceptional.none();
    }

    @NotNull
    @Override
    public <T> Exceptional<T> getInstance(@NotNull Class<T> type) {
        Selene.log().debug("Instance requested for [" + type.getCanonicalName() + "]");
        for (Object o : instanceMappings.values()) {
            if (null != o && o.getClass().equals(type))
                // Condition meets requirement for checked cast
                //noinspection unchecked
                return Exceptional.of((T) o);
        }
        return Exceptional.none();
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public Exceptional<?> getInstance(@NotNull String id) {
        return Exceptional.of(instanceMappings.get(id));
    }

    @NotNull
    @Override
    public Map<String, Object> getInstanceMappings() {
        return SeleneUtils.asUnmodifiableMap(instanceMappings);
    }

    @NotNull
    @Override
    public List<ModuleContainer> initialiseModules() {
        Collection<Class<?>> annotatedTypes =
                Reflect.getAnnotatedTypes(SeleneInformation.PACKAGE_PREFIX, Module.class);
        Selene.log().info("Found '" + annotatedTypes.size() + "' integrated annotated types.");
        return annotatedTypes.stream()
                .filter(type -> !type.isAnnotationPresent(Disabled.class))
                .map(type -> {
                    Selene.log().info(" - [" + type.getCanonicalName() + "]");
                    SimpleModuleContext context = new SimpleModuleContext(type.getCanonicalName(), type, type.getAnnotation(Module.class));

                    return new Tuple<>(new ModuleContainer(context), type);
                })
                .filter(tuple -> {
                    ModuleContainer container = tuple.getKey();
                    return this.createComponentInstance(tuple.getValue(), container);
                })
                .map(Tuple::getKey)
                .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public List<String> getRegisteredModuleIds() {
        return SeleneUtils.asList(instanceMappings.keySet());
    }

    private <T> boolean createComponentInstance(Class<T> entry, ModuleContainer container) {
        Module header = entry.getAnnotation(Module.class);
        List<Module> existingHeaders = SeleneUtils.emptyList();
        moduleContainers.forEach(c -> existingHeaders.add(c.module()));
        //noinspection CallToSuspiciousStringMethod
        if (existingHeaders.stream().anyMatch(e -> e.id().equals(header.id()))) {
            Selene.log().warn("Module with unique ID " + header.id() + " already present!");
            return false;
        }

        assert null != header : "@Module header missing from previously checked type [" + entry.getCanonicalName() + "]! This should not be possible!";

        String[] dependencies = header.dependencies();
        for (String dependency : dependencies) {
            try {
                String formattedDependency = SimpleModuleManager.convertDependencyName(dependency);
                // Using Package.getPackage would only return a package if the package has been used by the
                // classloader
                // before this call has been made. By requesting it as a resource we can ensure it can be
                // loaded.
                // == Warning! ==
                // This is no longer functional as of JDK 9, where packages are encapsulated in modules.
                // Classes however
                // are not encapsulated, so it is possible to reuse this to look up classes in the future.
                Object pkg = this.getClass().getClassLoader().getResource(formattedDependency);
                if (null == pkg) {
                    Selene.log().warn("Dependent package '" + dependency + " (" + formattedDependency + ")' is not present, failing " + header.name());
                    // Do not instantiate entries which require dependencies which are not present.
                    container.status(entry, ModuleStatus.FAILED);
                    return false;
                }
            }
            catch (Throwable e) {
                Selene.handle("Failed to obtain package [" + dependency + "].", e);
            }
        }

        // Make sure the container is available here before the instance is created, but after the existence checks are done
        moduleContainers.add(container);

        T instance;
        instance = Selene.provide(entry);

        if (null == instance) {
            try {
                Constructor<T> defaultConstructor = entry.getConstructor();
                defaultConstructor.setAccessible(true);
                instance = defaultConstructor.newInstance();
                SimpleModuleManager.injectMembers(instance, container);

                container.status(entry, ModuleStatus.LOADED);
                Selene.getServer().bind(entry, instance);
            }
            catch (NoSuchMethodException | IllegalAccessException e) {
                container.status(entry, ModuleStatus.FAILED);
                Selene.log().warn("No default accessible constructor available for [" + entry.getCanonicalName() + ']');
                return false;
            }
            catch (InstantiationException | InvocationTargetException e) {
                container.status(entry, ModuleStatus.ERRORED);
                Selene.log().warn("Failed to instantiate default constructor for [" + entry.getCanonicalName() + "]. Proceeding to look for injectable constructors.");
                return false;
            }
        }

        instanceMappings.put(header.id(), instance);
        return true;
    }

    /**
     * Converts a dependency format to a valid resource name.
     *
     * <p>Example:
     *
     * <pre>{@code
     * java.lang.String.class -> java/lang/String.class
     * java.lang -> java/lang
     * }</pre>
     *
     * @param dependency
     *         The unformatted dependency format
     *
     * @return The formatted dependency format
     */
    private static String convertDependencyName(String dependency) {
        dependency = dependency.replaceAll("\\.", "/");
        dependency = dependency.replaceAll("/class", ".class");
        return dependency;
    }

    private static <T> void injectMembers(T instance, ModuleContainer container) {
        Selene.getServer().injectMembers(instance);
        Selene.getServer().createModuleInjector(instance, container).injectMembers(instance);
    }
}
