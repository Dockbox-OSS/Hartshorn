package com.darwinreforged.server.core.init;

import com.darwinreforged.server.core.events.test.EventBus;
import com.darwinreforged.server.core.files.FileManager;
import com.darwinreforged.server.core.modules.DisabledModule;
import com.darwinreforged.server.core.modules.ModuleInfo;
import com.darwinreforged.server.core.modules.PluginModuleNative;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.util.PlayerUtils;
import com.darwinreforged.server.core.util.Tuple;

import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class DarwinServer<T> {

    protected static final Map<Class<? extends PluginModuleNative>, Tuple<PluginModuleNative, ModuleInfo>> MODULES = new HashMap<>();
    protected static final List<String> FAILED_MODULES = new ArrayList<>();
    protected EventBus eventBus;
    protected static DarwinServer<?> server;
    protected ServerType serverType;
    protected FileManager fileManager;
    protected PlayerUtils<T> playerUtils;

    public DarwinServer(ServerType serverType, FileManager fileManager, PlayerUtils<T> playerUtils) {
        this.serverType = serverType;
        this.fileManager = fileManager;
        this.playerUtils = playerUtils;
        this.eventBus = new EventBus();
    }

    public static ServerType getServerType() {
        return getServer().serverType;
    }

    public static FileManager getFileManager() {
        return getServer().fileManager;
    }

    public PlayerUtils<T> getPlayerUtils() {
        return playerUtils;
    }

    public EventBus getEventBus() {
        return eventBus;
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

    public static DarwinServer<?> getServer() {
        return server;
    }

    public void initModules() {
        Reflections reflections = new Reflections("com.darwinreforged.server.modules");
        Set<Class<? extends PluginModuleNative>> pluginModules = reflections
                .getSubTypesOf(PluginModuleNative.class);
        AtomicInteger done = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();
        pluginModules.forEach(mod -> {
            DarwinServer.ModuleRegistration result = registerModule(mod);
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

        Translations.collect();
        Permissions.collect();
    }

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
