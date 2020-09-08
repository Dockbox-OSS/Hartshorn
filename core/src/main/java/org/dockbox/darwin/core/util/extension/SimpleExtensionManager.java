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

import com.google.inject.Singleton;

import org.dockbox.darwin.core.server.Server;
import org.dockbox.darwin.core.util.extension.ExtensionContext.ComponentType;
import org.dockbox.darwin.core.util.extension.status.ExtensionStatus;
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
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class SimpleExtensionManager implements ExtensionManager {

    private static final Collection<ExtensionContext> globalContexts = new CopyOnWriteArrayList<>();
    private static final Map<String, Object> instanceMappings = new ConcurrentHashMap<>();

    @NotNull
    @Override
    public Optional<ExtensionContext> getContext(@NotNull Class<?> type) {
        for (ExtensionContext ctx : globalContexts) {
            for (Class<?> componentType : ctx.getClasses().values()) {
                if (componentType.equals(type)) return Optional.of(ctx);
            }
        }
        return Optional.empty();
    }

    @NotNull
    @Override
    public Optional<ExtensionContext> getContext(@NonNls @NotNull String id) {
        for (ExtensionContext ctx : globalContexts) {
            for (Extension extension : ctx.getClasses().keySet()) {
                if (extension.id().equals(id)) return Optional.of(ctx);
            }
        }
        return Optional.empty();
    }

    @NotNull
    @Override
    public <T> Optional<T> getInstance(@NotNull Class<T> type) {
        Server.log().info("Instance requested for [" + type.getCanonicalName() +"]");
        for (Object o : instanceMappings.values()) {
            Server.log().info(" - mapping: " + o);
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
    public List<ExtensionContext> getExternalExtensions() {
        Path moduleDir = Server.getInstance(FileUtils.class).getModuleDir();
        try (Stream<Path> stream = Files.walk(moduleDir, 1)) {
            List<ExtensionContext> contexts = new CopyOnWriteArrayList<>();
            Server.log().info("Scanning [" + moduleDir + "] for component files");
            // Filter out anything that isn't a directory (making it a file), as we only want to scan one layer deep
            stream.filter(file -> !Files.isDirectory(file))
                    .forEach(jar -> this.loadExternalExtension(jar).ifPresent(contexts::add));

            globalContexts.addAll(contexts);
            return contexts;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @NotNull
    @Override
    public List<ExtensionContext> collectIntegratedExtensions() {
        Reflections integratedReflections = new Reflections("org.dockbox.darwin.integrated");
        Set<Class<?>> annotatedTypes = integratedReflections.getTypesAnnotatedWith(Extension.class);
        Server.log().info("Found '" + annotatedTypes.size() + "' integrated annotated types.");
        return annotatedTypes.stream().map(type -> {

            Server.log().info(" - [" + type.getCanonicalName() + "]");
            ExtensionContext context = new ExtensionContext(ComponentType.INTERNAL_CLASS, type.getCanonicalName());
            context.addComponentClass(type);
            this.createComponentInstance(type, context);

            globalContexts.add(context);

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
                        .forEach(entry -> this.injectJarEntry(entry, context));

            } catch (IOException e) {
                Server.getServer().except("Failed to convert known .jar file to JarFile instance", e);
            }
            return Optional.of(context);
        }
        return Optional.empty();
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

    private void injectJarEntry(JarEntry entry, ExtensionContext context) {
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

                this.createComponentInstance(classEntry, context);

                Server.log().info("Finished component registration for [" + classEntry.getCanonicalName() + "] with status " + context.getStatus(classEntry));
            }
        } catch (ClassNotFoundException | IllegalStateException e) {
            Server.getServer().except("Failed to load (supposedly) injected class", e);
        }
    }

    private <T> void createComponentInstance(Class<T> entry, ExtensionContext context) {
        Extension header = entry.getAnnotation(Extension.class);
        assert null != header : "@Extension header missing from previously checked type [" + entry.getCanonicalName() + "]! This should not be possible!";

        String[] dependencies = header.dependencies();
        for (String dependentPackage : dependencies) {
            try {
                Package pkg = Package.getPackage(dependentPackage);
                if (null == pkg) {
                    // Do not instantiate entries which require dependencies which are not present.
                    context.addStatus(entry, ExtensionStatus.FAILED);
                    return;
                }

                // Due to the way external .jar files are injected Reflections sometimes causes issues when using the Package instance
                Reflections ref = new Reflections(dependentPackage);
                Set<Class<?>> extensions = ref.getTypesAnnotatedWith(Extension.class);
                if (!extensions.isEmpty())
                    Server.log().warn("Detected " + extensions.size() + " extensions in dependent package " + dependentPackage + ". " +
                            "Common code shared by extensions should be implemented in DarwinServer, extensions should NEVER depend on each other.");

            } catch (Throwable e) {
                // Package.getPackage(String) typically returns null if no package with that name is present, this clause is a fail-safe and should
                // technically never be reached. If it is reached we explicitly need to mention the package to prevent future issues (by reporting this).
                Server.getServer().except("Failed to obtain package [" + dependentPackage + "].", e);
            }
        }

        T instance = null;
        try {
            Constructor<T> defaultConstructor = entry.getConstructor();
            defaultConstructor.setAccessible(true);
            instance = defaultConstructor.newInstance();
            ((Server) Server.getServer()).getInjector().injectMembers(instance);
            context.addStatus(entry, ExtensionStatus.LOADED);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            context.addStatus(entry, ExtensionStatus.FAILED);
            Server.log().warn("No default accessible constructor available for [" + entry.getCanonicalName() + ']');
        } catch (InstantiationException | InvocationTargetException e) {
            context.addStatus(entry, ExtensionStatus.ERRORED);
            Server.log().warn("Failed to instantiate default constructor for [" + entry.getCanonicalName() + "]. Proceeding to look for injectable constructors.");
        }

        Server.log().info("Instance for [" + entry.getCanonicalName() + "] = " + instance);
        if (null != instance) instanceMappings.put(header.id(), instance);
    }
}
