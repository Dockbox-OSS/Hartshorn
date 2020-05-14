package com.darwinreforged.server.core.chat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 The type Text.
 */
public final class Text {

    private String text;
    private ChatColor color;
    private ClickEvent clickEvent;
    private HoverEvent hoverEvent;
    private boolean italic, bold, underlined, obfuscated, strikethrough;
    private List<Text> extra = new ArrayList<>();

    /**
     Instantiates a new Text.

     @param text
     the text
     */
    public Text(String text) {
        this.text = text;
    }

    /**
     Sets color.

     @param color
     the color

     @return the color
     */
    public Text setColor(ChatColor color) {
        this.color = color;
        return this;
    }

    /**
     Sets click event.

     @param clickEvent
     the click event

     @return the click event
     */
    public Text setClickEvent(ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
        return this;
    }

    /**
     Sets hover event.

     @param hoverEvent
     the hover event

     @return the hover event
     */
    public Text setHoverEvent(HoverEvent hoverEvent) {
        this.hoverEvent = hoverEvent;
        return this;
    }

    /**
     Sets italic.

     @param italic
     the italic

     @return the italic
     */
    public Text setItalic(boolean italic) {
        this.italic = italic;
        return this;
    }

    /**
     Sets bold.

     @param bold
     the bold

     @return the bold
     */
    public Text setBold(boolean bold) {
        this.bold = bold;
        return this;
    }

    /**
     Sets underlined.

     @param underlined
     the underlined

     @return the underlined
     */
    public Text setUnderlined(boolean underlined) {
        this.underlined = underlined;
        return this;
    }

    /**
     Sets strikethrough.

     @param strikethrough
     the strikethrough

     @return the strikethrough
     */
    public Text setStrikethrough(boolean strikethrough) {
        this.strikethrough = strikethrough;
        return this;
    }

    /**
     Sets obfuscated.

     @param obfuscated
     the obfuscated

     @return the obfuscated
     */
    public Text setObfuscated(boolean obfuscated) {
        this.obfuscated = obfuscated;
        return this;
    }

    /**
     Sets extra.

     @param extra
     the extra

     @return the extra
     */
    public Text setExtra(List<Text> extra) {
        this.extra.addAll(extra);
        return this;
    }

    /**
     Sets text.

     @param text
     the text

     @return the text
     */
    public Text setText(String text) {
        this.text = text;
        return this;
    }

    /**
     To json json object.

     @return the json object
     */
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

    /**
     From json text.

     @param object
     the object

     @return the text
     */
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

    /**
     To legacy string.

     @return the string
     */
    public String toLegacy() {
        return toLegacy(ChatColor.SECTION_SYMBOL);
    }

    /**
     Takes the current object and transforms it into a legacy string.

     @param charSubstitute
     - The substitute character to use if you do not want to use {@link ChatColor#SECTION_SYMBOL}

     @return A legacy string representation of a text object
     */
    public String toLegacy(char charSubstitute) {
        StringBuilder builder = new StringBuilder();

        if (this.getColor() != null) {
            builder.append(charSubstitute).append(this.getColor().charCode());
        }


        if (this.isObfuscated()) {
            builder.append(charSubstitute).append(ChatColor.OBFUSCATED.charCode());
        }

        if (this.isBold()) {
            builder.append(charSubstitute).append(ChatColor.BOLD.charCode());
        }


        if (this.isStrikethrough()) {
            builder.append(charSubstitute).append(ChatColor.STRIKETHROUGH.charCode());
        }


        if (this.isUnderlined()) {
            builder.append(charSubstitute).append(ChatColor.UNDERLINE.charCode());
        }

        if (this.isItalic()) {
            builder.append(charSubstitute).append(ChatColor.ITALIC.charCode());
        }

        if (this.getColor() == ChatColor.RESET) {
            builder.setLength(0);
            builder.append(charSubstitute).append(ChatColor.RESET.charCode());
        }

        if (this.getText() != null && !this.getText().isEmpty()) {
            builder.append(this.getText());
        }

        for (Text extra : this.getExtra()) {
            builder.append(extra.toLegacy(charSubstitute));
        }

        return builder.toString();
    }

