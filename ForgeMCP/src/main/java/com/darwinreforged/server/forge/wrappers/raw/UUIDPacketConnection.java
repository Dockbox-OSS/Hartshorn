package com.darwinreforged.server.forge.wrappers.raw;

import com.darwinreforged.server.forge.protocol.Protocol.AbstractProtocol;

import java.util.UUID;

public interface UUIDPacketConnection {

    void sendPacket(UUID uuid, AbstractProtocol<?> protocol);

}
