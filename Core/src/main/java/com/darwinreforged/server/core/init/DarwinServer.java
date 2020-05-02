package com.darwinreforged.server.core.init;

import com.darwinreforged.server.core.entities.Tuple;
import com.darwinreforged.server.core.events.util.EventBus;
import com.darwinreforged.server.core.modules.DisabledModule;
import com.darwinreforged.server.core.modules.ModuleInfo;
import com.darwinreforged.server.core.modules.PluginModuleNative;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.util.FileUtils;

import org.reflections.Reflections;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class DarwinServer {

    protected static final Map<Class<? extends PluginModuleNative>, Tuple<PluginModuleNative, ModuleInfo>> MODULES = new HashMap<>();
    protected static final List<String> FAILED_MODULES = new ArrayList<>();
    protected static final Map<Class<?>, Object> UTILS = new HashMap<>();

    protected EventBus eventBus;
    protected static DarwinServer server;

    public DarwinServer(Class<? extends DarwinServer> implementation) {
        // Create utility implementations
        initUtils(implementation);

        // Create event bus
        this.eventBus = new EventBus();

        // Import permissions and translations
        Translations.collect();
        Permissions.collect();

        // Create integrated modules (in server jar)
        info("Loading integrated modules");
        initModulePackage("com.darwinreforged.server.modules", true);

        // Create external modules (outside server jar, inside modules folder)
        info("Loading external modules");
        initExternalModules();
    }

    public static String getVersion() {
        String version = DarwinServer.class.getPackage().getImplementationVersion();
        return (version == null) ? "DEVELOPMENT" : version;
    }

    private void initExternalModules() {
        Path modDir = getUtilChecked(FileUtils.class).getModuleDirectory();
        try {
            URL url = modDir.toUri().toURL();
            info(String.format("Scanning %s for additional modules", url.toString()));
            Arrays
                    .stream(Objects.requireNonNull(modDir.toFile().listFiles()))
                    .filter(f -> f.getName().endsWith(".jar"))
                    .forEach(moduleCandidate -> {
                        try {
                            List<String> scannableModules = getScannableModules(moduleCandidate);
                            scannableModules.forEach(pkg -> {
                                boolean isModFile = initModulePackage(pkg, false);
                                if (!isModFile) warn(String.format("Found non-module file '%s'", moduleCandidate.getName()));
                                else info(String.format("Done loading module file '%s'", moduleCandidate.getName()));
                            });
                        } catch (IOException | ClassNotFoundException e) {
                            error("Failed to register potential module : " + moduleCandidate.toString());
                            error(e.getMessage());
                        }
                    });
        } catch (MalformedURLException e) {
            error("Failed to load additional modules");
            error(e.getMessage());
        }
    }

    private List<String> getScannableModules(File file) throws IOException, IllegalArgumentException, ClassNotFoundException {
        if (file != null && file.exists() && file.getName().endsWith(".jar")) {
            List<String> scannablePackages = new ArrayList<>();
            try (JarFile jarFile = new JarFile(file)) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.getName().endsWith(".class")) {
                        String name = entry.getName();
                        name = name.substring(0, name.lastIndexOf(".class"));
                        if (name.contains("/"))
                            name = name.replaceAll("/", ".");
                        if (name.contains("\\"))
                            name = name.replaceAll("\\\\", ".");

                        Class<?> clazz = Class.forName(name);
                        String packageName = clazz.getPackage().getName();
                        scannablePackages.add(packageName);
                    }
                }
            }
            return scannablePackages;
        }
        return new ArrayList<>();
    }

    private void initUtils(Class<? extends DarwinServer> implementation) {
        Reflections abstrPackRef = new Reflections("com.darwinreforged.server.core.util");
        Set<Class<?>> abstractUtils = abstrPackRef.getTypesAnnotatedWith(AbstractUtility.class);

        Reflections implPackRef = new Reflections(implementation.getPackage());
        Set<Class<?>> implCandidates = implPackRef.getTypesAnnotatedWith(UtilityImplementation.class);

        abstractUtils.forEach(abstr -> {
            Optional<Class<?>> possibleCandidate = implCandidates.parallelStream().filter(candidate -> candidate.getAnnotation(UtilityImplementation.class).value().equals(abstr)).findAny();
            if (possibleCandidate.isPresent()) {
                try {
                    UTILS.put(abstr, possibleCandidate.get().newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                throw new RuntimeException("Missing implementation for : " + abstr.getSimpleName());
            }
        });
    }

    public abstract ServerType getServerType();

    public EventBus getEventBus() {
        return eventBus;
    }

    @SuppressWarnings("unchecked")
    public static <I> Optional<? extends I> getUtil(Class<I> clazz) {
        Object implementation = UTILS.get(clazz);
        if (implementation != null) return (Optional<? extends I>) Optional.of(implementation);
        return Optional.empty();
    }

    public static <I> I getUtilChecked(Class<I> clazz) {
        Optional<? extends I> optionalImpl = getUtil(clazz);
        return optionalImpl.orElse(null);
    }

    /**
     Obtains the instance of the provided {@link PluginModuleNative} class.

     @param <I>
     The class type extending {@link PluginModuleNative}
     @param clazz
     The class of type {@link I}

     @return The optional module
     */
    public <I extends PluginModuleNative> Optional<I> getModule(Class<I> clazz) {
        return getModDataTuple(clazz).map(Tuple::getFirst);
    }

    @SuppressWarnings("unchecked")
    private <I extends PluginModuleNative> Optional<Tuple<I, ModuleInfo>> getModDataTuple(Class<I> clazz) {
        try {
            Tuple<I, ModuleInfo> module = (Tuple<I, ModuleInfo>) MODULES
                    .getOrDefault(clazz, null);
            return Optional.ofNullable(module);
        } catch (ClassCastException e) {
            return Optional.empty();
        }
    }

    /**
     Obtains the instance of the provided {@link PluginModuleNative} class. If present, returns the registered @{@link
    ModuleInfo} object of the instance.

     @param <I>
     The class type extending {@link PluginModuleNative}
     @param clazz
     The class of type {@link I}

     @return The optional module info of the registered {@link PluginModuleNative} instance
     */
    public <I extends PluginModuleNative> Optional<ModuleInfo> getModuleInfo(Class<I> clazz) {
        return getModDataTuple(clazz).map(Tuple::getSecond);
    }

    /**
     Register a given module and create a singleton instance of it.

     <p>If a module carries the {@link DisabledModule} annotation the module will not be loaded.
     This is to be defined by the creator of the module if the module is unstable or not ready for production.</p>

     <p>If a module is {@link Deprecated} there will still be an attempt to register and instantiate
     it, however a different {@link ModuleRegistration} state is returned.</p>

     @param module
     The module to register

     @return The resulting state of the registration
     */
    public ModuleRegistration registerModule(Class<? extends PluginModuleNative> module) {
        Deprecated deprecatedModule = module.getAnnotation(Deprecated.class);

        // Disabled module
        DisabledModule disabledModule = module.getAnnotation(DisabledModule.class);
        if (disabledModule != null) {
            return ModuleRegistration.DISABLED.setCtx(disabledModule.value());
        }

        try {
            Constructor<? extends PluginModuleNative> constructor = module.getDeclaredConstructor();
            PluginModuleNative instance = constructor.newInstance();

            ModuleInfo moduleInfo = module.getAnnotation(ModuleInfo.class);
            if (moduleInfo == null) throw new InstantiationException("No module info was provided");
            registerListener(instance);
            DarwinServer.MODULES.put(module, new Tuple<>(instance, moduleInfo));

            if (deprecatedModule != null) return ModuleRegistration.DEPRECATED_AND_SUCCEEDED;
            else return ModuleRegistration.SUCCEEDED;

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            DarwinServer.FAILED_MODULES.add(module.getSimpleName());
            if (deprecatedModule != null)
                return ModuleRegistration.DEPRECATED_AND_FAIL.setCtx(e.getMessage());
            else return ModuleRegistration.FAILED.setCtx(e.getMessage());
        }
    }

    public void registerListener(Object obj) {
        getEventBus().subscribe(obj);
    }

    public static DarwinServer getServer() {
        return server;
    }

    public boolean initModulePackage(String pkg, boolean integrated) {
        Reflections reflections = new Reflections(pkg);
        Set<Class<? extends PluginModuleNative>> pluginModules = reflections
                .getSubTypesOf(PluginModuleNative.class);
        if (pluginModules.isEmpty()) return false;

        AtomicInteger done = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();
        pluginModules.forEach(mod -> {
            DarwinServer.ModuleRegistration result = registerModule(mod);
            info(String.format("Found module '%s' (integrates:%s) and loaded with result : %s", mod.getCanonicalName(), integrated, result));
            switch (result) {
                case DEPRECATED_AND_FAIL:
                case FAILED:
                    failed.getAndIncrement();
                    break;
                case SUCCEEDED:
                case DEPRECATED_AND_SUCCEEDED:
                    done.getAndIncrement();
                    break;
                case DISABLED:
                    break;
            }
        });

        return true;
    }

    public abstract void trace(String s);

    public abstract void debug(String s);

    public abstract void info(String s);

    public abstract void warn(String s);

    public abstract void error(String s);

    /**
     Registration states during and after module registration
     */
    public enum ModuleRegistration {
        /**
         Module is disabled.
         */
        DISABLED,
        /**
         Module is deprecated and failed to register.
         */
        DEPRECATED_AND_FAIL,
        /**
         Module is deprecated but registered successfully.
         */
        DEPRECATED_AND_SUCCEEDED,
        /**
         Module failed to register.
         */
        FAILED,
        /**
         Module successfully registered.
         */
        SUCCEEDED;

        /**
         State context, present if passed in by parent (by default only used in DarwinServer on module registration)
         */
        String ctx;

        ModuleRegistration() {
        }

        /**
         Sets context.

         @param ctx
         the context

         @return the module registration state
         */
        public ModuleRegistration setCtx(String ctx) {
            this.ctx = ctx;
            return this;
        }

        /**
         Gets context.

         @return the context
         */
        public String getContext() {
            return this.ctx;
        }

    }

}
