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

package org.dockbox.darwin.core.util.module;

import org.dockbox.darwin.core.annotations.Module;
import org.dockbox.darwin.core.objects.module.ModuleClassCandidate;
import org.dockbox.darwin.core.objects.module.ModuleJarCandidate;
import org.dockbox.darwin.core.server.Server;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class SimpleModuleScanner implements ModuleScanner {

    private final List<ModuleJarCandidate> jarCandidates = new CopyOnWriteArrayList<>();
    private final List<ModuleClassCandidate> classCandidates = new CopyOnWriteArrayList<>();
    private List<Class<?>> scannedClasses = new CopyOnWriteArrayList<>();

    @NotNull
    @Override
    public ModuleScanner collectJarCandidates(@NotNull Path path) {
        Arrays.stream(Objects.requireNonNull(path.toFile().listFiles()))
                .filter(f -> f.getName().endsWith(".jar"))
                .forEach(f -> {
                    try {
                        Enumeration<JarEntry> jarEntries = new JarFile(f).entries();
                        while (jarEntries.hasMoreElements()) {
                            JarEntry entry = jarEntries.nextElement();
                            if (entry != null) jarCandidates.add(new ModuleJarCandidate(entry, f));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        return this;
    }

    @NotNull
    @Override
    public ModuleScanner collectClassCandidates(@NotNull String pkg) {
        if ("".equals(pkg)) return this;
        Reflections ref = new Reflections(pkg);
        Set<Class<?>> moduleCandidates = ref.getTypesAnnotatedWith(Module.class);

        if (moduleCandidates.isEmpty()) return this;
        scannedClasses = new CopyOnWriteArrayList<>(moduleCandidates);
        classCandidates.addAll(
                moduleCandidates.stream()
                        .filter(c -> c != Server.class)
                        .map(ModuleClassCandidate::new)
                        .collect(Collectors.toList()));

        return this;
    }

    @NotNull
    @Override
    public Iterable<ModuleJarCandidate> getJarCandidates() {
        return this.jarCandidates;
    }

    @NotNull
    @Override
    public Iterable<ModuleClassCandidate> getClassCandidates() {
        return this.classCandidates;
    }

    @NotNull
    @Override
    public Iterable<Class<?>> getAnnotatedCandidates() {
        return this.classCandidates.stream()
                .filter(candidate -> candidate.getClazz().isAnnotationPresent(Module.class))
                .map(ModuleClassCandidate::getClazz).collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Iterable<Class<?>> getScannedClasses() {
        return this.scannedClasses;
    }
}
