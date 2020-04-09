package com.darwinreforged.server.forge.protocol;

import java.util.Optional;

import eu.crushedpixel.sponge.packetgate.api.event.PacketEvent;
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListener;
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListener.ListenerPriority;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketGate;

public class ProtocolGate {

    private PacketGate gate;

    public static ProtocolGate provide(PacketGate gate) {
        ProtocolGate protGate = new ProtocolGate();
        protGate.gate = gate;
        return protGate;
    }

    public Optional<PacketGate> getPacketGate() {
        return Optional.ofNullable(gate);
    }

    public void registerListener(PacketListener packetListener, ListenerPriority priority, Protocol protocol) {
        if (this.gate != null) {
            this.gate.registerListener(packetListener, priority, protocol.type);
        }
    }

    @SuppressWarnings("rawtypes")
    public static void updateEvent(PacketEvent event, Protocol.AbstractProtocol protocol) {
        event.setPacket(protocol.get());
    }

}
