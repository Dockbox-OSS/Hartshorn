package org.dockbox.hartshorn.hsl.token;

import org.dockbox.hartshorn.hsl.token.type.TokenType;

import java.util.Set;
import java.util.function.Predicate;

public interface TokenRegistry {

    Set<TokenCharacter> characters();

    boolean isNumberSeparator(TokenCharacter character);

    boolean isNumberDelimiter(TokenCharacter character);

    boolean isLineSeparator(TokenCharacter character);

    TokenCharacter character(char character);

    TokenCharacter nullCharacter();

    Set<TokenType> tokenTypes(Predicate<TokenType> predicate);

    Set<TokenType> literals();

    TokenPairList tokenPairs();
}
