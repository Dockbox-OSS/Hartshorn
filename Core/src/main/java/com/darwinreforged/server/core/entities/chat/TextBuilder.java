package com.darwinreforged.server.core.entities.chat;

import java.util.ArrayList;
import java.util.List;

public final class TextBuilder {
    private Text root;

    // The current text object. This will change when we append text for example
    private Text current;

    // The storage of the extra items
    private List<Text> extra = new ArrayList<>();

    private TextBuilder(Text root) {
        this.root = root;
        this.current = root;
    }


    public TextBuilder color(ChatColor color) {
        this.current.setColor(color);
        return this;
    }


    public TextBuilder italicize(boolean b) {
        this.current.setItalic(b);
        return this;
    }

    public TextBuilder bold(boolean b) {
        this.current.setBold(b);
        return this;
    }


    public TextBuilder underline(boolean b) {
        this.current.setUnderlined(b);
        return this;
    }

    public TextBuilder obfuscate(boolean b) {
        this.current.setObfuscated(b);
        return this;
    }

    public TextBuilder strikethrough(boolean b) {
        this.current.setStrikethrough(b);
        return this;
    }

    public TextBuilder clickEvent(ClickEvent clickEvent) {
        this.current.setClickEvent(clickEvent);
        return this;
    }

    public TextBuilder hoverEvent(HoverEvent hoverEvent) {
        this.current.setHoverEvent(hoverEvent);
        return this;
    }

    public TextBuilder append(String text) {
        // essentially this completes what ever object we were on. No turning back!
        return this.append(new Text(text));
    }

    public TextBuilder append(Text object) {
        if (root == null) {
            this.root = object;
            this.current = object;
        } else {
            this.extra.add(this.current = object);
        }
        return this;
    }


    public Text build() {
        // currently we're only adding the extras to the root.
        this.root.setExtra(extra);
        return this.root;
    }

    public static TextBuilder of(String text) {
        return new TextBuilder(new Text(text));
    }

    public static TextBuilder empty() {
        return new TextBuilder(null);
    }

}
