package org.dockbox.hartshorn.hsl.token;

import java.util.Set;

import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

public class DefaultLiteralTokenList implements LiteralTokenList {

    public static final DefaultLiteralTokenList INSTANCE = new DefaultLiteralTokenList();

    @Override
    public Set<TokenType> literals() {
        return Set.of(LiteralTokenType.values());
    }

    @Override
    public TokenType eof() {
        return LiteralTokenType.EOF;
    }

    @Override
    public TokenType identifier() {
        return LiteralTokenType.IDENTIFIER;
    }

    @Override
    public TokenType string() {
        return LiteralTokenType.STRING;
    }

    @Override
    public TokenType character() {
        return LiteralTokenType.CHAR;
    }

    @Override
    public TokenType number() {
        return LiteralTokenType.NUMBER;
    }

    @Override
    public TokenType nullLiteral() {
        return LiteralTokenType.NULL;
    }
}
