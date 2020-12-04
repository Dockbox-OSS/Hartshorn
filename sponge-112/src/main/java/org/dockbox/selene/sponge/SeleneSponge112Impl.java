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

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDAInfo;

import org.dockbox.selene.core.DiscordUtils;
import org.dockbox.selene.core.MinecraftVersion;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.server.ServerType;
import org.dockbox.selene.sponge.listeners.SpongeCommandListener;
import org.dockbox.selene.sponge.listeners.SpongeDiscordListener;
import org.dockbox.selene.sponge.listeners.SpongePlayerListener;
import org.dockbox.selene.sponge.listeners.SpongeServerListener;
import org.dockbox.selene.sponge.util.SpongeInjector;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Platform.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

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
public class SeleneSponge112Impl extends Selene {

    private final SpongeDiscordListener discordListener = new SpongeDiscordListener();

    /**
     Creates a new Selene instance using the {@link org.dockbox.selene.sponge.util.SpongeInjector} bindings
     providing utilities.
     */
    public SeleneSponge112Impl() {
        super(new SpongeInjector());
    }

    /**
     Sponge Listener method, registers additional listeners present in
     {@link org.dockbox.selene.sponge.listeners.SpongeServerListener}.

     @param event
     Sponge's initialization event
     */
    @Listener
    public void onServerInit(GameInitializationEvent event) {
        // TODO GuusLieben, attempt to convert injector to raw bindings
//      super.upgradeInjectors(this.spongeInjector);
        this.registerSpongeListeners(
                getInstance(SpongeCommandListener.class),
                getInstance(SpongeServerListener.class),
                getInstance(SpongeDiscordListener.class),
                getInstance(SpongePlayerListener.class)
        );
        super.init();
    }

    private void registerSpongeListeners(Object... listeners) {
        for (Object obj : listeners) {
            Sponge.getEventManager().registerListeners(this, obj);
        }
    }

    /**
     Sponge Listener method, registers the MagiBridge JDA instance as soon as it is available.

     Sometimes MagiBridge takes a while to start, if this is the case we register a delayed task to execute
     this method again 30 seconds later.

     @param event
     the event
     */
    @Listener
    public void onServerStartedLate(GameStartedServerEvent event) {
        Exceptional<JDA> oj = getInstance(DiscordUtils.class).getJDA();
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
            // Attempt to get the JDA once every 10 seconds until successful
            Sponge.getScheduler().createTaskBuilder()
                    .delay(10, TimeUnit.SECONDS)
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
    public String getPlatformVersion() {
        return Sponge.getPlatform().getContainer(Component.IMPLEMENTATION).getVersion().orElse("Unknown");
    }

    @Override
    public MinecraftVersion getMinecraftVersion() {
        return MinecraftVersion.MC1_12;
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
