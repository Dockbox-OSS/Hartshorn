package com.darwinreforged.server.core.types.chat;

/**
 The enum Chat color.
 */
public enum ChatColor {
    /**
     Black chat color.
     */
// Colors
    BLACK,
    /**
     Dark blue chat color.
     */
    DARK_BLUE,
    /**
     Dark green chat color.
     */
    DARK_GREEN,
    /**
     Dark aqua chat color.
     */
    DARK_AQUA,
    /**
     Dark red chat color.
     */
    DARK_RED,
    /**
     Dark purple chat color.
     */
    DARK_PURPLE,
    /**
     Gold chat color.
     */
    GOLD,
    /**
     Gray chat color.
     */
    GRAY,
    /**
     Dark gray chat color.
     */
    DARK_GRAY,
    /**
     Blue chat color.
     */
    BLUE,
    /**
     Green chat color.
     */
    GREEN,
    /**
     Aqua chat color.
     */
    AQUA,
    /**
     Red chat color.
     */
    RED,
    /**
     Light purple chat color.
     */
    LIGHT_PURPLE,
    /**
     Yellow chat color.
     */
    YELLOW,
    /**
     White chat color.
     */
    WHITE,
    /**
     Obfuscated chat color.
     */
// Styles
    OBFUSCATED(true),
    /**
     Bold chat color.
     */
    BOLD(true),
    /**
     Strikethrough chat color.
     */
    STRIKETHROUGH(true),
    /**
     Underline chat color.
     */
    UNDERLINE(true),
    /**
     Italic chat color.
     */
    ITALIC(true),
    /**
     Reset chat color.
     */
    RESET(true);


    /**
     The constant SECTION_SYMBOL.
     */
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

    /**
     Is valid boolean.

     @param c
     the c

     @return the boolean
     */
    public static boolean isValid(char c) {
        return CHARS_STRING.indexOf(Character.toLowerCase(c)) != -1;
    }

    /**
     Gets by char code.

     @param c
     the c

     @return the by char code
     */
    public static ChatColor getByCharCode(char c) {
        return ChatColor.values()[CHARS_STRING.indexOf(Character.toLowerCase(c))];
    }


    /**
     Is format boolean.

     @return the boolean
     */
    public boolean isFormat() {
        return format;
    }

    /**
     Char code char.

     @return the char
     */
    public char charCode() {
        return CHARS[this.ordinal()];
    }


    /**
     To legacy string string.

     @return the string
     */
    public String toLegacyString() {
        return String.valueOf(new char[]{SECTION_SYMBOL, this.charCode()});
    }

    /**
     To json string string.

     @return the string
     */
    public String toJsonString() {
        return this.name().toLowerCase();
    }

    @Override
    public String toString() {
        return this.toLegacyString();
    }
}
