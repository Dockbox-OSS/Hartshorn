package com.darwinreforged.server.forge.wrappers.forge;

import com.darwinreforged.server.forge.MCPWrapper;
import com.darwinreforged.server.forge.wrappers.raw.UUIDPacketConnection;
import com.darwinreforged.server.forge.protocol.Protocol.AbstractProtocol;

import java.util.UUID;

public class ForgeUUIDPacketConnection implements UUIDPacketConnection {
    @Override
    public void sendPacket(UUID uuid, AbstractProtocol<?> protocol) {
        MCPWrapper.getProtocol().flatMap(protocolGate ->
                protocolGate.getPacketGate()
                        .flatMap(packetGate -> packetGate.connectionByUniqueId(uuid)))
                .ifPresent(connection -> connection.sendPacket(protocol.get()));
    }
}
