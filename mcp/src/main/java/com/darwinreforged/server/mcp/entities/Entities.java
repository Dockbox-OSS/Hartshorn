package com.darwinreforged.server.mcp.entities;

import com.darwinreforged.server.mcp.reference.ForgeWorld;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;

public class Entities {

    public static abstract class AbstractEntity<T extends Entity> {
        protected T t;
        public T get() {
            return t;
        }
    }

    public static class LightningBolt extends AbstractEntity<EntityLightningBolt> {

        public LightningBolt(ForgeWorld forgeWorld, double x, double y, double z, boolean effectOnlyIn) {
            this.t = new EntityLightningBolt(forgeWorld.getWorld(), x, y, z, effectOnlyIn);
        }

    }

}
