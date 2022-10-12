package org.dockbox.hartshorn.hsl.parser;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;

public interface TokenStepValidator {

    Token expect(TokenType type);

    Token expect(TokenType type, String what);

    Token expectBefore(TokenType type, String before);

    Token expectAfter(TokenType type, TokenType after);

    Token expectAfter(TokenType type, String after);

    Token expectAround(TokenType type, String where, String position);

}
