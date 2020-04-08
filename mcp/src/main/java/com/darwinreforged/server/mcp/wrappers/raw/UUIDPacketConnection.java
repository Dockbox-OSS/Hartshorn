package com.darwinreforged.server.mcp.wrappers.raw;

import com.darwinreforged.server.mcp.protocol.Protocol.AbstractProtocol;

import java.util.UUID;

public interface UUIDPacketConnection {

    void sendPacket(UUID uuid, AbstractProtocol<?> protocol);

}
