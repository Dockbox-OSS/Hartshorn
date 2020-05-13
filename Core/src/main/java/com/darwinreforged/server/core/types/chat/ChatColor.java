package com.darwinreforged.server.core.types.chat;

public enum ChatColor {
    // Colors
    BLACK,
    DARK_BLUE,
    DARK_GREEN,
    DARK_AQUA,
    DARK_RED,
    DARK_PURPLE,
    GOLD,
    GRAY,
    DARK_GRAY,
    BLUE,
    GREEN,
    AQUA,
    RED,
    LIGHT_PURPLE,
    YELLOW,
    WHITE,
    // Styles
    OBFUSCATED(true),
    BOLD(true),
    STRIKETHROUGH(true),
    UNDERLINE(true),
    ITALIC(true),
    RESET(true);


    public static final char SECTION_SYMBOL = '\u00A7';

    private static final String CHARS_STRING = "0123456789abcdefklmnor";
    private static final char[] CHARS = CHARS_STRING.toCharArray();

    private boolean format;


    ChatColor(boolean format) {
        this.format = format;
    }

    ChatColor() {
        this(false);
    }

    public static boolean isValid(char c) {
        return CHARS_STRING.indexOf(Character.toLowerCase(c)) != -1;
    }

    public static ChatColor getByCharCode(char c) {
        return ChatColor.values()[CHARS_STRING.indexOf(Character.toLowerCase(c))];
    }


    public boolean isFormat() {
        return format;
    }

    public char charCode() {
        return CHARS[this.ordinal()];
    }


    public String toLegacyString() {
        return String.valueOf(new char[]{SECTION_SYMBOL, this.charCode()});
    }

    public String toJsonString() {
        return this.name().toLowerCase();
    }

    @Override
    public String toString() {
        return this.toLegacyString();
    }
}
