package com.darwinreforged.server.core.types.chat;

import java.util.ArrayList;
import java.util.List;

/**
 The type Text builder.
 */
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


    /**
     Color text builder.

     @param color
     the color

     @return the text builder
     */
    public TextBuilder color(ChatColor color) {
        this.current.setColor(color);
        return this;
    }


    /**
     Italicize text builder.

     @param b
     the b

     @return the text builder
     */
    public TextBuilder italicize(boolean b) {
        this.current.setItalic(b);
        return this;
    }

    /**
     Bold text builder.

     @param b
     the b

     @return the text builder
     */
    public TextBuilder bold(boolean b) {
        this.current.setBold(b);
        return this;
    }


    /**
     Underline text builder.

     @param b
     the b

     @return the text builder
     */
    public TextBuilder underline(boolean b) {
        this.current.setUnderlined(b);
        return this;
    }

    /**
     Obfuscate text builder.

     @param b
     the b

     @return the text builder
     */
    public TextBuilder obfuscate(boolean b) {
        this.current.setObfuscated(b);
        return this;
    }

    /**
     Strikethrough text builder.

     @param b
     the b

     @return the text builder
     */
    public TextBuilder strikethrough(boolean b) {
        this.current.setStrikethrough(b);
        return this;
    }

    /**
     Click event text builder.

     @param clickEvent
     the click event

     @return the text builder
     */
    public TextBuilder clickEvent(ClickEvent clickEvent) {
        this.current.setClickEvent(clickEvent);
        return this;
    }

    /**
     Hover event text builder.

     @param hoverEvent
     the hover event

     @return the text builder
     */
    public TextBuilder hoverEvent(HoverEvent hoverEvent) {
        this.current.setHoverEvent(hoverEvent);
        return this;
    }

    /**
     Append text builder.

     @param text
     the text

     @return the text builder
     */
    public TextBuilder append(String text) {
        // essentially this completes what ever object we were on. No turning back!
        return this.append(new Text(text));
    }

    /**
     Append text builder.

     @param object
     the object

     @return the text builder
     */
    public TextBuilder append(Text object) {
        if (root == null) {
            this.root = object;
            this.current = object;
        } else {
            this.extra.add(this.current = object);
        }
        return this;
    }


    /**
     Build text.

     @return the text
     */
    public Text build() {
        // currently we're only adding the extras to the root.
        this.root.setExtra(extra);
        return this.root;
    }

    /**
     Of text builder.

     @param text
     the text

     @return the text builder
     */
    public static TextBuilder of(String text) {
        return new TextBuilder(new Text(text));
    }

    /**
     Empty text builder.

     @return the text builder
     */
    public static TextBuilder empty() {
        return new TextBuilder(null);
    }

}
