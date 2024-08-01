/*
 * Copyright 2019-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.hsl.lexer;

import java.util.List;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenRegistry;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;

/**
 * Lexers are responsible for transforming a source string into a collection of {@link Token}s.
 * The lexer is configured with a {@link TokenRegistry} that defines the tokens that can be
 * recognized. The lexer will attempt to match the most accurate token for the current
 * character(s) in the source. If no match is found, an error is reported.
 *
 * <p>Lexers are stateful, and can only process one source at a time. The lexer will always
 * start at the beginning of the source, and will continue until the end of the source is
 * reached. The lexer will always end with a single {@link LiteralTokenType#EOF EndOfFile token}.
 *
 * <p>Lexers are not thread-safe. Each thread should have its own lexer instance. Depending
 * on the implementation, a lexer instance might be re-usable. This is not guaranteed.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public interface Lexer {

    /**
     * The source string that is being processed by this lexer. This value is never null.
     *
     * @return The source string.
     */
    String source();

    /**
     * The {@link TokenRegistry} that is used by this lexer. This registry defines the tokens
     * that can be recognized by this lexer. This value is never null.
     *
     * @return The token registry.
     */
    TokenRegistry tokenRegistry();

    /**
     * Transforms the configured source into valid {@link Token}s. If an invalid token is
     * encountered, an error is reported. When an error is reported, the lexer will attempt
     * to proceed to the next token, skipping the invalid token(s). The collection of
     * tokens will always end with a single {@link LiteralTokenType#EOF EndOfFile token}.
     *
     * @return The scanned tokens.
     */
    List<Token> scanTokens();

    /**
     * Returns the list of comments that were encountered during the scanning of the source.
     * This list is never null, but might be empty. Note that this list will always be empty
     * if {@link #scanTokens()} has not been invoked.
     *
     * @return The list of comments.
     */
    List<Comment> comments();
}
