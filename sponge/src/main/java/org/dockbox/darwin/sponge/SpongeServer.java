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

import net.byteflux.libby.LibraryManager;

import org.dockbox.darwin.core.command.CommandBus;
import org.dockbox.darwin.core.events.server.ServerEvent.Init;
import org.dockbox.darwin.core.server.Server;
import org.dockbox.darwin.core.util.extension.ExtensionManager;
import org.dockbox.darwin.core.util.events.EventBus;
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
public class SpongeServer extends Server<LibraryManager> {

    public SpongeServer() {
        super(new SpongeCommonInjector());
    }

    @Listener
    public void onServerInit(GameInitializationEvent event) {
        Sponge.getEventManager().registerListeners(this, new SpongeEventListener());

        EventBus eb = getInstance(EventBus.class);
        CommandBus cb = getInstance(CommandBus.class);
        ExtensionManager cm = getInstance(ExtensionManager.class);

        cm.collectIntegratedExtensions().forEach(componentContext -> {
            componentContext.getClasses().values().forEach(type -> {
                Optional<?> optionalInstance = cm.getInstance(type);
                optionalInstance.ifPresent(i -> {
                    eb.subscribe(i);
                    cb.register(i);
                });
            });
        });
        getInstance(EventBus.class).post(new Init());
    }

    @NotNull
    @Override
    public ServerType getServerType() {
        return ServerType.SPONGE;
    }

    @Override
    protected LibraryManager getLoader() {
        // TODO: See if we can get rid of Libby
        return getInstance(LibraryManager.class);
    }

    @Override
    protected LibraryArtifact[] getArtifacts() {
        // Define libraries to download, specifically targeting Sponge
        return new LibraryArtifact[0];
    }

    public static void main(String[] args) {
        //noinspection UseOfSystemOutOrSystemErr
        System.out.println("DarwinServer is a framework plugin, it should not be started as a separate application.");
        System.exit(8);
    }
}
