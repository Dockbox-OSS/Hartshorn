package com.darwinreforged.server.core;

import com.darwinreforged.server.core.chat.DiscordChatManager;
import com.darwinreforged.server.core.chat.Pagination.PaginationBuilder;
import com.darwinreforged.server.core.chat.Text;
import com.darwinreforged.server.core.commands.CommandBus;
import com.darwinreforged.server.core.commands.annotations.Command;
import com.darwinreforged.server.core.commands.annotations.Permission;
import com.darwinreforged.server.core.events.util.EventBus;
import com.darwinreforged.server.core.files.FileManager;
import com.darwinreforged.server.core.internal.DarwinConfig;
import com.darwinreforged.server.core.internal.ServerType;
import com.darwinreforged.server.core.internal.Utility;
import com.darwinreforged.server.core.modules.DisabledModule;
import com.darwinreforged.server.core.modules.Module;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.tuple.Tuple;
import com.darwinreforged.server.core.types.internal.Singleton;
import com.darwinreforged.server.core.types.living.CommandSender;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 The type Darwin server.
 */
@SuppressWarnings("UnusedReturnValue")
public abstract class DarwinServer extends Singleton {

    /**
     The constant MODULES.
     */
    protected static final Map<Class<?>, Tuple<Object, Module>> MODULES = new HashMap<>();
    /**
     The constant MODULE_SOURCES.
     */
    protected static final Map<String, String> MODULE_SOURCES = new HashMap<>();
    /**
     The constant FAILED_MODULES.
     */
    protected static final List<String> FAILED_MODULES = new ArrayList<>();
    /**
     The constant UTILS.
     */
    protected static final Map<Class<?>, Object> UTILS = new HashMap<>();

    /**
     The Event bus.
     */
    protected EventBus eventBus;
    private String version;
    private String lastUpdate;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private DarwinConfig config;

    /**
     The constant MODULE_PACKAGE.
     */
    protected static final String MODULE_PACKAGE = "com.darwinreforged.server.modules";
    /**
     The constant UTIL_PACKAGE.
     */
    protected static final String CORE_PACKAGE = "com.darwinreforged.server.core";
    /**
     The constant AUTHOR.
     */
    public static final String AUTHOR = "GuusLieben";

    /**
     Instantiates a new Darwin server.

     @throws InstantiationException
     the instantiation exception
     */
    public DarwinServer() throws InstantiationException {
    }

    /**
     Gets log.

     @return the log
     */
    public static Logger getLog() {
        return getServer().log;
    }

    /**
     Sets platform.

     @throws IOException
     the io exception
     */
    @SuppressWarnings({"InstantiationOfUtilityClass"})
    protected void setupPlatform() throws IOException {
        // Load plugin properties
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/darwin.properties"));
        version = properties.getOrDefault("version", "Unknown-dev").toString();
        lastUpdate = properties.getOrDefault("last_update", "Unknown").toString();

        // Create utility implementations
        scanUtilities(getServer().getClass());

        // Create event bus
        this.eventBus = new EventBus();

        // Create integrated modules (in server jar)
        log.info("Loading integrated modules");
        scanModulePackage(MODULE_PACKAGE, true);

        // Set up config
        config = new DarwinConfig();

        // Registering JDA Listeners
        DiscordChatManager du = getUtilChecked(DiscordChatManager.class);
        du.init(DarwinConfig.DISCORD_CHANNEL_WHITELIST.get());

        if (DarwinConfig.LOAD_EXTERNAL_MODULES.get()) {
            // Create external modules (outside server jar, inside modules folder)
            log.info("Loading external modules");
            loadExternalModules();
        }

        // Import permissions and translations
        Translations.collect();
        Permissions.collect();

        // Setting up commands
        CommandBus<?, ?> cb = getUtilChecked(CommandBus.class);
        cb.register(instance.getClass());
        cb.register(DarwinServer.class); // For dserver command
    }

    /**
     Gets config.

     @return the config
     */
    public DarwinConfig getConfig() {
        return config;
    }

