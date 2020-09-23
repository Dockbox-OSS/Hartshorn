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

package org.dockbox.selene.core.util.extension;

import com.google.inject.Singleton;

import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.util.extension.ExtensionContext.ComponentType;
import org.dockbox.selene.core.util.extension.status.ExtensionStatus;
import org.dockbox.selene.core.util.files.FileUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
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
        Selene.log().info("Instance requested for [" + type.getCanonicalName() +"]");
        for (Object o : instanceMappings.values()) {
            Selene.log().info(" - mapping: " + o);
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
        Path moduleDir = Selene.getInstance(FileUtils.class).getExtensionDir();
        try (Stream<Path> stream = Files.walk(moduleDir, 1)) {
            List<ExtensionContext> contexts = new CopyOnWriteArrayList<>();
            Selene.log().info("Scanning [" + moduleDir + "] for component files");
            // Filter out anything that isn't a directory (making it a file), as we only want to scan one layer deep
            stream.filter(file -> !Files.isDirectory(file))
                    .forEach(jar -> {
                        Selene.log().info("Attempting to load [" + jar.getFileName() + "]");
                        this.loadExternalExtension(jar).ifPresent(contexts::add);
                    });

            globalContexts.addAll(contexts);
            return contexts;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @NotNull
    @Override
    public List<ExtensionContext> collectIntegratedExtensions() {
        Reflections integratedReflections = new Reflections("org.dockbox.selene.integrated");
        Set<Class<?>> annotatedTypes = integratedReflections.getTypesAnnotatedWith(Extension.class);
        Selene.log().info("Found '" + annotatedTypes.size() + "' integrated annotated types.");
        return annotatedTypes.stream().map(type -> {

            Selene.log().info(" - [" + type.getCanonicalName() + "]");
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
            Selene.log().info("Prepared context for external file [" + fileName + "]");

            try (JarFile jarFile = new JarFile(file.toFile())) {
                // After adding the jar to the classpath, start registering all relevant classes
                jarFile.stream()
                        .filter(entry -> !entry.isDirectory() && entry.getName().endsWith(".class"))
                        .forEach(entry -> this.injectJarEntry(entry, context));
            } catch (IOException e) {
                Selene.getServer().except("Failed to convert known .jar file to JarFile instance", e);
            }
            return Optional.of(context);
        } else {
            Selene.log().warn("Failed to add [" + file.getFileName() + "] to classpath");
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
        if (jar.getFileName().toString().endsWith(".jar")) {
            try {
                URLClassLoader ucl = (URLClassLoader) ClassLoader.getSystemClassLoader();
                Method addUrl = ucl.getClass().getSuperclass().getDeclaredMethod("addURL", URL.class);
                addUrl.setAccessible(true);
                addUrl.invoke(ucl, jar.toUri().toURL());

                return true;
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | MalformedURLException e) {
                Selene.getServer().except("Failed to add [" + jar.getFileName() + "] to classpath", e);
                return false;
            }
        }
        return false;
    }

    private void injectJarEntry(JarEntry entry, ExtensionContext context) {
        String className = entry.getName().replace('/', '.');
        className = className.substring(0, className.length() - ".class".length());

        try {
            ClassLoader ucl = ClassLoader.getSystemClassLoader();
            // loadClass will first look for already loaded classes. If the class is already present or was injected
            // when loading a jar, this will return the pre-existing class.
            Class<?> classEntry = ucl.loadClass(className);

            if (null == classEntry) {
                Selene.log().info("Failed through direct UCL injection");
                //noinspection ThrowCaughtLocally
                throw new ClassNotFoundException(className);
            }

            // TODO: [High priority] Resolve externally added class entries not having annotations detected
            // classEntry.getAnnotations(); // this is always empty, I do not know why.

            // If the class isn't added to the classpath, the above will cause an
            // exception making it so this line is never reached.
            // Additionally, the addComponentClass method scans if the entry is annotated.
            // This ensures there will be no NPE's here.
            if (context.addComponentClass(classEntry)) {

                Extension header = classEntry.getAnnotation(Extension.class);
                if (null == header) {
                    throw new IllegalStateException("Supposed header is absent from component entry");
                }

                this.createComponentInstance(classEntry, context);

                Selene.log().info("Finished component registration for [" + classEntry.getCanonicalName() + "] with status " + context.getStatus(classEntry));
            }
        } catch (ClassNotFoundException | IllegalStateException e) {
            Selene.getServer().except("Failed to load (supposedly) injected class [" + className + "]", e);
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
                    Selene.log().warn("Detected " + extensions.size() + " extensions in dependent package " + dependentPackage + ". " +
                            "Common code shared by extensions should be implemented in Selene, extensions should NEVER depend on each other.");

            } catch (Throwable e) {
                // Package.getPackage(String) typically returns null if no package with that name is present, this clause is a fail-safe and should
                // technically never be reached. If it is reached we explicitly need to mention the package to prevent future issues (by reporting this).
                Selene.getServer().except("Failed to obtain package [" + dependentPackage + "].", e);
            }
        }

        T instance = null;
        try {
            Constructor<T> defaultConstructor = entry.getConstructor();
            defaultConstructor.setAccessible(true);
            instance = defaultConstructor.newInstance();
            ((Selene) Selene.getServer()).getInjector().injectMembers(instance);
            context.addStatus(entry, ExtensionStatus.LOADED);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            context.addStatus(entry, ExtensionStatus.FAILED);
            Selene.log().warn("No default accessible constructor available for [" + entry.getCanonicalName() + ']');
        } catch (InstantiationException | InvocationTargetException e) {
            context.addStatus(entry, ExtensionStatus.ERRORED);
            Selene.log().warn("Failed to instantiate default constructor for [" + entry.getCanonicalName() + "]. Proceeding to look for injectable constructors.");
        }

        Selene.log().info("Instance for [" + entry.getCanonicalName() + "] = " + instance);
        if (null != instance) instanceMappings.put(header.id(), instance);
    }
}
