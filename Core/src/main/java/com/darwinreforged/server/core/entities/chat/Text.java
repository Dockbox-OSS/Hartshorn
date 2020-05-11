package com.darwinreforged.server.core.entities.chat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public final class Text {

    private String text;
    private ChatColor color;
    private ClickEvent clickEvent;
    private HoverEvent hoverEvent;
    private boolean italic, bold, underlined, obfuscated, strikethrough;
    private List<Text> extra = new ArrayList<>();

    public Text(String text) {
        this.text = text;
    }

    public Text setColor(ChatColor color) {
        this.color = color;
        return this;
    }

    public Text setClickEvent(ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
        return this;
    }

    public Text setHoverEvent(HoverEvent hoverEvent) {
        this.hoverEvent = hoverEvent;
        return this;
    }

    public Text setItalic(boolean italic) {
        this.italic = italic;
        return this;
    }

    public Text setBold(boolean bold) {
        this.bold = bold;
        return this;
    }

    public Text setUnderlined(boolean underlined) {
        this.underlined = underlined;
        return this;
    }

    public Text setStrikethrough(boolean strikethrough) {
        this.strikethrough = strikethrough;
        return this;
    }

    public Text setObfuscated(boolean obfuscated) {
        this.obfuscated = obfuscated;
        return this;
    }

    public Text setExtra(List<Text> extra) {
        this.extra.addAll(extra);
        return this;
    }

    public Text setText(String text) {
        this.text = text;
        return this;
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();

        object.addProperty("text", text);

        if (color != null)
            object.addProperty("color", color.toJsonString());
        if (clickEvent != null)
            object.add("clickEvent", clickEvent.toJson());
        if (hoverEvent != null)
            object.add("hoverEvent", hoverEvent.toJson());
        if (italic)
            object.addProperty("italic", true);
        if (bold)
            object.addProperty("bold", true);
        if (underlined)
            object.addProperty("underlined", true);
        if (obfuscated)
            object.addProperty("obfuscated", true);
        if (strikethrough)
            object.addProperty("strikethrough", true);

        if (!extra.isEmpty()) {
            JsonArray array = new JsonArray();

            for (Text ex : extra) {
                array.add(ex.toJson());
            }

            object.add("extra", array);
        }
        return object;
    }

    public static Text fromJson(JsonObject object) {
        if (object.has("text")) {
            Text o = new Text(object.get("text").getAsString());

            if (object.has("clickEvent")) {
                o.setClickEvent(ClickEvent.fromJson(object.get("clickEvent").getAsJsonObject()));
            }

            if (object.has("hoverEvent")) {
                o.setHoverEvent(HoverEvent.fromJson(object.get("hoverEvent").getAsJsonObject()));
            }

            if (object.has("color")) {
                o.setColor(ChatColor.valueOf(object.get("color").getAsString().toUpperCase()));
            }

            if (object.has("obfuscated"))
                o.setObfuscated(object.get("obfuscated").getAsBoolean());
            if (object.has("italic"))
                o.setItalic(object.get("italic").getAsBoolean());
            if (object.has("bold"))
                o.setBold(object.get("bold").getAsBoolean());
            if (object.has("underlined"))
                o.setUnderlined(object.get("underlined").getAsBoolean());

            if (object.has("extra")) {
                for (JsonElement extra : object.getAsJsonArray("extra")) {
                    if (extra.isJsonObject()) {
                        Text e = Text.fromJson(extra.getAsJsonObject());
                        if (e != null) {
                            o.extra.add(e);
                        }
                    }
                }
            }

            return o;
        }

        // invalid object
        return null;
    }

    public String getText() {
        return text;
    }

    public ChatColor getColor() {
        return color;
    }

    public ClickEvent getClickEvent() {
        return clickEvent;
    }

    public HoverEvent getHoverEvent() {
        return hoverEvent;
    }

    public boolean isItalic() {
        return italic;
    }

    public boolean isBold() {
        return bold;
    }

    public boolean isUnderlined() {
        return underlined;
    }

    public boolean isObfuscated() {
        return obfuscated;
    }

    public boolean isStrikethrough() {
        return strikethrough;
    }

    public List<Text> getExtra() {
        return extra;
    }
}
