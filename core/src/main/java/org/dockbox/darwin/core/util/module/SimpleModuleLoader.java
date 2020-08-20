package org.dockbox.darwin.core.util.module;

import org.dockbox.darwin.core.annotations.Disabled;
import org.dockbox.darwin.core.annotations.Module;
import org.dockbox.darwin.core.exceptions.NoModulePresentException;
import org.dockbox.darwin.core.objects.module.ModuleCandidate;
import org.dockbox.darwin.core.objects.module.ModuleClassCandidate;
import org.dockbox.darwin.core.objects.module.ModuleInformation;
import org.dockbox.darwin.core.objects.module.ModuleJarCandidate;
import org.dockbox.darwin.core.objects.module.ModuleRegistration;
import org.dockbox.darwin.core.objects.module.ModuleStatus;
import org.dockbox.darwin.core.server.Server;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;

public class SimpleModuleLoader implements ModuleLoader {

    private final String idPattern = "([a-z]|[0-9]|_)";
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
    public void loadCandidate(@NotNull Class<?> clazz) {
        handleStatus(registerModule(clazz, "Internal Class Type", new ModuleClassCandidate(clazz)));
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
            assert entry != null : "";
            if (entry.getName().endsWith(".class")) {
                clazz = injectEntry(entry, sourceFile);
                if (clazz == null) return;
            }
            else return;
        } else {
            throw new IllegalArgumentException("Provided candidate is not of a known type");
        }

        handleStatus(registerModule(clazz, source, candidate));
    }

    private void handleStatus(ModuleStatus status) {
        // TODO: Decide how to handle these
        // X1:
        // * Add to failure collection?
        // * Render issue format?
        // * Create log overview?
        // * Other?
        switch (status) {
            case FAILED: // X1
                break;
            case LOADED: // Already added to registrations
                break;
            case ERRORED: // X1
                break;
            case DEPRECATED_LOADED: // Already added to registrations
                break;
            case DEPRECATED_FAILED: // X1
                break;
            case DEPRECATED_ERRORED: // X1
                break;
            case DISABLED: // Add to disabled module list, notify console
                break;
        }
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
            Server.getServer().except("Failed to load module from source '" + sourceFile.getName() + "'", e);
        }

        return clazz;
    }

    private <T> ModuleStatus registerModule(@NotNull Class<T> module, @NotNull String source, @NotNull ModuleCandidate candidate) {
        Module moduleAnnotation = module.getAnnotation(Module.class);

        boolean isDisabled = module.isAnnotationPresent(Disabled.class);
        if (isDisabled) {
            ModuleInformation information = new ModuleInformation(moduleAnnotation, source, ModuleStatus.DISABLED);
            registrations.add(new ModuleRegistration(null, information, candidate));
            return ModuleStatus.DISABLED;
        }

        boolean isDeprecated = module.isAnnotationPresent(Deprecated.class);
        boolean isModuleAnnotated = moduleAnnotation != null;

        if (!isModuleAnnotated) {
            Server.log().error("Module candidate not annotated as such (" + module.getSimpleName() + ")");
            return isDeprecated ? ModuleStatus.DEPRECATED_FAILED : ModuleStatus.FAILED;
        }


        boolean dependenciesPresent = Arrays.stream(moduleAnnotation.dependencies()).allMatch(dep -> {
            try {
                Class.forName(dep);
                return true;
            } catch (ClassNotFoundException e) {
                boolean packagePresent = Package.getPackage(dep) != null;
                if (packagePresent) return true;
                Server.log().warn("Missing dependency '" + dep + "' for module candidate " + module.getSimpleName());
                return false;
            }
        });
        if (!dependenciesPresent) {
            Server.log().error("One or more dependencies were missing for candidate " + module.getSimpleName());
            return isDeprecated ? ModuleStatus.DEPRECATED_FAILED : ModuleStatus.FAILED;
        }


        boolean hasCtors = module.getConstructors().length > 0;
        if (!hasCtors) {
            Server.log().error("No constructors present for module candidate " + module.getSimpleName());
            return isDeprecated ? ModuleStatus.DEPRECATED_FAILED : ModuleStatus.FAILED;
        }

        Constructor<T> ctor = null;
        try {
            ctor = module.getConstructor();
        } catch (ReflectiveOperationException e) {
            Server.log().error("Failed to get constructor for module candidate " + module.getSimpleName());
            return isDeprecated ? ModuleStatus.DEPRECATED_ERRORED : ModuleStatus.ERRORED;
        }

        if (moduleAnnotation.requiresNMS() && !Server.getServer().getServerType().getHasNMSAccess()) {
            Server.log().error("Module candidate requires NMS access but was not provided by current platform (" + module.getSimpleName() + ")");
            return isDeprecated ? ModuleStatus.DEPRECATED_FAILED : ModuleStatus.FAILED;
        }


        if (!moduleAnnotation.id().matches(idPattern)) {
            String lower = moduleAnnotation.id().toLowerCase();
            if (!lower.matches(idPattern)) {
                Server.log().warn("Module registered with uppercase ID '" + moduleAnnotation.id() + "', converting to '" + lower + "'");
            } else {
                Server.log().error("Module candidate registered with incorrect ID format '" + moduleAnnotation.id() + "' (" + module.getSimpleName() + ")");
                return isDeprecated ? ModuleStatus.DEPRECATED_FAILED : ModuleStatus.FAILED;
            }
        }

        try {
            T instance = ctor.newInstance();
            ModuleStatus status = isDeprecated ? ModuleStatus.DEPRECATED_LOADED : ModuleStatus.LOADED;
            ModuleInformation information = new ModuleInformation(moduleAnnotation, source, status);
            registrations.add(new ModuleRegistration(instance, information, candidate));
            return status;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            return isDeprecated ? ModuleStatus.DEPRECATED_ERRORED : ModuleStatus.ERRORED;
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
        return registrations.stream().filter(registration -> Objects.requireNonNull(registration.getInstance()).getClass().equals(module))
                .findFirst().orElseThrow(() -> new NoModulePresentException(module.getCanonicalName()));
    }

    @NotNull
    @Override
    public ModuleRegistration getRegistration(@NotNull String module) {
        return registrations.stream().filter(registration -> registration.getInformation().getModule().id().equalsIgnoreCase(module))
                .findFirst().orElseThrow(() -> new NoModulePresentException(module));
    }
}
