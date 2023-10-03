package org.dockbox.hartshorn.hsl.token;

@FunctionalInterface
public interface SimpleTokenCharacter extends TokenCharacter {

    @Override
    default boolean isDigit() {
        char character = this.character();
        return character >= '0' && character <= '9';
    }

    @Override
    default boolean isAlpha() {
        char character = this.character();
        return (character >= 'a' && character <= 'z') ||
                (character >= 'A' && character <= 'Z') ||
                character == '_';
    }

    @Override
    default boolean isAlphaNumeric() {
        return isAlpha() || isDigit();
    }
}
