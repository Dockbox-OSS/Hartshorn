package org.dockbox.hartshorn.hsl.lexer;

import java.util.List;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;

public interface Lexer {

    /**
     * Transforms the configured source into valid {@link Token}s. If an invalid token is
     * encountered, an error is reported. When an error is reported, the lexer will attempt
     * to proceed to the next token, skipping the invalid token(s). The collection of
     * tokens will always end with a single {@link LiteralTokenType#EOF EndOfFile token}.
     *
     * @return The scanned tokens.
     */
    List<Token> scanTokens();

    List<Comment> comments();
}
