package org.dockbox.hartshorn.hsl.token.type;

import org.dockbox.hartshorn.hsl.token.TokenCharacter;

public interface TokenType {

    String tokenName();

    String representation();

    boolean keyword();

    boolean standaloneStatement();

    boolean reserved();

    TokenType assignsWith();

    String defaultLexeme();

    TokenCharacter[] characters();
}
