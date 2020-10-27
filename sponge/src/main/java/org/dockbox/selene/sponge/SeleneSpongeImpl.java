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

package org.dockbox.selene.sponge;

import com.google.inject.Inject;
import com.google.inject.Injector;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDAInfo;

import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.util.discord.DiscordUtils;
import org.dockbox.selene.core.util.library.LibraryArtifact;
import org.dockbox.selene.sponge.listeners.SpongeDiscordListener;
import org.dockbox.selene.sponge.listeners.SpongeServerEventListener;
import org.dockbox.selene.sponge.util.inject.SpongeCommonInjector;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Platform.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 Sponge implementation of Selene, using events to initiate startup tasks.
 */
@Plugin(
        id = "selene",
        name = "Selene Server",
        description = "Custom plugins and modifications combined into a single source",
        url = "https://github.com/GuusLieben/Selene",
        authors = "GuusLieben",
        dependencies = {
                @Dependency(id = "plotsquared"),
                @Dependency(id = "nucleus"),
                @Dependency(id = "luckperms"),
                @Dependency(id = "spotlin")
        }
)
public class SeleneSpongeImpl extends Selene {

    @Inject
    private Injector spongeInjector;

    private final SpongeDiscordListener discordListener = new SpongeDiscordListener();

    /**
     Creates a new Selene instance using the {@link org.dockbox.selene.sponge.util.inject.SpongeCommonInjector} bindings
     providing utilities.
     */
    public SeleneSpongeImpl() {
        super(new SpongeCommonInjector());
    }

    /**
     Sponge Listener method, registers additional listeners present in
     {@link org.dockbox.selene.sponge.listeners.SpongeServerEventListener}.

     @param event
     Sponge's initialization event
     */
    @Listener
    public void onServerInit(GameInitializationEvent event) {
        super.upgradeInjectors(this.spongeInjector);
        Sponge.getEventManager().registerListeners(this, new SpongeServerEventListener());
        super.init();
    }

    @Listener
    public void onServerStarted(GameStartedServerEvent event) {
        super.debugRegisteredInstances();
    }

    /**
     Sponge Listener method, registers the MagiBridge JDA instance as soon as it is available.

     Sometimes MagiBridge takes a while to start, if this is the case we register a delayed task to execute
     this method again 30 seconds later.

     @param event
     the event
     */
    @Listener(order = Order.LAST)
    public void onServerStartedLate(GameStartedServerEvent event) {
        Optional<JDA> oj = getInstance(DiscordUtils.class).getJDA();
        if (oj.isPresent()) {
            JDA jda = oj.get();
            // Avoid registering it twice if the scheduler outside this condition is executing this twice.
            // Usually cancelling all tasks would be preferred, however any extension is able to schedule tasks
            // we may not want to cancel.
            if (!jda.getRegisteredListeners().contains(this.discordListener)) {
                jda.addEventListener(this.discordListener);
                log().info("Initiated JDA" + JDAInfo.VERSION);
            }
        } else {
            // Attempt to get the JDA once every 30 seconds until successful
            Sponge.getScheduler().createTaskBuilder()
                    .delay(30, TimeUnit.SECONDS)
                    .execute(() -> this.onServerStartedLate(event))
                    .name("JDA_scheduler")
                    .async()
                    .submit(this);
        }

    }

    @NotNull
    @Override
    public ServerType getServerType() {
        return ServerType.SPONGE;
    }

    @Override
    protected LibraryArtifact[] getPlatformArtifacts() {
        // Define libraries to download, specifically targeting Sponge.
        // At the time of writing there are no additional libraries required for Sponge.
        return new LibraryArtifact[0];
    }

    @Override
    public String getPlatformVersion() {
        return Sponge.getPlatform().getContainer(Component.IMPLEMENTATION).getVersion().orElse("Unknown");
    }

    @Override
    public String getMinecraftVersion() {
        return Sponge.getPlatform().getMinecraftVersion().getName();
    }


    /**
     The entry point of application, in case it is started directly.

     @param args
     the input arguments
     */
    public static void main(String[] args) {
        // This is the only place where SystemOut is allowed as no server instance can exist at this point.
        //noinspection UseOfSystemOutOrSystemErr
        System.out.println("Selene is a framework plugin, it should not be started as a separate application.");

        // This will cause Forge to complain about direct System.exit references. This only results in a warning
        // message and an automatic redirect to FMLCommonHandler.exitJava.
        System.exit(8);
    }
}
