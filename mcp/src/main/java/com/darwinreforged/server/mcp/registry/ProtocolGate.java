package com.darwinreforged.server.mcp.registry;

import com.darwinreforged.server.mcp.protocol.Protocol;

import java.util.Optional;
import java.util.UUID;

import eu.crushedpixel.sponge.packetgate.api.event.PacketEvent;
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListener;
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListener.ListenerPriority;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketConnection;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketGate;

public class ProtocolGate {

    private PacketGate gate;

    public static ProtocolGate provide(PacketGate gate) {
        ProtocolGate protGate = new ProtocolGate();
        protGate.gate = gate;
        return protGate;
    }

    public Optional<MCPPacketConnection> connectionByUniqueId(UUID uuid) {
        if (this.gate != null) {
            Optional<PacketConnection> connOpt = this.gate.connectionByUniqueId(uuid);
            return connOpt.map(MCPPacketConnection::from);
        } return Optional.empty();
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
