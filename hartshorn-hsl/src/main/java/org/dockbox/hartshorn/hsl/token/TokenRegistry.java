package org.dockbox.hartshorn.hsl.token;

import java.util.Set;
import java.util.function.Predicate;

import org.dockbox.hartshorn.hsl.token.type.TokenType;

public interface TokenRegistry {

    Set<TokenCharacter> characters();

    boolean isLineSeparator(TokenCharacter character);

    TokenCharacter character(char character);

    TokenCharacterList characterList();

    Set<TokenType> tokenTypes();

    Set<TokenType> tokenTypes(Predicate<TokenType> predicate);

    LiteralTokenList literals();

    CommentTokenList comments();

    TokenPairList tokenPairs();

    TokenGraph tokenGraph();
}
