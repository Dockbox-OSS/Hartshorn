package org.dockbox.hartshorn.hsl.token;

public interface TokenCharacter {

    char character();

    boolean isDigit();

    boolean isAlpha();

    boolean isAlphaNumeric();
}
