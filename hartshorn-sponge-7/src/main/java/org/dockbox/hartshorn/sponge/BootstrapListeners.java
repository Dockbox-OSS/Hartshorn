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

package org.dockbox.hartshorn.sponge;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDAInfo;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.events.EventBus;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.discord.DiscordUtils;
import org.dockbox.hartshorn.nms.packets.NMSPacket;
import org.dockbox.hartshorn.nms.properties.NativePacketProperty;
import org.dockbox.hartshorn.server.minecraft.events.packet.PacketEvent;
import org.dockbox.hartshorn.server.minecraft.packets.Packet;
import org.dockbox.hartshorn.server.minecraft.players.Players;
import org.dockbox.hartshorn.sponge.listeners.SpongeCommandListener;
import org.dockbox.hartshorn.sponge.listeners.SpongeDiscordListener;
import org.dockbox.hartshorn.sponge.listeners.SpongeEntityListener;
import org.dockbox.hartshorn.sponge.listeners.SpongePlayerListener;
import org.dockbox.hartshorn.sponge.listeners.SpongeServerListener;
import org.dockbox.hartshorn.sponge.plotsquared.PlotSquaredEventListener;
import org.dockbox.hartshorn.sponge.util.SpongeTaskRunner;
import org.dockbox.hartshorn.util.Reflect;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import eu.crushedpixel.sponge.packetgate.api.listener.PacketListener.ListenerPriority;
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListenerAdapter;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketConnection;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketGate;

public class BootstrapListeners {

    private final SpongeDiscordListener discordListener = new SpongeDiscordListener();

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
        Exceptional<JDA> oj = Hartshorn.context().get(DiscordUtils.class).getJDA();
        if (oj.present()) {
            JDA jda = oj.get();
            // Avoid registering it twice if the scheduler outside this condition is executing this twice.
            // Usually cancelling all tasks would be preferred, however any module is able to schedule
            // tasks
            // we may not want to cancel.
            if (!jda.getRegisteredListeners().contains(this.discordListener)) {
                jda.addEventListener(this.discordListener);
                Hartshorn.log().info("Initiated JDA" + JDAInfo.VERSION);
            }
        }
        else {
            // Attempt to get the JDA once every 10 seconds until successful
            new SpongeTaskRunner().acceptDelayed(() -> this.on(event), 10, TimeUnit.SECONDS);
        }
    }

    /**
     * Sponge Listener method, registers additional listeners present in {@link
     * org.dockbox.hartshorn.sponge.listeners.SpongeServerListener}.
     *
     * @param event
     *         Sponge's initialization event
     */
    @Listener
    public void on(GameInitializationEvent event) {
        try {
            Sponge7Application.instance.init.run();

            this.registerSpongeListeners(
                    Hartshorn.context().get(SpongeCommandListener.class),
                    Hartshorn.context().get(SpongeServerListener.class),
                    Hartshorn.context().get(SpongeDiscordListener.class),
                    Hartshorn.context().get(SpongePlayerListener.class),
                    Hartshorn.context().get(SpongeEntityListener.class),
                    Hartshorn.context().get(PlotSquaredEventListener.class)
            );

            Optional<PacketGate> packetGate = Sponge.getServiceManager().provide(PacketGate.class);
            if (packetGate.isPresent()) {
                BootstrapListeners.preparePacketGateListeners(packetGate.get());
                Hartshorn.log().info("Successfully hooked into PacketGate");
            }
            else {
                Hartshorn.log().warn("Missing PacketGate, packet events will not be fired!");
            }
        } catch (Throwable e) {
            Except.handle(e);
        }
    }

    private void registerSpongeListeners(Object... listeners) {
        for (Object obj : listeners) {
            if (null != obj) Sponge.getEventManager().registerListeners(Sponge7Application.container(), obj);
            else Hartshorn.log().warn("Attempted to register 'null' listener");
        }
    }

    private static void preparePacketGateListeners(PacketGate packetGate) {
        EventBus bus = Hartshorn.context().get(EventBus.class);
        Set<Class<? extends Packet>> adaptedPackets = HartshornUtils.emptySet();
        bus.getListenersToInvokers().forEach((k, v) -> v.forEach(
                eventWrapper -> {
                    if (Reflect.assignableFrom(
                            PacketEvent.class, eventWrapper.getEventType())) {
                        Class<? extends Packet> packet = eventWrapper.getMethod()
                                .getAnnotation(org.dockbox.hartshorn.server.minecraft.packets.annotations.Packet.class)
                                .value();

                        // Adapters post the event globally, so we only need to register it once.
                        // This also avoids double-posting of the same event.
                        if (!adaptedPackets.contains(packet)) {
                            Packet emptyPacket = Hartshorn.context().get(packet);
                            packetGate.registerListener(
                                    BootstrapListeners.getPacketGateAdapter(packet),
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
                Hartshorn.context().get(Players.class)
                        .getPlayer(connection.getPlayerUUID())
                        .present(player -> {
                            net.minecraft.network.Packet<?> nativePacket = packetEvent.getPacket();
                            Packet internalPacket = Hartshorn.context().get(packet, new NativePacketProperty<>(nativePacket));

                            PacketEvent<? extends Packet> event =
                                    new PacketEvent<>(internalPacket, player).post();
                            packetEvent.setCancelled(event.isCancelled());
                            if (event.isModified() && internalPacket instanceof NMSPacket)
                                packetEvent.setPacket(((NMSPacket<?>) internalPacket).getPacket());
                        });
            }
        };
    }

}
