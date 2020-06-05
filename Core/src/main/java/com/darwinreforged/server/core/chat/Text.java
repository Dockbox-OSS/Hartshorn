package com.darwinreforged.server.core.chat;

public final class Text {

    private String text = "";
    private ClickEvent clickEvent;
    private HoverEvent hoverEvent;

    public static final char SECTION_SYMBOL = '\u00A7';
    public static final char NEW_LINE = '\n';

    public Text(String text) {
        this.text = text;
    }

    public Text setClickEvent(ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
        return this;
    }

    public Text setHoverEvent(HoverEvent hoverEvent) {
        this.hoverEvent = hoverEvent;
        return this;
    }

    public Text setText(String text) {
        this.text = text;
        return this;
    }

    public String toLegacy() {
        return this.text.replaceAll(Text.SECTION_SYMBOL + "", "&");
    }

    public Text append(Text text) {
        // TODO : Implement multi-text registrations to provide click/hover actions for partial messages
        this.text += text.toLegacy();
        return this;
    }

    public Text append(CharSequence text) {
        this.text += text;
        return this;
    }

    public Text append(char text) {
        this.text += text;
        return this;
    }

    public static Text of(Object... obj) {
        char[] convertedChars = "0123456789abcdefklmnor".toCharArray();
        Text text = new Text("");
        for (Object o : obj) {
            String toRepl = (o instanceof Text) ? ((Text) o).getText() : o.toString();
            for (char c : convertedChars) toRepl = toRepl.replaceAll("&" + c, Text.SECTION_SYMBOL + "" + c);
            text.append(toRepl);
        }
        return text;
    }

    public String getText() {
        return text;
    }

    public ClickEvent getClickEvent() {
        return clickEvent;
    }

    public HoverEvent getHoverEvent() {
        return hoverEvent;
    }

}
