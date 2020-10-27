/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.impl.util.extension;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Singleton;

import org.dockbox.selene.core.objects.tuple.Tuple;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.util.extension.Extension;
import org.dockbox.selene.core.util.extension.ExtensionContext;
import org.dockbox.selene.core.util.extension.ExtensionContext.ComponentType;
import org.dockbox.selene.core.util.extension.ExtensionManager;
import org.dockbox.selene.core.util.extension.status.ExtensionStatus;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Singleton
public class SimpleExtensionManager implements ExtensionManager {

    private static final Collection<SimpleExtensionContext> globalContexts = new CopyOnWriteArrayList<>();
    private static final Map<String, Object> instanceMappings = new ConcurrentHashMap<>();

    @NotNull
    @Override
    public Optional<ExtensionContext> getContext(@NotNull Class<?> type) {
        for (SimpleExtensionContext ctx : globalContexts) {
            Class<?> componentClassType = ctx.getExtensionClass();
            if (componentClassType.equals(type)) return Optional.of(ctx);
        }
        return Optional.empty();
    }

    @NotNull
    @Override
    public Optional<ExtensionContext> getContext(@NonNls @NotNull String id) {
        for (SimpleExtensionContext ctx : globalContexts) {
            if (ctx.getExtension().id().equals(id)) return Optional.of(ctx);
        }
        return Optional.empty();
    }

    @NotNull
    @Override
    public <T> Optional<T> getInstance(@NotNull Class<T> type) {
        Selene.log().debug("Instance requested for [" + type.getCanonicalName() +"]");
        for (Object o : instanceMappings.values()) {
            if (null != o && o.getClass().equals(type))
                // Condition meets requirement for checked cast
                //noinspection unchecked
                return Optional.of((T) o);
        }
        return Optional.empty();
    }

    @NotNull
    @Override
    public Optional<?> getInstance(@NotNull String id) {
        return Optional.ofNullable(instanceMappings.get(id));
    }

    @NotNull
    @Override
    public List<ExtensionContext> initialiseExtensions() {
        Reflections integratedReflections = new Reflections("org.dockbox.selene");
        Set<Class<?>> annotatedTypes = integratedReflections.getTypesAnnotatedWith(Extension.class);
        Selene.log().info("Found '" + annotatedTypes.size() + "' integrated annotated types.");
        return annotatedTypes.stream().map(type -> {

            Selene.log().info(" - [" + type.getCanonicalName() + "]");
            SimpleExtensionContext context = new SimpleExtensionContext(
                    ComponentType.INTERNAL_CLASS,
                    type.getCanonicalName(),
                    type,
                    type.getAnnotation(Extension.class)
            );

            return new Tuple<>(context, type);
        }).filter(tuple -> {
            if (this.createComponentInstance(tuple.getSecond(), tuple.getFirst())) {
                globalContexts.add(tuple.getFirst());
                return true;
            }
            return false;
        }).map(Tuple::getFirst).collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Optional<Extension> getHeader(@NotNull Class<?> type) {
        return Optional.ofNullable(type.getAnnotation(Extension.class));
    }

    @NotNull
    @Override
    public Optional<Extension> getHeader(@NotNull String id) {
        return this.getInstance(id).map(i -> i.getClass().getAnnotation(Extension.class));
    }

    @NotNull
    @Override
    public List<String> getRegisteredExtensionIds() {
        return new ArrayList<>(instanceMappings.keySet());
    }

    private <T> boolean createComponentInstance(Class<T> entry, ExtensionContext context) {
        Extension header = entry.getAnnotation(Extension.class);
        List<Extension> existingHeaders = new LinkedList<>();
        globalContexts.forEach(ctx -> existingHeaders.add(ctx.getExtension()));
        //noinspection CallToSuspiciousStringMethod
        if (existingHeaders.stream().anyMatch(e -> e.uniqueId().equals(header.uniqueId()))) {
            Selene.log().warn("Extension with unique ID " + header.uniqueId() + " already present!");
            return false;
        }

        assert null != header : "@Extension header missing from previously checked type [" + entry.getCanonicalName() + "]! This should not be possible!";

        String[] dependencies = header.dependencies();
        for (String dependentPackage : dependencies) {
            try {
                Package pkg = Package.getPackage(dependentPackage);
                if (null == pkg) {
                    // Do not instantiate entries which require dependencies which are not present.
                    context.addStatus(entry, ExtensionStatus.FAILED);
                    return false;
                }

                // Due to the way external .jar files are injected Reflections sometimes causes issues when using the Package instance
                Reflections ref = new Reflections(dependentPackage);
                Set<Class<?>> extensions = ref.getTypesAnnotatedWith(Extension.class);
                if (!extensions.isEmpty())
                    Selene.log().warn("Detected " + extensions.size() + " extensions in dependent package " + dependentPackage + ". " +
                            "Common code shared by extensions should be implemented in Selene, extensions should NEVER depend on each other.");

            } catch (Throwable e) {
                // Package.getPackage(String) typically returns null if no package with that name is present, this clause is a fail-safe and should
                // technically never be reached. If it is reached we explicitly need to mention the package to prevent future issues (by reporting this).
                Selene.getServer().except("Failed to obtain package [" + dependentPackage + "].", e);
            }
        }

        T instance;
        instance = Selene.getInstance(entry);

        if (null == instance) {
            try {
                Constructor<T> defaultConstructor = entry.getConstructor();
                defaultConstructor.setAccessible(true);
                instance = defaultConstructor.newInstance();
                this.injectMembers(instance, context, header);

                context.addStatus(entry, ExtensionStatus.LOADED);
            } catch (NoSuchMethodException | IllegalAccessException e) {
                context.addStatus(entry, ExtensionStatus.FAILED);
                Selene.log().warn("No default accessible constructor available for [" + entry.getCanonicalName() + ']');
                return false;
            } catch (InstantiationException | InvocationTargetException e) {
                context.addStatus(entry, ExtensionStatus.ERRORED);
                Selene.log().warn("Failed to instantiate default constructor for [" + entry.getCanonicalName() + "]. Proceeding to look for injectable constructors.");
                return false;
            }
        }

        instanceMappings.put(header.id(), instance);
        return true;
    }

    private <T> void injectMembers(T instance, ExtensionContext context, Extension header) {
        Selene.getServer().injectMembers(instance);

        AbstractModule extensionModule = new AbstractModule() {
            @Override
            protected void configure() {
                this.bind(ExtensionContext.class).toInstance(context);
                this.bind(Extension.class).toInstance(header);
                this.bind(Logger.class).toInstance(LoggerFactory.getLogger(instance.getClass()));
            }
        };
        Guice.createInjector(extensionModule).injectMembers(instance);
    }
}
