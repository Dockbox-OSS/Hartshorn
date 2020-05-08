package com.darwinreforged.server.sponge;

import com.darwinreforged.server.core.entities.living.DarwinPlayer;
import com.darwinreforged.server.core.events.internal.ServerInitEvent;
import com.darwinreforged.server.core.events.internal.ServerStartedEvent;
import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.init.ServerType;
import com.darwinreforged.server.core.modules.DisabledModule;
import com.darwinreforged.server.core.modules.Module;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.util.PlayerUtils;
import com.darwinreforged.server.core.util.commands.annotation.Command;
import com.darwinreforged.server.core.util.commands.annotation.Description;
import com.darwinreforged.server.core.util.commands.annotation.Permission;
import com.darwinreforged.server.core.util.commands.annotation.Src;
import com.google.inject.Inject;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 Central plugin which handles module registrations and passes early server events
 */
@Plugin(
        id = "darwinserver",
        name = "Darwin Server",
        description = "Custom plugins and modifications combined into a single source",
        url = "https://darwinreforged.com",
        authors = {DarwinServer.AUTHOR}
)
public class DarwinServerSponge extends DarwinServer {

    public DarwinServerSponge() throws InstantiationException {
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        eventBus.post(new ServerStartedEvent(null));
    }

    @Listener
    public void onServerInit(GameInitializationEvent event) throws IOException {
        setupPlatform();
        Sponge.getEventManager().registerListeners(this, new SpongeListener());
        eventBus.post(new ServerInitEvent(null));
    }

    @Override
    @Permission(Permissions.ADMIN_BYPASS)
    @Description("Returns active and failed modules to the player")
    @Command("dserver")
    public void commandList(@Src DarwinPlayer player) {
        List<Text> moduleContext = new ArrayList<>();
        DarwinServerSponge.MODULES.forEach((clazz, ignored) -> {
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
        DarwinServerSponge.FAILED_MODULES.forEach(module -> moduleContext.add(Text.of(Translations.FAILED_MODULE_ROW.f(module))));

        // TODO : PaginationBuilder inside Core
        AtomicReference<MessageReceiver> tf = new AtomicReference<>();
        if (getUtilChecked(PlayerUtils.class).isConsole(player)) tf.set(Sponge.getServer().getConsole());
        else Sponge.getServer().getPlayer(player.getUuid()).ifPresent(tf::set);

        Text header = Text.builder()
                .append(Text.of(Translations.DARWIN_SERVER_VERSION.f(getVersion())))
                .append(Text.NEW_LINE)
                .append(Text.of(Translations.DARWIN_SERVER_UPDATE.f(getLastUpdate())))
                .append(Text.NEW_LINE)
                .append(Text.of(Translations.DARWIN_SERVER_AUTHOR.f(AUTHOR)))
                .append(Text.NEW_LINE)
                .append(Text.of(Translations.DARWIN_SERVER_MODULE_HEAD.s()))
                .build();

        PaginationList.Builder builder = PaginationList.builder();
        builder
                .title(Text.of(Translations.DARWIN_MODULE_TITLE.s()))
                .padding(Text.of(Translations.DARWIN_MODULE_PADDING.s()))
                .contents(moduleContext)
                .header(header)
                .build().sendTo(tf.get());
    }

    @Override
    public ServerType getServerType() {
        return ServerType.SPONGE;
    }
}
