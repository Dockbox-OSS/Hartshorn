/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.darwin.sponge;

import org.dockbox.darwin.core.command.CommandBus;
import org.dockbox.darwin.core.events.server.ServerEvent;
import org.dockbox.darwin.core.server.Server;
import org.dockbox.darwin.core.util.events.EventBus;
import org.dockbox.darwin.core.util.extension.ExtensionContext;
import org.dockbox.darwin.core.util.extension.ExtensionManager;
import org.dockbox.darwin.core.util.library.LibraryArtifact;
import org.dockbox.darwin.sponge.listeners.SpongeEventListener;
import org.dockbox.darwin.sponge.util.inject.SpongeCommonInjector;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import java.util.Optional;
import java.util.function.Consumer;

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
public class SpongeServer extends Server {

    public SpongeServer() {
        super(new SpongeCommonInjector());
    }

    @Listener
    public void onServerInit(GameInitializationEvent event) {
        Sponge.getEventManager().registerListeners(this, new SpongeEventListener());

        EventBus eb = getInstance(EventBus.class);
        CommandBus cb = getInstance(CommandBus.class);
        ExtensionManager cm = getInstance(ExtensionManager.class);

        super.initIntegratedExtensions(this.getConsumer("integrated", cb, eb, cm));
        super.initExternalExtensions(this.getConsumer("external", cb, eb, cm));

        getInstance(EventBus.class).post(new ServerEvent.Init());
    }

    private Consumer<ExtensionContext> getConsumer(String contextType, CommandBus cb, EventBus eb, ExtensionManager em) {
        return (ExtensionContext ctx) -> ctx.getClasses().values().forEach(type -> {
            log().info("Found type [" + type.getCanonicalName() + "] in " + contextType + " context");
            Optional<?> oi = em.getInstance(type);
            oi.ifPresent(i -> {
                log().info("Registering [" + type.getCanonicalName() + "] as Event and Command listener");
                eb.subscribe(i);
                cb.register(i);
            });
        });
    }

    @NotNull
    @Override
    public ServerType getServerType() {
        return ServerType.SPONGE;
    }

    @Override
    protected LibraryArtifact[] getArtifacts() {
        // Define libraries to download, specifically targeting Sponge.
        // At the time of writing there are no additional libraries required for Sponge.
        return new LibraryArtifact[0];
    }

    public static void main(String[] args) {
        // This is the only place where SystemOut is allowed as no server instance can exist at this point.
        //noinspection UseOfSystemOutOrSystemErr
        System.out.println("DarwinServer is a framework plugin, it should not be started as a separate application.");

        // This will cause Forge to complain about direct System.exit references. This only results in a warning
        // message and an automatic redirect to FMLCommonHandler.exitJava.
        System.exit(8);
    }
}
