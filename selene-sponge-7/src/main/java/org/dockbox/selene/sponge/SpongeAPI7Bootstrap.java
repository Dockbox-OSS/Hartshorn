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

import com.google.common.reflect.TypeToken;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDAInfo;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.events.EventBus;
import org.dockbox.selene.di.Provider;
import org.dockbox.selene.discord.DiscordUtils;
import org.dockbox.selene.nms.packets.NMSPacket;
import org.dockbox.selene.nms.properties.NativePacketProperty;
import org.dockbox.selene.server.DefaultServer;
import org.dockbox.selene.server.Server;
import org.dockbox.selene.server.minecraft.MinecraftServerBootstrap;
import org.dockbox.selene.server.minecraft.MinecraftServerType;
import org.dockbox.selene.server.minecraft.MinecraftVersion;
import org.dockbox.selene.server.minecraft.events.packet.PacketEvent;
import org.dockbox.selene.server.minecraft.packets.Packet;
import org.dockbox.selene.server.minecraft.players.Players;
import org.dockbox.selene.sponge.listeners.SpongeCommandListener;
import org.dockbox.selene.sponge.listeners.SpongeDiscordListener;
import org.dockbox.selene.sponge.listeners.SpongeEntityListener;
import org.dockbox.selene.sponge.listeners.SpongePlayerListener;
import org.dockbox.selene.sponge.listeners.SpongeServerListener;
import org.dockbox.selene.sponge.objects.composite.Composite;
import org.dockbox.selene.sponge.objects.composite.CompositeDataManipulatorBuilder;
import org.dockbox.selene.sponge.objects.composite.ImmutableCompositeData;
import org.dockbox.selene.sponge.objects.composite.MutableCompositeData;
import org.dockbox.selene.sponge.plotsquared.PlotSquaredEventListener;
import org.dockbox.selene.sponge.util.SpongeInjector;
import org.dockbox.selene.sponge.util.SpongeTaskRunner;
import org.dockbox.selene.util.Reflect;
import org.dockbox.selene.util.SeleneUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Platform.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import eu.crushedpixel.sponge.packetgate.api.listener.PacketListener.ListenerPriority;
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListenerAdapter;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketConnection;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketGate;

/** Sponge API 7.x implementation of Selene, using events to initiate startup tasks. */
@Plugin(
        id = "selene",
        name = "Selene Server",
        description = "Custom plugins and modifications combined into a single source",
        url = "https://github.com/GuusLieben/Selene",
        authors = "GuusLieben",
        dependencies = {
                @Dependency(id = "plotsquared"),
                @Dependency(id = "nucleus"),
                @Dependency(id = "luckperms")
        })
public class SpongeAPI7Bootstrap extends MinecraftServerBootstrap {

    private final SpongeDiscordListener discordListener = new SpongeDiscordListener();
    @Inject
    private PluginContainer container;

    /**
     * Creates a new Selene instance using the {@link org.dockbox.selene.sponge.util.SpongeInjector}
     * bindings providing utilities.
     */
    public SpongeAPI7Bootstrap() {
        super(new SpongeInjector());
    }

    /**
     * The entry point of application, in case it is started directly.
     *
     * @param args
     *         The input arguments
     */
    public static void main(String[] args) {
        // This is the only place where SystemOut is allowed as no server instance can exist at this
        // point.
        //noinspection UseOfSystemOutOrSystemErr
        System.out.println(
                "Selene is a framework plugin, it should not be started as a separate application.");

        // This will cause Forge to complain about direct System.exit references. This only results in a
        // warning
        // message and an automatic redirect to FMLCommonHandler.exitJava.
        System.exit(8);
    }

    public static PluginContainer getContainer() {
        return ((SpongeAPI7Bootstrap) getInstance()).container;
    }

    @SuppressWarnings({ "AnonymousInnerClassMayBeStatic", "UnstableApiUsage" })
    @Listener
    public void on(GamePreInitializationEvent event) {
        Composite.ITEM_KEY = Key.builder()
                .type(new TypeToken<MapValue<String, Object>>() {
                })
                .query(DataQuery.of(Composite.QUERY))
                .id(Composite.ID)
                .name(Composite.NAME)
                .build();

        DataRegistration.builder()
                .dataClass(MutableCompositeData.class)
                .immutableClass(ImmutableCompositeData.class)
                .builder(new CompositeDataManipulatorBuilder())
                .id(Composite.ID)
                .name(Composite.NAME)
                .build();
    }

