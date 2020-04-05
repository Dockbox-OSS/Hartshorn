package com.darwinreforged.server.mcp.mixin.impl;

import com.darwinreforged.server.mcp.Mixins;
import com.darwinreforged.server.mcp.mixin.raw.UUIDPacketConnection;
import com.darwinreforged.server.mcp.protocol.Protocol.AbstractProtocol;

import java.util.UUID;

public class ForgeUUIDPacketConnection implements UUIDPacketConnection {
    @Override
    public void sendPacket(UUID uuid, AbstractProtocol<?> protocol) {
        Mixins.getProtocol().flatMap(protocolGate ->
                protocolGate.getPacketGate()
                        .flatMap(packetGate -> packetGate.connectionByUniqueId(uuid)))
                .ifPresent(connection -> connection.sendPacket(protocol.get()));
    }
}
