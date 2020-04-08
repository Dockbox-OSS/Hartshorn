package com.darwinreforged.server.mcp;

import com.darwinreforged.server.mcp.wrappers.forge.ForgeUUIDPacketConnection;
import com.darwinreforged.server.mcp.wrappers.raw.UUIDPacketConnection;
import com.darwinreforged.server.mcp.protocol.ProtocolGate;

import java.util.Optional;

import dagger.Module;
import dagger.Provides;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketGate;

@Module
public class MCPWrapper {

    private static ProtocolGate protocolGate;

    @Provides
    public static UUIDPacketConnection getUUIDPacketConnection() {
        return new ForgeUUIDPacketConnection();
    }

    public static void provide(PacketGate packetGate) {
        if (packetGate != null) MCPWrapper.protocolGate = ProtocolGate.provide(packetGate);
    }

    public static Optional<ProtocolGate> getProtocol() {
        return Optional.ofNullable(MCPWrapper.protocolGate);
    }

}
