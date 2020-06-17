package org.dockbox.darwin.core.util.module;

import org.dockbox.darwin.core.annotations.Module;
import org.dockbox.darwin.core.exceptions.NoModulePresentException;
import org.dockbox.darwin.core.objects.module.ModuleInformation;
import org.dockbox.darwin.core.objects.module.ModuleRegistration;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimpleModuleLoader implements ModuleLoader {

    private final List<ModuleRegistration> registrations = new CopyOnWriteArrayList<>();

    @NotNull
    @Override
    public <I> Optional<I> getModuleInstance(@NotNull Class<I> module) {
        Object potentialInstance = getRegistration(module).getInstance();
        if (potentialInstance.getClass().equals(module)) //noinspection unchecked
            return Optional.of((I) potentialInstance);
        throw new NoModulePresentException(module.getCanonicalName());
    }

    @Override
    public void loadCandidate(@NotNull ModuleCandidate candidate) {
        String source;
        Class<?> clazz;
        if (candidate instanceof ModuleClassCandidate) {
            source = "Integrated";
            clazz = ((ModuleClassCandidate) candidate).getClazz();
        } else if (candidate instanceof ModuleJarCandidate) {
            File sourceFile = ((ModuleJarCandidate) candidate).getSourceFile();
            source = sourceFile.getName();

            JarEntry entry = ((ModuleJarCandidate) candidate).getEntry();
            // Could also be a resource file, those will be ignored
            if (entry.getName().endsWith(".class")) {
                clazz = injectEntry(entry, sourceFile);
                if (clazz == null) return;
            }
            else return;
        } else {
            throw new IllegalArgumentException("Provided candidate is not of a known type");
        }
    private Class<?> injectEntry(JarEntry entry, File sourceFile) {
        Class<?> clazz = null;
        try {
            URLClassLoader ucl = URLClassLoader.newInstance(
                    new URL[]{sourceFile.toURI().toURL()},
                    this.getClass().getClassLoader());
            String className = entry.getName().replace("/", ".").replace(".class", "");

            try {
                clazz = ucl.loadClass(className);
            } catch (ClassNotFoundException e) {
                clazz = Class.forName(className, true, ucl);
            }

            // Ensure the class is a module
            if (!clazz.isAnnotationPresent(Module.class)) return null;

            // Ensure the class has an empty constructor (will throw an exception if not)
            clazz.newInstance();

            // Ensure the class has a dedicated package, if not it may cause issues with other sources
            if (clazz.getPackage() == null)
                throw new IllegalStateException("Found class without dedicated package, this is not permitted!");

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | MalformedURLException e) {
            CoreServer.getServer().except("Failed to load module from source '" + sourceFile.getName() + "'", e);
        }

        return clazz;
    }
    }

    @NotNull
    @Override
    public ModuleInformation getModuleInformation(@NotNull Class<?> module) {
        return getRegistration(module).getInformation();
    }

    @NotNull
    @Override
    public ModuleInformation getModuleInformation(@NotNull String module) {
        return getRegistration(module).getInformation();
    }

    @NotNull
    @Override
    public String getModuleSource(@NotNull Class<?> module) {
        return getRegistration(module).getInformation().getSource();
    }

    @NotNull
    @Override
    public String getModuleSource(@NotNull String module) {
        return getRegistration(module).getInformation().getSource();
    }

    @NotNull
    @Override
    public Module getModule(@NotNull Class<?> module) {
        return getRegistration(module).getInformation().getModule();
    }

    @NotNull
    @Override
    public Module getModule(@NotNull String module) {
        return getRegistration(module).getInformation().getModule();
    }

    @NotNull
    @Override
    public Iterable<ModuleRegistration> getAllRegistrations() {
        return registrations;
    }

    @NotNull
    @Override
    public ModuleRegistration getRegistration(@NotNull Class<?> module) {
        return registrations.stream().filter(registration -> registration.getInstance().getClass().equals(module))
                .findFirst().orElseThrow(() -> new NoModulePresentException(module.getCanonicalName()));
    }

    @NotNull
    @Override
    public ModuleRegistration getRegistration(@NotNull String module) {
        return registrations.stream().filter(registration -> registration.getInformation().getModule().id().equalsIgnoreCase(module))
                .findFirst().orElseThrow(() -> new NoModulePresentException(module));
    }
}
