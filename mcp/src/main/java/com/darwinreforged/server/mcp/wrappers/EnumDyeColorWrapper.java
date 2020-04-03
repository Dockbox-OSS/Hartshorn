package com.darwinreforged.server.mcp.wrappers;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.text.TextFormatting;

public enum EnumDyeColorWrapper {

    WHITE,
    ORANGE,
    MAGENTA,
    LIGHT_BLUE,
    YELLOW,
    LIME,
    PINK,
    GRAY,
    SILVER,
    CYAN,
    PURPLE,
    BLUE,
    BROWN,
    GREEN,
    RED,
    BLACK;

    private EnumDyeColor color;

    EnumDyeColorWrapper() {
        this.color = EnumDyeColor.valueOf(this.toString());
    }

    public EnumDyeColor get() {
        return this.color;
    }
}
