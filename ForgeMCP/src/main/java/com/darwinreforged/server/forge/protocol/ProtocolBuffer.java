package com.darwinreforged.server.forge.protocol;

import net.minecraft.network.PacketBuffer;

import io.netty.buffer.ByteBuf;

public class ProtocolBuffer {

    private final PacketBuffer packetBuffer;

    public ProtocolBuffer(ByteBuf wrapped) {
        this.packetBuffer = new PacketBuffer(wrapped);
    }

    public long readLong() {
        return packetBuffer.readLong();
    }

    public PacketBuffer getPacketBuffer() {
        return this.packetBuffer;
    }

}
