package com.darwinreforged.servermodifications;

import com.darwinreforged.servermodifications.modules.root.DisabledModule;
import com.darwinreforged.servermodifications.modules.root.PluginModuleNative;
import com.google.inject.Inject;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
        }
)
public class DarwinServer {

    private static final Map<Class<? extends PluginModuleNative>, PluginModuleNative> MODULES = new HashMap<>();

    @Inject
    private Logger logger;

    private static DarwinServer server;

    public DarwinServer() {
        DarwinServer.server = this;
        Reflections reflections = new Reflections("com.darwinreforged.servermodifications.modules");
        Set<Class<? extends PluginModuleNative>> pluginModules = reflections.getSubTypesOf(PluginModuleNative.class);
        pluginModules.forEach(clazz -> {
            try {
                DisabledModule disabledModule = clazz.getAnnotation(DisabledModule.class);
                if (disabledModule != null)
                    logger.warn(String.format("Disabled plugin : %s for reason '%s'", clazz.getSimpleName(), disabledModule.value()));
                else {
                    Constructor<? extends PluginModuleNative> constructor = clazz.getDeclaredConstructor();
                    PluginModuleNative instance = constructor.newInstance();
                    Sponge.getEventManager().registerListeners(this, instance);
                    MODULES.put(clazz, instance);
                }
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                logger.error(String.format("Failed to instantiate module of type '%s'", clazz.getSimpleName()));
            }
        });
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        DarwinServer.MODULES.forEach((clazz, module) -> module.onServerStart(event));
    }

    @Listener
    public void onServerFinishLoad(GameInitializationEvent event) {
        DarwinServer.MODULES.forEach((clazz, module) -> module.onServerFinishLoad(event));
    }

    @SuppressWarnings("unchecked")
    public static <I> Optional<I> getModule(Class<I> clazz) {
        return Optional.ofNullable((I) DarwinServer.MODULES.getOrDefault(clazz, null));
    }

    public static Logger getLogger() {
        return DarwinServer.server.logger;
    }

    public static DarwinServer getServer() {
        return DarwinServer.server;
    }
}
