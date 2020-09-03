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

package org.dockbox.darwin.core.util.extension;

import org.dockbox.darwin.core.server.Server;
import org.dockbox.darwin.core.util.extension.ExtensionContext.ComponentType;
import org.dockbox.darwin.core.util.files.FileUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleExtensionManager implements ExtensionManager {

    private final Collection<ExtensionContext> globalContexts = new CopyOnWriteArrayList<>();
    private final Map<String, Object> instanceMappings = new ConcurrentHashMap<>();

    @NotNull
    @Override
    public Optional<ExtensionContext> getContext(@NotNull Class<?> module) {
        for (ExtensionContext ctx : this.globalContexts) {
            for (Class<?> componentType : ctx.getClasses().values()) {
                if (componentType.equals(module)) return Optional.of(ctx);
            }
        }
        return Optional.empty();
    }

    @NotNull
    @Override
    public Optional<ExtensionContext> getContext(@NonNls @NotNull String id) {
        for (ExtensionContext ctx : this.globalContexts) {
            for (Extension extension : ctx.getClasses().keySet()) {
                if (extension.id().equals(id)) return Optional.of(ctx);
            }
        }
        return Optional.empty();
    }

    @NotNull
    @Override
    public <T> Optional<T> getInstance(@NotNull Class<T> module) {
        for (Object o : this.instanceMappings.values()) {
            if (null != o && o.getClass().equals(module))
                // Condition meets requirement for checked cast
                //noinspection unchecked
                return Optional.of((T) o);
        }
        return Optional.empty();
    }

    @NotNull
    @Override
    public Optional<?> getInstance(@NotNull String id) {
        return Optional.ofNullable(this.instanceMappings.get(id));
    }

    @NotNull
    @Override
    public List<ExtensionContext> getExternalExtensions() {
        Path moduleDir = Server.getInstance(FileUtils.class).getModuleDir();
        try (Stream<Path> stream = Files.walk(moduleDir, 1)) {
            List<ExtensionContext> contexts = new CopyOnWriteArrayList<>();
            Server.log().info("Scanning [" + moduleDir + "] for component files");
            // Filter out anything that isn't a directory (making it a file), as we only want to scan one layer deep
            stream.filter(file -> !Files.isDirectory(file))
                    .forEach(jar -> this.loadExternalExtension(jar).ifPresent(contexts::add));

            this.globalContexts.addAll(contexts);
            return contexts;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private boolean addJarToClassPath(Path jar) {
        if (jar.getFileName().endsWith(".jar")) {
            try {
                ClassLoader ucl = ClassLoader.getSystemClassLoader();
                Method addUrl = ucl.getClass().getSuperclass().getDeclaredMethod("addURL", URL.class);
                addUrl.setAccessible(true);
                addUrl.invoke(ucl, jar.toUri().toURL());
                return true;
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | MalformedURLException e) {
                return false;
            }
        }
        return false;
    }

    private <T> void createComponentInstance(Class<T> entry) {
        T instance = null;
        try {
            Constructor<T> defaultConstructor = entry.getConstructor();
            defaultConstructor.setAccessible(true);
            instance = defaultConstructor.newInstance();
        } catch (NoSuchMethodException e) {
            Server.log().warn("No default constructor available for [" + entry.getCanonicalName() + ']');
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            Server.log().warn("Failed to instantiate default constructor for [" + entry.getCanonicalName() + "]. Proceeding to look for injectable constructors.");
        }

        // TODO: Guice injection for alternative instance creation

        Extension header = entry.getAnnotation(Extension.class);
        if (null != instance) this.instanceMappings.put(header.id(), instance);
    }

    @NotNull
    @Override
    public List<ExtensionContext> collectIntegratedExtensions() {
        Reflections integratedReflections = new Reflections("org.dockbox.darwin.integrated");
        Set<Class<?>> annotatedTypes = integratedReflections.getTypesAnnotatedWith(Extension.class);
        return annotatedTypes.stream().map(type -> {

            // TODO: Add extension status (
            // - loaded (all went well)
            // - errored (caught exception when instantiating)
            // - failed (something else went wrong)
            // - disabled (@Disabled annotation present)
            // - deprecated_[n] (@Deprecated annotation present, but with one of the above statuses)
            ExtensionContext context = new ExtensionContext(ComponentType.INTERNAL_CLASS, type.getCanonicalName());
            context.addComponentClass(type);
            this.globalContexts.add(context);

            this.createComponentInstance(type);

            return context;
        }).collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Optional<ExtensionContext> loadExternalExtension(@NotNull Path file) {
        if (this.addJarToClassPath(file)) {

            // Context should always be created for all injected jar files, so we can find it back later
            String fileName = file.toString();
            ExtensionContext context = new ExtensionContext(ComponentType.EXTERNAL_JAR, fileName);
            Server.log().info("Prepared context for external file [" + fileName + "]");

            try {
                // After adding the jar to the classpath, start registering all relevant classes
                JarFile jarFile = new JarFile(file.toFile());
                jarFile.stream()
                        .filter(entry -> !entry.isDirectory() && entry.getName().endsWith(".class"))
                        .forEach(entry -> {
                            try {
                                String className = entry.getName().replace('/', '.');
                                className = className.substring(0, className.length() - ".class".length());
                                Class<?> classEntry = Class.forName(className);

                                // If the class isn't added to the classpath, this the above will cause an
                                // exception making it so this line is never reached.
                                // Additionally, the addComponentClass method scans if the entry is annotated.
                                if (context.addComponentClass(classEntry)) {
                                    Extension header = classEntry.getAnnotation(Extension.class);
                                    if (null == header) //noinspection ThrowCaughtLocally
                                        throw new IllegalStateException("Supposed header is absent from component entry");

                                    this.createComponentInstance(classEntry);

                                    Server.log().info("Finished component registration for [" + classEntry.getCanonicalName() + ']');
                                }
                            } catch (ClassNotFoundException | IllegalStateException e) {
                                Server.getServer().except("Failed to load (supposedly) injected class", e);
                            }
                        });

            } catch (IOException e) {
                Server.getServer().except("Failed to convert known .jar file to JarFile instance", e);
            }
            return Optional.of(context);
        }
        return Optional.empty();
    }

    @NotNull
    @Override
    public Optional<Extension> getHeader(@NotNull Class<?> module) {
        return Optional.ofNullable(module.getAnnotation(Extension.class));
    }

    @NotNull
    @Override
    public Optional<Extension> getHeader(@NotNull String id) {
        return this.getInstance(id).map(i -> i.getClass().getAnnotation(Extension.class));
    }

    @NotNull
    @Override
    public List<String> getRegisteredExtensionIds() {
        return new ArrayList<>(this.instanceMappings.keySet());
    }
}
