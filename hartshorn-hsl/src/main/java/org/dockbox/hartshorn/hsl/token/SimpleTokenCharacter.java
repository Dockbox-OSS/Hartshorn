package org.dockbox.hartshorn.hsl.token;

public interface SimpleTokenCharacter extends TokenCharacter {

    static SimpleTokenCharacter of(char character, boolean standalone) {
        return new SimpleTokenCharacter() {

            @Override
            public char character() {
                return character;
            }

            @Override
            public boolean isStandaloneCharacter() {
                return standalone;
            }
        };
    }

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
