package com.darwinreforged.server.api;

import com.darwinreforged.server.api.modules.DisabledModule;
import com.darwinreforged.server.api.modules.ModuleInfo;
import com.darwinreforged.server.api.modules.PluginModuleNative;
import com.darwinreforged.server.api.resources.Permissions;
import com.darwinreforged.server.api.resources.Translations;
import com.google.inject.Inject;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 Central plugin which handles module registrations and passes early server events
 */
@Plugin(
        id = "darwinserver",
        name = "Darwin Server",
        description = "Custom plugins and modifications combined into a single source",
        url = "https://darwinreforged.com",
        authors = {
                "GuusLieben",
                "TheCrunchy",
                "simbolduc",
                "pumbas600"
        },
        version = "71f4502-alpha"
)
public class DarwinServer
        implements CommandExecutor {

    private final CommandSpec darwinCmd = CommandSpec.builder()
            .description(Text.of("Returns active and failed modules to the player"))
            .permission("darwin.server.admin")
            .executor(this)
            .build();

    private static final Map<Class<? extends PluginModuleNative>, Tuple<PluginModuleNative, ModuleInfo>> MODULES = new HashMap<>();
    private static final List<String> FAILED_MODULES = new ArrayList<>();

    @Inject
    private Logger logger;

    private static DarwinServer server;

    /**
     Public constructor for the DarwinServer instance.
     */
    public DarwinServer() {
        if (server != null) throw new RuntimeException("Singleton instance already exists");
    }

    /**
     Obtains the instance of the provided {@link PluginModuleNative} class.

     @param <I>
     The class type extending {@link PluginModuleNative}
     @param clazz
     The class of type {@link I}

     @return The optional module
     */
    public static <I extends PluginModuleNative> Optional<I> getModule(Class<I> clazz) {
        return getModDataTuple(clazz).map(Tuple::getFirst);
    }

    @SuppressWarnings("unchecked")
    private static <I extends PluginModuleNative> Optional<Tuple<I, ModuleInfo>> getModDataTuple(Class<I> clazz) {
        try {
            Tuple<I, ModuleInfo> module = (Tuple<I, ModuleInfo>) DarwinServer.MODULES
                    .getOrDefault(clazz, null);
            return Optional.ofNullable(module);
        } catch (ClassCastException e) {
            return Optional.empty();
        }
    }

    /**
     Register a given command using the given list of aliases.

     <p>Will always use the {@link DarwinServer} plugin instance to register the commands to
     Sponge.</p>

     <p>See {@link org.spongepowered.api.command.CommandManager} for further documentation.</p>

     @param callable
     The command
     @param alias
     An array of aliases
     */
    public static void registerCommand(CommandCallable callable, String... alias) {
        try {
            Sponge.getCommandManager().register(getServer(), callable, alias);
        } catch (IllegalArgumentException e) {
            getLogger()
                    .error("Attempted to register command alias(es) '" + String.join(", ", alias) + "' twice");
        }
    }

    /**
     Gets the {@link DarwinServer} plugin instance. There is ever only going to be a single instance at any given time.

     @return The plugin instance
     */
    public static DarwinServer getServer() {
        return DarwinServer.server;
    }

    /**
     Gets logger.

     @return the logger
     */
    public static Logger getLogger() {
        return DarwinServer.server.logger;
    }

    /**
     Looks up and registers all modules in {@link com.darwinreforged.server.modules}.

     <p>Only modules implementing {@link PluginModuleNative} will be registered.</p>

     <p>The amount of registered and failed modules are tracked and logged at the INFO level. Issues
     with the registration state are logged at the WARN leven.</p>

     <p>Once registered all succeeded modules will be invoked on their
     {@link PluginModuleNative#onServerFinishLoad(GameInitializationEvent)} method. Any exceptions will be logged on a
     per-module basis at the ERROR level}</p>

     @param event
     The {@link GameInitializationEvent} event provided by Sponge
     */
    @Listener(order = Order.LAST)
    public void onServerFinishLoad(GameInitializationEvent event) {
        DarwinServer.server = this;

        Reflections reflections = new Reflections("com.darwinreforged.servermodifications.modules");
        Set<Class<? extends PluginModuleNative>> pluginModules = reflections
                .getSubTypesOf(PluginModuleNative.class);
        AtomicInteger done = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();
        pluginModules.stream()
                .filter(clazz -> !clazz.getPackage().getName()
                        .equals("com.darwinreforged.servermodifications.modules.root"))
                .forEach(mod -> {
                    ModuleRegistration result = registerModule(mod);
                    switch (result) {
                        case DEPRECATED_AND_FAIL:
                        case FAILED:
                            failed.getAndIncrement();
                            break;
                        case SUCCEEDED:
                            done.getAndIncrement();
                            break;
                        case DEPRECATED_AND_SUCCEEDED:
                            done.getAndIncrement();
                            logger.warn(mod.getSimpleName() + " is being deprecated, registration of this module may cause unexpected behavior!");
                            break;
                        case DISABLED:
                            logger.warn("Did not load module '" + mod.getSimpleName() + "' as it is disabled.");
                    }
                });
        logger.info(String.format("Found %d modules and registered %s and failed %s modules", pluginModules
                .size(), done, failed));

        logger.info("Collecting translations from config");
        Translations.collect();
        Permissions.collect();

        logger.info("Sending server finish load event to " + DarwinServer.MODULES
                .size() + " modules");

        DarwinServer.MODULES.forEach((clazz, module) -> {
            try {
                module.getFirst().onServerFinishLoad(event);
            } catch (Exception e) {
                getLogger().error(String.format("Caught exception from %s : %s", module.getSecond()
                        .name(), e.getMessage()));
                DarwinServer.MODULES.remove(clazz);
                DarwinServer.FAILED_MODULES.add(clazz.getSimpleName());
            }
        });

        registerCommand(darwinCmd, "dserver");
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
    public static ModuleRegistration registerModule(Class<? extends PluginModuleNative> module) {
        getLogger().warn("Requested module '" + module.getSimpleName() + "'");

        Deprecated deprecatedModule = module.getAnnotation(Deprecated.class);

        // Disabled module
        DisabledModule disabledModule = module.getAnnotation(DisabledModule.class);
        if (disabledModule != null) {
            getLogger()
                    .warn(String.format("- Disabled plugin : %s for reason '%s'", module.getSimpleName(), disabledModule
                            .value()));
            return ModuleRegistration.DISABLED.setCtx(disabledModule.value());
        }

        try {
            getLogger().warn("- Instantiating module of type '" + module.getSimpleName() + "'");
            Constructor<? extends PluginModuleNative> constructor = module.getDeclaredConstructor();
            PluginModuleNative instance = constructor.newInstance();

            ModuleInfo moduleInfo = module.getAnnotation(ModuleInfo.class);
            if (moduleInfo == null) throw new InstantiationException("No module info was provided");

            getLogger().warn(String.format("- Registering listeners for module of type '%s'", module.getSimpleName()));
            DarwinServer.registerListener(instance);

            DarwinServer.MODULES.put(module, new Tuple<>(instance, moduleInfo));

            if (deprecatedModule != null) return ModuleRegistration.DEPRECATED_AND_SUCCEEDED;
            else return ModuleRegistration.SUCCEEDED;

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            getLogger()
                    .error(String.format("- Failed to instantiate module of type '%s'", module.getSimpleName()));
            DarwinServer.FAILED_MODULES.add(module.getSimpleName());
            if (deprecatedModule != null)
                return ModuleRegistration.DEPRECATED_AND_FAIL.setCtx(e.getMessage());
            else return ModuleRegistration.FAILED.setCtx(e.getMessage());
        }
    }

    /**
     Register a given command using the given list of aliases.

     <p>Will always use the {@link DarwinServer} plugin instance to register the commands to
     Sponge.</p>

     <p>See {@link org.spongepowered.api.command.CommandManager} for further documentation.</p>

     @param commandSpec
     The command
     @param alias
     An array of aliases
     */
    public static void registerCommand(CommandSpec commandSpec, String... alias) {
        try {
            Sponge.getCommandManager().register(getServer(), commandSpec, alias);
        } catch (IllegalArgumentException e) {
            getLogger()
                    .error("Attempted to register command alias(es) '" + String.join(", ", alias) + "' twice");
        }
    }

    /**
     Registers {@link org.spongepowered.api.event.Event} methods annotated with @{@link Listener} in the specified
     object.

     <p>Will always use the {@link DarwinServer} plugin instance to register listeners to Sponge</p>

     <p>See {@link org.spongepowered.api.event.EventManager} for further documentation.</p>

     @param obj
     The object
     */
    public static void registerListener(Object obj) {
        Sponge.getEventManager().registerListeners(getServer(), obj);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args)
            throws CommandException {
        List<Text> moduleContext = new ArrayList<>();
        DarwinServer.MODULES.forEach((clazz, ignored) -> {
            Optional<ModuleInfo> infoOptional = getModuleInfo(clazz);
            if (infoOptional.isPresent()) {
                ModuleInfo info = infoOptional.get();
                String name = info.name();
                String id = info.id();
                boolean disabled = clazz.getAnnotation(DisabledModule.class) != null;
                moduleContext.add(disabled ? Translations.DISABLED_MODULE_ROW
                        .ft(name, id) : Translations.ACTIVE_MODULE_ROW
                        .ft(name, id));
            }
        });
        DarwinServer.FAILED_MODULES
                .forEach(module -> moduleContext.add(Translations.FAILED_MODULE_ROW.ft(module)));

        PaginationList.Builder builder = PaginationList.builder();
        builder
                .title(Translations.DARWIN_MODULE_TITLE.t())
                .padding(Translations.DARWIN_MODULE_PADDING.t())
                .contents(moduleContext)
                .build().sendTo(src);
        return CommandResult.success();
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
    public static <I extends PluginModuleNative> Optional<ModuleInfo> getModuleInfo(Class<I> clazz) {
        return getModDataTuple(clazz).map(Tuple::getSecond);
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
        ModuleRegistration setCtx(String ctx) {
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
