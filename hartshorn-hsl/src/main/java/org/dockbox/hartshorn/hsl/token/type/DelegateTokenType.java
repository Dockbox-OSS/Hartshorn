package org.dockbox.hartshorn.hsl.token.type;

import org.dockbox.hartshorn.hsl.token.TokenCharacter;

public interface DelegateTokenType extends TokenType {

    TokenType delegate();

    @Override
    default String representation() {
        return this.delegate().representation();
    }

    @Override
    default boolean keyword() {
        return this.delegate().keyword();
    }

    @Override
    default boolean standaloneStatement() {
        return this.delegate().standaloneStatement();
    }

    @Override
    default boolean reserved() {
        return this.delegate().reserved();
    }

    @Override
    default TokenType assignsWith() {
        return this.delegate().assignsWith();
    }

    @Override
    default String defaultLexeme() {
        return this.delegate().defaultLexeme();
    }

    @Override
    default TokenCharacter[] characters() {
        return this.delegate().characters();
    }
}
