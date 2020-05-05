package com.darwinreforged.server.core.init;

import com.darwinreforged.server.core.entities.DarwinPlayer;
import com.darwinreforged.server.core.entities.Target;
import com.darwinreforged.server.core.entities.Tuple;
import com.darwinreforged.server.core.events.util.EventBus;
import com.darwinreforged.server.core.modules.DisabledModule;
import com.darwinreforged.server.core.modules.ModuleInfo;
import com.darwinreforged.server.core.modules.PluginModule;
import com.darwinreforged.server.core.modules.PluginModuleNative;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.util.CommandUtils;
import com.darwinreforged.server.core.util.FileUtils;
import com.darwinreforged.server.core.util.commands.annotation.Src;
import com.darwinreforged.server.core.util.commands.command.Command;

import org.reflections.Reflections;
import org.slf4j.Logger;

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
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public abstract class DarwinServer extends Target {

    protected static final Map<Class<? extends PluginModuleNative>, Tuple<PluginModuleNative, ModuleInfo>> MODULES = new HashMap<>();
    protected static final Map<String, String> MODULE_SOURCES = new HashMap<>();
    protected static final List<String> FAILED_MODULES = new ArrayList<>();
    protected static final Map<Class<?>, Object> UTILS = new HashMap<>();

    protected EventBus eventBus;
    protected static DarwinServer server;
    private String version;
    private String lastUpdate;

    protected static final String MODULE_PACKAGE = "com.darwinreforged.server.modules";
    protected static final String UTIL_PACKAGE = "com.darwinreforged.server.core.util";
    public static final String AUTHOR = "GuusLieben";

    public DarwinServer() throws InstantiationException {
        if (server != null) throw new InstantiationException("Singleton instance already exists");
        server = this;
    }

    @SuppressWarnings("unchecked")
    protected void setupPlatform() throws IOException {
        // Load plugin properties
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/darwin.properties"));
        version = properties.getOrDefault("version", "Unknown-dev").toString();
        lastUpdate = properties.getOrDefault("last_update", "Unknown").toString();

        // Create utility implementations
        initUtils(server.getClass());

        // Create event bus
        this.eventBus = new EventBus();

        // Create integrated modules (in server jar)
        System.out.println("Loading integrated modules");
        initModulePackage(MODULE_PACKAGE, true);

        // Create external modules (outside server jar, inside modules folder)
        System.out.println("Loading external modules");
        initExternalModules();
        // Import permissions and translations
        Translations.collect();
        Permissions.collect();

        // Setting up commands
        CommandUtils<? extends Command> cu = getUtilChecked(CommandUtils.class);
        cu.registerPackage(server.getClass());
        cu.registerPackage(MODULE_PACKAGE);
        cu.submit();
    }

    public static String getVersion() {
        return (server == null || server.version == null) ? "Unknown-dev" : server.version;
    }

    public static String getLastUpdate() {
        return (server == null || server.lastUpdate == null) ? "Unknown" : server.lastUpdate;
    }

    private void initExternalModules() {
        Path modDir = getUtilChecked(FileUtils.class).getModuleDirectory();
        try {
            URL url = modDir.toUri().toURL();
            System.out.println(String.format("Scanning %s for additional modules", url.toString()));
            Arrays
                    .stream(Objects.requireNonNull(modDir.toFile().listFiles()))
                    .filter(f -> f.getName().endsWith(".jar"))
                    .forEach(moduleCandidate -> {
                        try {
                            List<String> scannableModules = getScannableModules(moduleCandidate);
                            scannableModules.forEach(pkg -> {
                                boolean isModFile = initModulePackage(pkg, false, moduleCandidate.getName());
                                if (!isModFile) System.out.println(String.format("Found non-module file '%s'", moduleCandidate.getName()));
                                else System.out.println(String.format("Done loading module file '%s'", moduleCandidate.getName()));
                            });
                        } catch (IOException | ClassNotFoundException e) {
                            System.err.println("Failed to register potential module : " + moduleCandidate.toString());
                            System.err.println(e.getMessage());
                        }
                    });
        } catch (MalformedURLException e) {
            System.err.println("Failed to load additional modules");
            System.err.println(e.getMessage());
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
        Reflections abstrPackRef = new Reflections(UTIL_PACKAGE);
        Set<Class<?>> abstractUtils = abstrPackRef.getTypesAnnotatedWith(AbstractUtility.class);

        Reflections implPackRef = new Reflections(implementation.getPackage().getName());
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
            e.printStackTrace();
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
    public ModuleRegistration registerModule(Class<? extends PluginModuleNative> module, String source) {
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
            // Do not register the same module twice
            if (getUtil(module).isPresent()) return ModuleRegistration.SUCCEEDED;
            DarwinServer.MODULES.put(module, new Tuple<>(instance, moduleInfo));
            DarwinServer.MODULE_SOURCES.put(moduleInfo.id(), source);

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

    public static List<ModuleInfo> getAllModuleInfo() {
        return MODULES.values().stream().map(Tuple::getSecond).collect(Collectors.toList());
    }

    public boolean initModulePackage(String pkg, boolean integrated) {
        return initModulePackage(pkg, integrated, integrated ? "Integrated" : "Unknown");
    }

    public boolean initModulePackage(String pkg, boolean integrated, String source) {
        Reflections reflections = new Reflections(pkg);
        Set<Class<? extends PluginModuleNative>> pluginModules = reflections
                .getSubTypesOf(PluginModuleNative.class);
        if (pluginModules.isEmpty()) return false;

        AtomicInteger done = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();
        pluginModules.stream().filter(mod -> !mod.equals(PluginModule.class)).forEach(mod -> {
            DarwinServer.ModuleRegistration result = registerModule(mod, source);
            System.out.println(String.format("Found module '%s' (integrated:%s) and loaded with result : %s", mod.getCanonicalName(), integrated, result));
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

    public abstract void commandList(@Src DarwinPlayer player);

    @Override
    public UUID getUuid() {
        return UUID.fromString("0-0-0-0");
    }

    @Override
    public String getName() {
        return "DarwinServerHost";
    }

    public abstract Logger getLogger();

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
