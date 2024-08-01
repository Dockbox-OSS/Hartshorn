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

package org.dockbox.hartshorn.hsl.token.type;

import org.dockbox.hartshorn.hsl.token.TokenCharacter;

/**
 * Represents the different types of tokens that can be used in the HSL language.
 *
 * @see TokenType
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public interface TokenType {

    /**
     * Gets the name of the {@link TokenType}. This is typically used to indicate the
     * name of the token in messages and logs. While not enforced, it is recommended
     * to use a unique name for each {@link TokenType}.
     *
     * @return The name of the token.
     */
    String tokenName();

    /**
     * Gets the standard representation of the {@link TokenType}.
     *
     * @return The representation of the token.
     */
    String representation();

    /**
     * Gets whether the {@link TokenType} represents a keyword.
     *
     * @return {@code true} if the token represents a keyword, or {@code false}.
     */
    boolean keyword();

    /**
     * Gets whether the {@link TokenType} can be used as a standalone statement. This
     * is typically used to indicate a token is not part of an expression statement,
     * but can optionally accept expressions when parsed.
     *
     * @return {@code true} if the token can be used as a standalone statement.
     */
    boolean standaloneStatement();

    /**
     * Gets whether the use of this {@link TokenType} is reserved. This is typically used
     * to indicate a token that is not yet supported, but is reserved for future use.
     *
     * @return {@code true} if the token is reserved.
     */
    boolean reserved();

    /**
     * Gets the {@link TokenType} which this {@link TokenType} assigns with. This is
     * typically used to indicate a token that can be used as an assignment operator.
     *
     * @return The {@link TokenType} which this {@link TokenType} assigns with.
     */
    TokenType assignsWith();

    /**
     * Gets the default lexeme of the {@link TokenType}. This is typically used to
     * indicate the abstract meaning of the token.
     *
     * @return The default lexeme of the token.
     */
    String defaultLexeme();

    /**
     * Gets the characters that represent the {@link TokenType}. This is typically used
     * by the lexer to identify the token in the source code.
     *
     * @return The characters that represent the token.
     */
    TokenCharacter[] characters();
}
