package com.darwinreforged.server.core.chat;

/**
 Legacy text is a conversion util which will convert a "legacy" chat string into a TextObject,
 and a TextObject into a legacy string
 */
public final class LegacyText {

    /**
     From legacy text.

     @param legacyText
     the legacy text

     @return the text
     */
    public static Text fromLegacy(String legacyText) {
        return LegacyText.fromLegacy(legacyText, '&');
    }

    /**
     This function takes in a legacy text string and converts it into a {@link Text}.
     <p>
     Legacy text strings use the {@link ChatColor#SECTION_SYMBOL}. Many keyboards do not have this symbol however,
     which is probably why it was chosen. To get around this, it is common practice to substitute
     the symbol for another, then translate it later. Often '&' is used, but this can differ from person
     to person. In case the string does not have a {@link ChatColor#SECTION_SYMBOL}, the method also checks for the
     {@param characterSubstitute}

     @param legacyText
     The text to make into an object
     @param characterSubstitute
     The character substitute

     @return A TextObject representing the legacy text.
     */
    public static Text fromLegacy(String legacyText, char characterSubstitute) {
        TextBuilder builder = TextBuilder.of("");
        Text currentObject = new Text("");
        StringBuilder text = new StringBuilder();

        for (int i = 0; i < legacyText.length(); i++) {
            char c = legacyText.charAt(i);

            if (c == ChatColor.SECTION_SYMBOL || c == characterSubstitute) {
                if ((i + 1) > legacyText.length() - 1) {
                    // do nothing.
                    continue;
                }
                // peek at the next character.
                char peek = legacyText.charAt(i + 1);

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
     To legacy string.

     @param object
     the object

     @return the string
     */
    public static String toLegacy(Text object) {
        return LegacyText.toLegacy(object, ChatColor.SECTION_SYMBOL);
    }


    /**
     Takes an {@link Text} and transforms it into a legacy string.

     @param text
     - The {@link Text} to transform to.
     @param charSubstitute
     - The substitute character to use if you do not want to use {@link ChatColor#SECTION_SYMBOL}

     @return A legacy string representation of a text object
     */
    public static String toLegacy(Text text, char charSubstitute) {
        StringBuilder builder = new StringBuilder();

        if (text.getColor() != null) {
            builder.append(charSubstitute).append(text.getColor().charCode());
        }


        if (text.isObfuscated()) {
            builder.append(charSubstitute).append(ChatColor.OBFUSCATED.charCode());
        }

        if (text.isBold()) {
            builder.append(charSubstitute).append(ChatColor.BOLD.charCode());
        }


        if (text.isStrikethrough()) {
            builder.append(charSubstitute).append(ChatColor.STRIKETHROUGH.charCode());
        }


        if (text.isUnderlined()) {
            builder.append(charSubstitute).append(ChatColor.UNDERLINE.charCode());
        }

        if (text.isItalic()) {
            builder.append(charSubstitute).append(ChatColor.ITALIC.charCode());
        }

        if (text.getColor() == ChatColor.RESET) {
            builder.setLength(0);
            builder.append(charSubstitute).append(ChatColor.RESET.charCode());
        }

        if (text.getText() != null && !text.getText().isEmpty()) {
            builder.append(text.getText());
        }

        for (Text extra : text.getExtra()) {
            builder.append(LegacyText.toLegacy(extra, charSubstitute));
        }

        return builder.toString();
    }


}
