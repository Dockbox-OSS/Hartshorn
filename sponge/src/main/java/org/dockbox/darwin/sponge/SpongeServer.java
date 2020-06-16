package org.dockbox.darwin.sponge;

import org.dockbox.darwin.core.server.CoreServer;
import org.dockbox.darwin.core.util.module.ModuleLoader;
import org.dockbox.darwin.core.util.module.ModuleScanner;
import org.dockbox.darwin.sponge.util.inject.SpongeCommonInjector;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;


@Plugin(
        id = "darwinserver",
        name = "Darwin Server",
        description = "Custom plugins and modifications combined into a single source",
        url = "https://darwinreforged.com",
        authors = "GuusLieben",
        dependencies = {
                @Dependency(id = "plotsquared"),
                @Dependency(id = "nucleus"),
                @Dependency(id = "luckperms")
        }
)
public class SpongeServer extends CoreServer {

    public SpongeServer() {
        super(new SpongeCommonInjector());
    }

    @Listener
    public void onServerInit(GameInitializationEvent event) {
        // TODO : Register module instances as listeners
        Iterable<Class<?>> annotatedCandidates = CoreServer.getInstance(ModuleScanner.class)
                .collectClassCandidates("org.dockbox.darwin.integrated")
                .getAnnotatedCandidates();
        annotatedCandidates.forEach(module -> CoreServer.getInstance(ModuleLoader.class).loadModule(module));
        getInstance(EventBus.class).post(new Init());
    }

    @NotNull
    @Override
    public ServerType getServerType() {
        return ServerType.SPONGE;
    }
}
