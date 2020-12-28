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

package org.dockbox.selene.core.impl.extension;

import com.google.inject.Singleton;

import org.dockbox.selene.core.annotations.extension.Disabled;
import org.dockbox.selene.core.annotations.extension.Extension;
import org.dockbox.selene.core.extension.ExtensionContext;
import org.dockbox.selene.core.extension.ExtensionManager;
import org.dockbox.selene.core.extension.ExtensionStatus;
import org.dockbox.selene.core.impl.SimpleExtensionContext;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.tuple.Tuple;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.util.SeleneUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class SimpleExtensionManager implements ExtensionManager {

    private static final Collection<SimpleExtensionContext> globalContexts = SeleneUtils.COLLECTION.emptyConcurrentList();
    private static final Map<String, Object> instanceMappings = SeleneUtils.COLLECTION.emptyConcurrentMap();

    @NotNull
    @Override
    public Exceptional<ExtensionContext> getContext(@NotNull Class<?> type) {
        for (SimpleExtensionContext ctx : globalContexts) {
            Class<?> componentClassType = ctx.getExtensionClass();
            if (componentClassType.equals(type)) return Exceptional.of(ctx);
        }
        return Exceptional.empty();
    }

    @NotNull
    @Override
    public Exceptional<ExtensionContext> getContext(@NonNls @NotNull String id) {
        for (SimpleExtensionContext ctx : globalContexts) {
            if (ctx.getExtension().id().equals(id)) return Exceptional.of(ctx);
        }
        return Exceptional.empty();
    }

    @NotNull
    @Override
    public <T> Exceptional<T> getInstance(@NotNull Class<T> type) {
        Selene.log().debug("Instance requested for [" + type.getCanonicalName() +"]");
        for (Object o : instanceMappings.values()) {
            if (null != o && o.getClass().equals(type))
                // Condition meets requirement for checked cast
                //noinspection unchecked
                return Exceptional.of((T) o);
        }
        return Exceptional.empty();
    }

    @NotNull
    @Override
    public Exceptional<?> getInstance(@NotNull String id) {
        return Exceptional.ofNullable(instanceMappings.get(id));
    }

    @NotNull
    @Override
    public List<ExtensionContext> initialiseExtensions() {
        Collection<Class<?>> annotatedTypes = SeleneUtils.REFLECTION.getAnnotatedTypes(Selene.PACKAGE_PREFIX, Extension.class);
        Selene.log().info("Found '" + annotatedTypes.size() + "' integrated annotated types.");
        return annotatedTypes.stream()
                .filter(type -> !type.isAnnotationPresent(Disabled.class))
                .map(type -> {

                    Selene.log().info(" - [" + type.getCanonicalName() + "]");
                    SimpleExtensionContext context = new SimpleExtensionContext(
                            type.getCanonicalName(),
                            type,
                            type.getAnnotation(Extension.class)
                    );

                    return new Tuple<>(context, type);
                }).filter(tuple -> {
                    if (this.createComponentInstance(tuple.getValue(), tuple.getKey())) {
                        globalContexts.add(tuple.getKey());
                        return true;
                    }
                    return false;
                }).map(Tuple::getKey).collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Exceptional<Extension> getHeader(@NotNull Class<?> type) {
        return Exceptional.ofNullable(type.getAnnotation(Extension.class));
    }

    @NotNull
    @Override
    public Exceptional<Extension> getHeader(@NotNull String id) {
        return this.getInstance(id).map(i -> i.getClass().getAnnotation(Extension.class));
    }

    @NotNull
    @Override
    public List<String> getRegisteredExtensionIds() {
        return SeleneUtils.COLLECTION.asList(instanceMappings.keySet());
    }

    private <T> boolean createComponentInstance(Class<T> entry, ExtensionContext context) {
        Extension header = entry.getAnnotation(Extension.class);
        List<Extension> existingHeaders = SeleneUtils.COLLECTION.emptyList();
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
            } catch (Throwable e) {
                // Package.getPackage(String) typically returns null if no package with that name is present, this clause is a fail-safe and should
                // technically never be reached. If it is reached we explicitly need to mention the package to prevent future issues (by reporting this).
                Selene.except("Failed to obtain package [" + dependentPackage + "].", e);
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
        Selene.getServer().createExtensionInjector(instance, header, context).injectMembers(instance);
    }
}
