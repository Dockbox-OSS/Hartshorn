package com.darwinreforged.server.mcp.wrappers;

import net.minecraft.block.material.MapColor;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MapColorWrapper extends Wrapper<MapColor> {

    public MapColorWrapper(MapColor color) {
        set(color);
    }

    @SideOnly(Side.CLIENT)
    public int getMapColor(int index) {
        return get().getMapColor(index);
    }

    public static MapColorWrapper getBlockColor(EnumDyeColorWrapper dyeColorIn) {
        return new MapColorWrapper(MapColor.getBlockColor(dyeColorIn.get()));
    }
}
