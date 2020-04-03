package com.darwinreforged.server.mcp.protocol;

import com.darwinreforged.server.mcp.entities.Entities.AbstractEntity;

import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.SPacketTimeUpdate;

import java.io.IOException;

import eu.crushedpixel.sponge.packetgate.api.event.PacketEvent;

public enum Protocol {

    CHANGE_GAME_STATE(SPacketChangeGameState.class),
    SPAWN_GLOBAL_ENTITY(SPacketSpawnGlobalEntity.class),
    TIME_UPDATE(SPacketTimeUpdate.class);

    public Class<? extends Packet<? extends INetHandler>> type;

    Protocol(Class<? extends Packet<? extends INetHandler>> clazz) {
        this.type = clazz;
    }

    public abstract static class AbstractProtocol<P extends Packet<? extends INetHandler>> {
        protected P packet;

        public AbstractProtocol() {
        }

        public AbstractProtocol(P p) {
            this.packet = p;
        }

        public P get() {
            return this.packet;
        }

        public boolean isEmpty() {
            return this.packet == null;
        }

        public void writePacketData(ProtocolBuffer buf) throws IOException {
            this.packet.writePacketData(buf.getPacketBuffer());
        }

        public void readPacketData(ProtocolBuffer buf) throws IOException {
            this.packet.readPacketData(buf.getPacketBuffer());
        }
    }

    public static class ChangeGameState extends AbstractProtocol<SPacketChangeGameState> {
        public ChangeGameState(int stateIn, float valueIn) {
            this.packet = new SPacketChangeGameState(stateIn, valueIn);
        }
    }

    public static class SpawnGlobalEntity extends AbstractProtocol<SPacketSpawnGlobalEntity> {
        public <T extends Entity> SpawnGlobalEntity(AbstractEntity<T> entity) {
            this.packet = new SPacketSpawnGlobalEntity(entity.get());
        }
    }

    public static class TimeUpdate extends AbstractProtocol<SPacketTimeUpdate> {
        public TimeUpdate(long totalWorldTimeIn, long worldTimeIn, boolean doDaylightCycle) {
            this.packet = new SPacketTimeUpdate(totalWorldTimeIn, worldTimeIn, doDaylightCycle);
        }

        public TimeUpdate(SPacketTimeUpdate sPacketTimeUpdate) {
            super(sPacketTimeUpdate);
        }

        public TimeUpdate(PacketEvent event) {
            if (event.getPacket() instanceof SPacketTimeUpdate)
                this.packet = (SPacketTimeUpdate) event.getPacket();
        }
    }
}
