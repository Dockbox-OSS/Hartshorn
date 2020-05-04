package com.darwinreforged.server.sponge;

import com.darwinreforged.server.core.entities.DarwinPlayer;
import com.darwinreforged.server.core.events.ServerInitEvent;
import com.darwinreforged.server.core.events.ServerStartedEvent;
import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.init.ServerType;
import com.darwinreforged.server.core.modules.DisabledModule;
import com.darwinreforged.server.core.modules.ModuleInfo;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.util.commands.annotation.Command;
import com.darwinreforged.server.core.util.commands.annotation.Description;
import com.darwinreforged.server.core.util.commands.annotation.Permission;
import com.darwinreforged.server.core.util.commands.annotation.Src;
import com.google.inject.Inject;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 Central plugin which handles module registrations and passes early server events
 */
@Plugin(
        id = "darwinserver",
        name = "Darwin Server",
        description = "Custom plugins and modifications combined into a single source",
        url = "https://darwinreforged.com",
        authors = {"GuusLieben"}
)
public class DarwinServerSponge extends DarwinServer {

    @Inject
    Logger logger;

    @Override
    public Logger getLogger() {
        return logger;
    }

    public DarwinServerSponge() throws InstantiationException {
        super(DarwinServerSponge.class);
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        eventBus.post(new ServerStartedEvent(null));
    }

    @Listener
    public void onServerInit(GameInitializationEvent event) {
        setupPlatform();
        eventBus.post(new ServerInitEvent(null));
    }

    @Override
    @Permission("darwin.server.admin")
    @Description("Returns active and failed modules to the player")
    @Command("dserver")
    public void commandList(@Src DarwinPlayer player) {
        List<Text> moduleContext = new ArrayList<>();
        DarwinServerSponge.MODULES.forEach((clazz, ignored) -> {
            Optional<ModuleInfo> infoOptional = getModuleInfo(clazz);
            if (infoOptional.isPresent()) {
                ModuleInfo info = infoOptional.get();
                String name = info.name();
                String id = info.id();
                boolean disabled = clazz.getAnnotation(DisabledModule.class) != null;
                moduleContext.add(disabled ? Text.of(Translations.DISABLED_MODULE_ROW.f(name, id))
                        : Text.of(Translations.ACTIVE_MODULE_ROW.f(name, id)));
            }
        });
        DarwinServerSponge.FAILED_MODULES.forEach(module -> moduleContext.add(Text.of(Translations.FAILED_MODULE_ROW.f(module))));

        Optional<Player> optionalPlayer = Sponge.getServer().getPlayer(player.getUuid());
        if (optionalPlayer.isPresent()) {
            PaginationList.Builder builder = PaginationList.builder();
            builder
                    .title(Text.of(Translations.DARWIN_MODULE_TITLE.s()))
                    .padding(Text.of(Translations.DARWIN_MODULE_PADDING.s()))
                    .contents(moduleContext)
                    .footer(Text.of(Translations.DARWIN_SERVER_VERSION.f(getVersion())))
                    .build().sendTo(optionalPlayer.get());
        }
    }

    @Override
    public ServerType getServerType() {
        return ServerType.SPONGE;
    }
}
