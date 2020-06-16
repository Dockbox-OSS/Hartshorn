package org.dockbox.darwin.sponge;

import org.dockbox.darwin.core.server.CoreServer;
import org.dockbox.darwin.core.util.module.ModuleLoader;
import org.dockbox.darwin.core.util.module.ModuleScanner;
import org.dockbox.darwin.sponge.util.inject.SpongeExceptionInjector;
import org.dockbox.darwin.sponge.util.inject.SpongeModuleInjector;
import org.dockbox.darwin.sponge.util.inject.SpongeUtilInjector;
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
        super(new SpongeModuleInjector(), new SpongeExceptionInjector(), new SpongeUtilInjector());
    }

    @Listener
    public void onServerInit(GameInitializationEvent event) {
        // TODO : Init event listener
        Iterable<Class<?>> annotatedCandidates = CoreServer.getInstance(ModuleScanner.class)
                .collectClassCandidates("org.dockbox.darwin.integrated")
                .getAnnotatedCandidates();
        annotatedCandidates.forEach(module -> CoreServer.getInstance(ModuleLoader.class).loadModule(module));
        // TODO : Post init event to modules
    }
}
