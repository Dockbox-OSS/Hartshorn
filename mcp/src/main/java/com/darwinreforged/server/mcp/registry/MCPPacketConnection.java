package com.darwinreforged.server.mcp.registry;

import com.darwinreforged.server.mcp.protocol.Protocol.AbstractProtocol;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

import eu.crushedpixel.sponge.packetgate.api.registry.PacketConnection;
import io.netty.channel.Channel;

public class MCPPacketConnection extends PacketConnection {
    public MCPPacketConnection(org.slf4j.Logger logger, Channel channel) {
        super(logger, channel);
    }

    public static MCPPacketConnection from(PacketConnection connection) {
        return (MCPPacketConnection) connection;
    }

    public <I extends Packet<? extends INetHandler>> void sendPacket(AbstractProtocol<I> protocol) {
        this.sendPacket(protocol.get());
    }
}
