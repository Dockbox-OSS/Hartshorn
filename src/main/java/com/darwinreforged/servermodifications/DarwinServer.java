package com.darwinreforged.servermodifications;

import com.darwinreforged.servermodifications.modules.root.DisabledModule;
import com.darwinreforged.servermodifications.modules.root.ModuleInfo;
import com.darwinreforged.servermodifications.modules.root.PluginModuleNative;
import com.darwinreforged.servermodifications.resources.Permissions;
import com.darwinreforged.servermodifications.resources.Translations;
import com.google.inject.Inject;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
        version = "aabf015-alpha"
)
public class DarwinServer implements CommandExecutor {

    private final CommandSpec darwinCmd = CommandSpec.builder()
            .description(Text.of("Returns active and failed modules to the player"))
            .permission("darwin.server.admin")
            .executor(this)
            .build();

    private static final Map<Class<? extends PluginModuleNative>, Tuple<PluginModuleNative, ModuleInfo>> MODULES = new HashMap<>();
    private static final List<String> FAILED_MODULES = new ArrayList<>();
    private static final List<String> REQUESTED_COMMANDS = new ArrayList<>();

    @Inject
    private Logger logger;

    private static DarwinServer server;

    public DarwinServer() {
    }

    @Listener(order = Order.LAST)
    public void onServerStart(GameStartedServerEvent event) {
        logger.warn("Sending server starting event to " + DarwinServer.MODULES.size() + " modules");
        Collection<PluginContainer> containerCollection = Sponge.getPluginManager().getPlugins();
        containerCollection.forEach(pluginContainer -> DarwinServer.MODULES.forEach((clazz, module) -> {
            if (module.getSecond().id().equals(pluginContainer.getId()))
                logger.warn(String.format("Found duplicate plugin/module id '%s'", pluginContainer.getId()));
        }));

        DarwinServer.MODULES.forEach((clazz, module) -> module.getFirst().onServerStart(event));
    }

    @Listener(order = Order.LAST)
    public void onServerFinishLoad(GameInitializationEvent event) {

        DarwinServer.server = this;

        Reflections reflections = new Reflections("com.darwinreforged.servermodifications.modules");
        Set<Class<? extends PluginModuleNative>> pluginModules = reflections.getSubTypesOf(PluginModuleNative.class);
        AtomicInteger done = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();
        pluginModules.stream()
                .filter(clazz -> !clazz.getPackage().getName().equals("com.darwinreforged.servermodifications.modules.root"))
                .forEach(clazz -> {
                    logger.warn("Found module '" + clazz.getSimpleName() + "'");
                    try {
                        DisabledModule disabledModule = clazz.getAnnotation(DisabledModule.class);
                        if (disabledModule != null)
                            logger.warn(String.format("- Disabled plugin : %s for reason '%s'", clazz.getSimpleName(), disabledModule.value()));
                        else {
                            logger.warn("- Instantiating module of type '" + clazz.getSimpleName() + "'");
                            Constructor<? extends PluginModuleNative> constructor = clazz.getDeclaredConstructor();
                            PluginModuleNative instance = constructor.newInstance();

                            ModuleInfo moduleInfo = clazz.getAnnotation(ModuleInfo.class);
                            if (moduleInfo == null) throw new InstantiationException();

                            logger.warn(String.format("- Registering listeners for module of type '%s'", clazz.getSimpleName()));
                            DarwinServer.registerListener(instance);

                            int permissionAmount = Permissions.getModulePermissions(clazz).length;
                            logger.warn(String.format("- Registered %d permissions for module of type '%s'", permissionAmount, clazz.getSimpleName()));
                            MODULES.put(clazz, new Tuple<>(instance, moduleInfo));
                            done.getAndIncrement();
                        }
                    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        logger.error(String.format("- Failed to instantiate module of type '%s'", clazz.getSimpleName()));
                        failed.getAndIncrement();
                        FAILED_MODULES.add(clazz.getSimpleName());
                    }
                });
        logger.warn(String.format("Found %d modules and registered %s and failed %s modules", pluginModules.size(), done, failed));

        logger.warn("Sending server finish load event to " + DarwinServer.MODULES.size() + " modules");

        DarwinServer.MODULES.forEach((clazz, module) -> module.getFirst().onServerFinishLoad(event));

        registerCommand(darwinCmd, "dserver");
    }

    public static <I extends PluginModuleNative> Optional<I> getModule(Class<I> clazz) {
        return getTuple(clazz).map(Tuple::getFirst);
    }

    public static <I extends PluginModuleNative> Optional<ModuleInfo> getModuleInfo(Class<I> clazz) {
        return getTuple(clazz).map(Tuple::getSecond);
    }

    @SuppressWarnings("unchecked")
    private static <I extends PluginModuleNative> Optional<Tuple<I, ModuleInfo>> getTuple(Class<I> clazz) {
        try {
            Tuple<I, ModuleInfo> module = (Tuple<I, ModuleInfo>) DarwinServer.MODULES.getOrDefault(clazz, null);
            return Optional.ofNullable(module);
        } catch (ClassCastException e) {
            return Optional.empty();
        }
    }

    public static Logger getLogger() {
        return DarwinServer.server.logger;
    }

    public static DarwinServer getServer() {
        return DarwinServer.server;
    }

    public static void registerListener(Object obj) {
        Sponge.getEventManager().registerListeners(getServer(), obj);
    }

    public static void registerCommand(CommandSpec commandSpec, String... alias) {
        try {
            Sponge.getCommandManager().register(getServer(), commandSpec, alias);
        } catch (IllegalArgumentException e) {
            getLogger().error("Attempted to register command alias(es) '" + String.join(", ", alias) + "' twice");
        }
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        List<Text> moduleContext = new ArrayList<>();
        DarwinServer.MODULES.forEach((clazz, ignored) -> {
            Optional<ModuleInfo> infoOptional = getModuleInfo(clazz);
            if (infoOptional.isPresent()) {
                ModuleInfo info = infoOptional.get();
                String name = info.name();
                String id = info.id();
                boolean disabled = clazz.getAnnotation(DisabledModule.class) != null;
                moduleContext.add(disabled ? Translations.DISABLED_MODULE_ROW.ft(name, id) : Translations.ACTIVE_MODULE_ROW.ft(name, id));
            }
        });
        DarwinServer.FAILED_MODULES.forEach(module -> moduleContext.add(Translations.FAILED_MODULE_ROW.ft(module)));

        PaginationList.Builder builder = PaginationList.builder();
        builder
                .title(Translations.DARWIN_MODULE_TITLE.t())
                .padding(Translations.DARWIN_MODULE_PADDING.t())
                .contents(moduleContext)
                .build().sendTo(src);
        return null;
    }
}
