package org.dockbox.darwin.core.util.module;

import org.dockbox.darwin.core.annotations.Module;
import org.dockbox.darwin.core.objects.module.ModuleClassCandidate;
import org.dockbox.darwin.core.objects.module.ModuleJarCandidate;
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
import java.util.stream.Stream;

public class SimpleModuleScanner implements ModuleScanner {

    private final List<ModuleJarCandidate> jarCandidates = new CopyOnWriteArrayList<>();
    private final List<ModuleClassCandidate> classCandidates = new CopyOnWriteArrayList<>();

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
                            if (entry != null) jarCandidates.add(new ModuleJarCandidate(entry));
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
        classCandidates.addAll(moduleCandidates.stream().map(ModuleClassCandidate::new).collect(Collectors.toList()));

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

    @Override
    public Stream<Class<?>> getAnnotatedCandidates() {
        return this.classCandidates.stream()
                .filter(candidate -> candidate.getClazz().isAnnotationPresent(Module.class))
                .map(ModuleClassCandidate::getClazz);
    }

}