    /**
     From legacy text.

     @param legacy
     the legacy text

     @return the text
     */
    public static Text of(String legacy) {
        return of(legacy, '&');
    }

    /**
     This function takes in a legacy text string and converts it into a {@link Text}.
     <p>
     Legacy text strings use the {@link ChatColor#SECTION_SYMBOL}. Many keyboards do not have this symbol however,
     which is probably why it was chosen. To get around this, it is common practice to substitute
     the symbol for another, then translate it later. Often '&' is used, but this can differ from person
     to person. In case the string does not have a {@link ChatColor#SECTION_SYMBOL}, the method also checks for the
     {@param characterSubstitute}

     @param legacy
     The text to make into an object
     @param characterSubstitute
     The character substitute

     @return A TextObject representing the legacy text.
     */
    public static Text of(String legacy, char characterSubstitute) {
        TextBuilder builder = TextBuilder.of("");
        Text currentObject = new Text("");
        StringBuilder text = new StringBuilder();

        for (int i = 0; i < legacy.length(); i++) {
            char c = legacy.charAt(i);

            if (c == ChatColor.SECTION_SYMBOL || c == characterSubstitute) {
                if ((i + 1) > legacy.length() - 1) {
                    // do nothing.
                    continue;
                }
                // peek at the next character.
                char peek = legacy.charAt(i + 1);

                if (ChatColor.isValid(peek)) {
                    i += 1; // if valid
                    if (text.length() > 0) {
                        // create a new text object
                        currentObject.setText(text.toString());

                        // append the current object.
                        builder.append(currentObject);

                        // reset the current object.
                        currentObject = new Text("");

                        // reset the buffer
                        text.setLength(0);
                    }

                    ChatColor color = ChatColor.getByCharCode(peek);

                    switch (color) {
                        case OBFUSCATED:
                            currentObject.setObfuscated(true);
                            break;
                        case BOLD:
                            currentObject.setBold(true);
                            break;
                        case STRIKETHROUGH:
                            currentObject.setStrikethrough(true);
                            break;
                        case ITALIC:
                            currentObject.setItalic(true);
                            break;
                        case UNDERLINE:
                            currentObject.setUnderlined(true);
                            break;
                        case RESET:
                            // Reset everything.
                            currentObject.setColor(ChatColor.WHITE);
                            currentObject.setObfuscated(false);
                            currentObject.setBold(false);
                            currentObject.setItalic(false);
                            currentObject.setUnderlined(false);
                            currentObject.setStrikethrough(false);
                            break;
                        default:
                            // emulate Minecraft's behavior of dropping styles that do not yet have an object.
                            currentObject = new Text("");
                            currentObject.setColor(color);
                            break;
                    }

                } else {
                    text.append(c);
                }
            } else {
                text.append(c);
            }
        }

        // whatever we were working on when the loop exited
        {
            currentObject.setText(text.toString());
            builder.append(currentObject);
        }

        return builder.build();
    }

    /**
     Gets text.

     @return the text
     */
    public String getText() {
        return text;
    }

    /**
     Gets color.

     @return the color
     */
    public ChatColor getColor() {
        return color;
    }

    /**
     Gets click event.

     @return the click event
     */
    public ClickEvent getClickEvent() {
        return clickEvent;
    }

    /**
     Gets hover event.

     @return the hover event
     */
    public HoverEvent getHoverEvent() {
        return hoverEvent;
    }

    /**
     Is italic boolean.

     @return the boolean
     */
    public boolean isItalic() {
        return italic;
    }

    /**
     Is bold boolean.

     @return the boolean
     */
    public boolean isBold() {
        return bold;
    }

    /**
     Is underlined boolean.

     @return the boolean
     */
    public boolean isUnderlined() {
        return underlined;
    }

    /**
     Is obfuscated boolean.

     @return the boolean
     */
    public boolean isObfuscated() {
        return obfuscated;
    }

    /**
     Is strikethrough boolean.

     @return the boolean
     */
    public boolean isStrikethrough() {
        return strikethrough;
    }

    /**
     Gets extra.

     @return the extra
     */
    public List<Text> getExtra() {
        return extra;
    }
}
