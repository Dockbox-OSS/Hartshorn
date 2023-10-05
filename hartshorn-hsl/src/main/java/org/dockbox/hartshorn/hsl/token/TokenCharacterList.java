package org.dockbox.hartshorn.hsl.token;

public interface TokenCharacterList {

    TokenCharacter nullCharacter();

    // Open/close quote character for strings
    TokenCharacter quoteCharacter();

    // Open/close quote character for characters
    TokenCharacter charCharacter();

    // Separator for numbers
    TokenCharacter numberSeparator();

    TokenCharacter numberDelimiter();

}
