package com.darwinreforged.server.mcp.wrappers;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class MaterialWrapper extends Wrapper<Material> {

    public MaterialWrapper(MapColorWrapper color) {
        set(new Material(color.get()));
    }

        public boolean isLiquid() {
        return get().isLiquid();
    }

        public boolean isSolid() {
        return get().isSolid();
    }

        public boolean blocksLight() {
        return get().blocksLight();
    }

        public boolean blocksMovement() {
        return get().blocksMovement();
    }

        public boolean getCanBurn() {
        return get().getCanBurn();
    }

        public Material setReplaceable() {
        return get().setReplaceable();
    }

        public boolean isReplaceable() {
        return get().isReplaceable();
    }

        public boolean isOpaque() {
        return get().isOpaque();
    }

        public boolean isToolNotRequired() {
        return get().isToolNotRequired();
    }

        public EnumPushReaction getPushReaction() {
        return get().getPushReaction();
    }

        public MapColor getMaterialMapColor() {
        return get().getMaterialMapColor();
    }
}
