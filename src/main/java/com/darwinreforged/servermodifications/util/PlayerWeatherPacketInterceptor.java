package com.darwinreforged.servermodifications.util;

import eu.crushedpixel.sponge.packetgate.api.event.PacketEvent;
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListenerAdapter;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketConnection;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;

public class PlayerWeatherPacketInterceptor extends PacketListenerAdapter
{
    @Override
    public void onPacketWrite(PacketEvent event, PacketConnection connection) {
        if (!(event.getPacket() instanceof SPacketSpawnGlobalEntity)) return;

//        if (!Core.lightningPlayersContains(uuid)) {
//            event.setCancelled(false);
//            optionalPlayer.ifPresent(player -> {Core.sendMessage(player, "cancelled packet");});
//        }

//        SPacketSpawnGlobalEntity packet = (SPacketSpawnGlobalEntity) event.getPacket();
//        //buffer(128) -> initial buffer capacity, however, is automatically extended as necessary
//        PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer(128));
//
//        try {
//            packet.readPacketData(packetBuffer);
//            //packet.writePacketData(packetBuffer);
//        }
//        catch (IOException e) {
//            optionalPlayer.ifPresent(player -> {Core.sendMessage(player, "failed to read packet");});
//            return;
//        }
//
//        int EID = packetBuffer.readVarInt();
//        byte type = packetBuffer.readByte();
//
//        optionalPlayer.ifPresent(player -> {Core.sendMessage(player, "EID: " + EID + ", Type: " + type);});


    }

}