    /**
     Gets version.

     @return the version
     */
    public static String getVersion() {
        return (instance == null || getServer().version == null) ? "Unknown-dev" : getServer().version;
    }

    /**
     Gets last update.

     @return the last update
     */
    public static String getLastUpdate() {
        return (instance == null || getServer().lastUpdate == null) ? "Unknown" : getServer().lastUpdate;
    }

    private void loadExternalModules() {
        Path modDir = getUtilChecked(FileManager.class).getModuleDirectory();
        try {
            URL url = modDir.toUri().toURL();
            log.info(String.format("Scanning %s for additional modules", url.toString()));
            Arrays.stream(Objects.requireNonNull(modDir.toFile().listFiles()))
                    .filter(f -> f.getName().endsWith(".jar"))
                    .forEach(this::scanModulesInFile);
        } catch (MalformedURLException e) {
            error("Failed to load additional modules", e);
        }
    }

    /**
     Error.

     @param message
     the message
     */
    public static void error(String message) {
        error(message, new Exception());
    }

    /**
     Error.

     @param message
     the message
     @param e
     the e
     */
    public static void error(String message, Throwable e) {
        if (DarwinConfig.FRIENDLY_ERRORS.get()) {
            StringBuilder b = new StringBuilder();

            String err = b.append(String.join("", Collections.nCopies(5, "=")))
                    .append(e.getClass().toGenericString().split("\\.")[2])
                    .append(String.join("", Collections.nCopies(5, "=")))
                    .append("\n")
                    .append(String.format(" Message -> %s%n", e.getMessage()))
                    .append(String.format(" Source -> %s%n", e.getStackTrace()[0].getClassName()))
                    .append(String.format(" Method -> %s%n", e.getStackTrace()[0].getMethodName()))
                    .append(String.format(" Line -> %d%n", e.getStackTrace()[0].getLineNumber()))
                    .append(String.format(" Additional message -> %s%n", message))
                    .toString();
            getServer().log.error(err);
        }
        if (DarwinConfig.STACKTRACES.get()) {
            e.printStackTrace();
        }
        if (!DarwinConfig.STACKTRACES.get() && !DarwinConfig.FRIENDLY_ERRORS.get()) {
            getServer().log.error(e.getMessage());
        }
    }

