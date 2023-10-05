package org.dockbox.hartshorn.hsl.token;

import java.util.Set;

import org.dockbox.hartshorn.hsl.token.type.TokenType;

public interface LiteralTokenList {

    Set<TokenType> literals();

    TokenType eof();

    TokenType identifier();

    TokenType string();

    TokenType character();

    TokenType number();

    TokenType nullLiteral();

}
