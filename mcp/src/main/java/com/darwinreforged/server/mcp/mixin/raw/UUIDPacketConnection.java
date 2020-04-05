package com.darwinreforged.server.mcp.mixin.raw;

import com.darwinreforged.server.mcp.mixin.MixinInterface;
import com.darwinreforged.server.mcp.protocol.Protocol.AbstractProtocol;

import java.util.UUID;

public interface UUIDPacketConnection extends MixinInterface {

    void sendPacket(UUID uuid, AbstractProtocol<?> protocol);

}
