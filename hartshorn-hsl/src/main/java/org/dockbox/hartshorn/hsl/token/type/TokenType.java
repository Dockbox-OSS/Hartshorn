package org.dockbox.hartshorn.hsl.token.type;

public interface TokenType {

    String tokenName();

    String representation();

    boolean keyword();

    boolean standaloneStatement();

    boolean reserved();

    TokenType assignsWith();

    String defaultLexeme();
}