    /**
     * Sponge Listener method, registers additional listeners present in {@link
     * org.dockbox.selene.sponge.listeners.SpongeServerListener}.
     *
     * @param event
     *         Sponge's initialization event
     */
    @Listener
    public void on(GameInitializationEvent event) {
        super.init();

        this.getInjector().bind(Server.class, DefaultServer.class);

        this.registerSpongeListeners(
                Provider.provide(SpongeCommandListener.class),
                Provider.provide(SpongeServerListener.class),
                Provider.provide(SpongeDiscordListener.class),
                Provider.provide(SpongePlayerListener.class),
                Provider.provide(SpongeEntityListener.class),
                Provider.provide(PlotSquaredEventListener.class)
        );

        Optional<PacketGate> packetGate = Sponge.getServiceManager().provide(PacketGate.class);
        if (packetGate.isPresent()) {
            SpongeAPI7Bootstrap.preparePacketGateListeners(packetGate.get());
            Selene.log().info("Successfully hooked into PacketGate");
        }
        else {
            Selene.log().warn("Missing PacketGate, packet events will not be fired!");
        }
    }

    private void registerSpongeListeners(Object... listeners) {
        for (Object obj : listeners) {
            if (null != obj) Sponge.getEventManager().registerListeners(this, obj);
            else Selene.log().warn("Attempted to register 'null' listener");
        }
    }

    private static void preparePacketGateListeners(PacketGate packetGate) {
        EventBus bus = Provider.provide(EventBus.class);
        Set<Class<? extends Packet>> adaptedPackets = SeleneUtils.emptySet();
        bus.getListenersToInvokers().forEach((k, v) -> v.forEach(
                eventWrapper -> {
                    if (Reflect.assignableFrom(
                            PacketEvent.class, eventWrapper.getEventType())) {
                        Class<? extends Packet> packet = eventWrapper.getMethod()
                                .getAnnotation(org.dockbox.selene.server.minecraft.packets.annotations.Packet.class)
                                .value();

                        // Adapters post the event globally, so we only need to register it once.
                        // This also avoids double-posting of the same event.
                        if (!adaptedPackets.contains(packet)) {
                            Packet emptyPacket = Provider.provide(packet);
                            packetGate.registerListener(
                                    SpongeAPI7Bootstrap.getPacketGateAdapter(packet),
                                    ListenerPriority.DEFAULT,
                                    emptyPacket.getNativePacketType());
                            adaptedPackets.add(packet);
                        }
                    }
                }));
    }

    private static PacketListenerAdapter getPacketGateAdapter(Class<? extends Packet> packet) {
        return new PacketListenerAdapter() {
            @Override
            public void onPacketWrite(eu.crushedpixel.sponge.packetgate.api.event.PacketEvent packetEvent, PacketConnection connection) {
                Provider.provide(Players.class)
                        .getPlayer(connection.getPlayerUUID())
                        .present(player -> {
                            net.minecraft.network.Packet<?> nativePacket = packetEvent.getPacket();
                            Packet internalPacket = Provider.provide(packet, new NativePacketProperty<>(nativePacket));

                            PacketEvent<? extends Packet> event =
                                    new PacketEvent<>(internalPacket, player).post();
                            packetEvent.setCancelled(event.isCancelled());
                            if (event.isModified() && internalPacket instanceof NMSPacket)
                                packetEvent.setPacket(((NMSPacket<?>) internalPacket).getPacket());
                        });
            }
        };
    }

    @NotNull
    @Override
    public MinecraftServerType getServerType() {
        return MinecraftServerType.SPONGE;
    }

    @Override
    public String getPlatformVersion() {
        return Sponge.getPlatform()
                .getContainer(Component.IMPLEMENTATION)
                .getVersion()
                .orElse("Unknown");
    }

    @Override
    public MinecraftVersion getMinecraftVersion() {
        return MinecraftVersion.MC1_12;
    }

    /**
     * Sponge Listener method, registers the MagiBridge JDA instance as soon as it is available.
     *
     * <p>Sometimes MagiBridge takes a while to start, if this is the case we register a delayed task
     * to execute this method again 30 seconds later.
     *
     * @param event
     *         The event
     */
    @Listener
    public void on(GameStartedServerEvent event) {
        Exceptional<JDA> oj = Provider.provide(DiscordUtils.class).getJDA();
        if (oj.present()) {
            JDA jda = oj.get();
            // Avoid registering it twice if the scheduler outside this condition is executing this twice.
            // Usually cancelling all tasks would be preferred, however any module is able to schedule
            // tasks
            // we may not want to cancel.
            if (!jda.getRegisteredListeners().contains(this.discordListener)) {
                jda.addEventListener(this.discordListener);
                Selene.log().info("Initiated JDA" + JDAInfo.VERSION);
            }
        }
        else {
            // Attempt to get the JDA once every 10 seconds until successful
            new SpongeTaskRunner().acceptDelayed(() -> this.on(event), 10, TimeUnit.SECONDS);
        }
    }
}