    private void scanModulesInFile(File moduleCandidate) {
        if (moduleCandidate != null && moduleCandidate.exists() && moduleCandidate.getName().endsWith(".jar")) {
            try (URLClassLoader ucl = URLClassLoader.newInstance(
                    new URL[]{moduleCandidate.toURI().toURL()},
                    this.getClass().getClassLoader());
                 JarFile jarFile = new JarFile(moduleCandidate)
            ) {

                Enumeration<JarEntry> entries = jarFile.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.getName().endsWith(".class")) {
                        Class<?> clazz;
                        String className = entry.getName().replace("/", ".").replace(".class", "");
                        try {
                            // Inject into classpath
                            try {
                                clazz = ucl.loadClass(className);
                            } catch (ClassNotFoundException e) {
                                clazz = Class.forName(className, true, ucl);
                            }

                            // As classes are external it doesn't match Class types, generic string values are however the same
                            if (clazz.isAnnotationPresent(Module.class)) {
                                // Make sure there is a constructor applicable before accepting it
                                clazz.newInstance();

                                // Require modules to have a dedicated package
                                if (clazz.getPackage() == null) {
                                    error(String.format("Found module candidate without defined package '%s' at %s%n", className, moduleCandidate.getName()));
                                    continue;
                                }

                                registerClasses(moduleCandidate.getName(), clazz);
                            }
                        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                error("Failed to register potential module : " + moduleCandidate.toString(), e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void scanUtilities(Class<? extends DarwinServer> implementation) {
        Reflections abstrPackRef = new Reflections(CORE_PACKAGE);
        Reflections implPackRef = new Reflections(implementation.getPackage().getName());
        Set<Class<?>> abstractUtils = abstrPackRef.getTypesAnnotatedWith(Utility.class);

        abstractUtils.forEach(abstractUtil -> {
            Set<Class<?>> candidates = implPackRef.getSubTypesOf((Class<Object>) abstractUtil);
            if (candidates.size() == 1) {
                try {
                    Class<?> impl = new ArrayList<>(candidates).get(0);
                    UTILS.put(abstractUtil, impl.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    DarwinServer.error("Failed to create instance of utility type", e);
                }
            } else {
                throw new RuntimeException("Missing implementation for : " + abstractUtil.getSimpleName());
            }
        });
    }

    /**
     Gets server type.

     @return the server type
     */
    public abstract ServerType getServerType();

    /**
     Gets event bus.

     @return the event bus
     */
    public static EventBus getEventBus() {
        return getServer().eventBus;
    }

    /**
     Gets util.

     @param <I>
     the type parameter
     @param clazz
     the clazz

     @return the util
     */
    @SuppressWarnings("unchecked")
    public static <I> Optional<? extends I> getUtil(Class<I> clazz) {
        if (!clazz.isAnnotationPresent(Utility.class))
            throw new IllegalArgumentException(String.format("Requested utility class is not annotated as such (%s)", clazz.toGenericString()));
        Object implementation = UTILS.get(clazz);
        if (implementation != null) return (Optional<? extends I>) Optional.of(implementation);
        return Optional.empty();
    }

    /**
     Gets util checked.

     @param <I>
     the type parameter
     @param clazz
     the clazz

     @return the util checked
     */
    public static <I> I getUtilChecked(Class<I> clazz) {
        Optional<? extends I> optionalImpl = getUtil(clazz);
        return optionalImpl.orElse(null);
    }

    /**
     Obtains the instance of the provided Module class.

     @param <I>
     the type parameter
     @param clazz
     the clazz

     @return The optional module
     */
    public static <I> Optional<I> getModule(Class<I> clazz) {
        return getModDataTuple(clazz).map(Tuple::getFirst);
    }

    @SuppressWarnings("unchecked")
    public static <I> Optional<I> getModule(String id) {
        return (Optional<I>) getModDataTuple(id).map(Tuple::getFirst);
    }

    @SuppressWarnings("unchecked")
    private static <I> Optional<Tuple<I, Module>> getModDataTuple(String id) {
        try {
            return MODULES.values().stream().filter(objectModuleTuple -> (objectModuleTuple.getSecond().id().equals(id)))
            .map(objectModuleTuple -> (Tuple<I, Module>) objectModuleTuple).findFirst();
        } catch (ClassCastException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    private static <I> Optional<Tuple<I, Module>> getModDataTuple(Class<I> clazz) {
        try {
            Tuple<Object, Module> module = MODULES
                    .getOrDefault(clazz, null);
            return Optional.ofNullable((Tuple<I, Module>) module);
        } catch (ClassCastException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     Obtains the instance of the provided Module class. If present, returns the registered @{@link
    Module}* object of the instance.

     @param clazz
     The class type

     @return The optional module info of the registered Module instance
     */
    public static Optional<Module> getModuleInfo(Class<?> clazz) {
        return getModDataTuple(clazz).map(Tuple::getSecond);
    }

    /**
     Gets module source.

     @param id
     the id

     @return the module source
     */
    public static String getModuleSource(String id) {
        return DarwinServer.MODULE_SOURCES.get(id);
    }

    /**
     Register a given module and create a singleton instance of it.
     <p>If a module carries the {@link DisabledModule} annotation the module will not be loaded.
     This is to be defined by the creator of the module if the module is unstable or not ready for production.</p>
     <p>If a module is {@link Deprecated} there will still be an attempt to register and instantiate
     it, however a different {@link ModuleRegistration} state is returned.</p>

     @param module
     The module to register
     @param source
     the source

     @return The resulting state of the registration
     */
    protected ModuleRegistration registerModule(Class<?> module, String source) {
        Deprecated deprecatedModule = module.getAnnotation(Deprecated.class);

        // Disabled module
        DisabledModule disabledModule = module.getAnnotation(DisabledModule.class);
        if (disabledModule != null) {
            return ModuleRegistration.DISABLED.setCtx(disabledModule.value());
        }

        try {
            Constructor<?> constructor = module.getDeclaredConstructor();
            Object instance = constructor.newInstance();

            Module moduleInfo = module.getAnnotation(Module.class);
            if (moduleInfo == null) throw new InstantiationException("No module info was provided");
            registerListener(instance);
            CommandBus<?, ?> cb = getUtilChecked(CommandBus.class);
            cb.register(instance.getClass());
            // Do not register the same module twice
            if (getModule(module).isPresent()) return ModuleRegistration.SUCCEEDED;
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

    /**
     Register listener.

     @param obj
     the obj
     */
    public void registerListener(Object obj) {
        getEventBus().subscribe(obj);
    }

    /**
     Gets server.

     @return the server
     */
    public static DarwinServer getServer() {
        return (DarwinServer) DarwinServer.instance;
    }

    /**
     Gets all module info.

     @return the all module info
     */
    public static List<Module> getAllModuleInfo() {
        return MODULES.values().stream().map(Tuple::getSecond).collect(Collectors.toList());
    }

    /**
     Scan module package boolean.

     @param pkg
     the pkg
     @param integrated
     the integrated

     @return the boolean
     */
    public boolean scanModulePackage(String pkg, boolean integrated) {
        return scanModulePackage(pkg, integrated ? "Integrated" : "Unknown");
    }

    private void registerClasses(String source, Class<?>... pluginModules) {
        AtomicInteger done = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();
        Arrays.stream(pluginModules).forEach(mod -> {
            DarwinServer.ModuleRegistration result = registerModule(mod, source);
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
    }

    /**
     Scan module package boolean.

     @param packageString
     the package string
     @param source
     the source

     @return the boolean
     */
    public boolean scanModulePackage(String packageString, String source) {
        if ("".equals(packageString)) return false;
        Reflections reflections = new Reflections(packageString);
        Set<Class<?>> pluginModules = reflections
                .getTypesAnnotatedWith(Module.class);
        if (pluginModules.isEmpty()) return false;

        registerClasses(source, pluginModules.toArray(new Class[0]));
        return true;
    }

    @Command(aliases = "dserver", usage = "dserver", desc = "Returns active and failed modules to the player", min = 0, context = "dserver")
    @Permission(Permissions.ADMIN_BYPASS)
    public void commandList(CommandSender src) {
        List<Text> moduleContext = new ArrayList<>();
        MODULES.forEach((clazz, ignored) -> {
            Optional<Module> infoOptional = getModuleInfo(clazz);
            if (infoOptional.isPresent()) {
                Module info = infoOptional.get();
                String name = info.name();
                String id = info.id();
                boolean disabled = clazz.getAnnotation(DisabledModule.class) != null;
                String source = Translations.MODULE_SOURCE.f(MODULE_SOURCES.get(id));
                moduleContext.add(disabled ? Text.of(Translations.DISABLED_MODULE_ROW.f(name, id, source))
                        : Text.of(Translations.ACTIVE_MODULE_ROW.f(name, id, source)));
            }
        });
        FAILED_MODULES.forEach(module -> moduleContext.add(Text.of(Translations.FAILED_MODULE_ROW.f(module))));

        Text header = Text.of(Translations.DARWIN_SERVER_VERSION.f(getVersion()))
                .append(Text.NEW_LINE)
                .append(Text.of(Translations.DARWIN_SERVER_UPDATE.f(getLastUpdate())))
                .append(Text.NEW_LINE)
                .append(Text.of(Translations.DARWIN_SERVER_AUTHOR.f(AUTHOR)))
                .append(Text.NEW_LINE)
                .append(Text.of(Translations.DARWIN_SERVER_MODULE_HEAD.s()));

        PaginationBuilder builder = PaginationBuilder.builder();
        builder
                .title(Text.of(Translations.DARWIN_MODULE_TITLE.s()))
                .padding(Text.of(Translations.DARWIN_MODULE_PADDING.s()))
                .contents(moduleContext)
                .header(header)
                .build().sendTo(src);
    }

    /**
     Run async.

     @param runnable
     the runnable
     */
    public abstract void runAsync(Runnable runnable);

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
