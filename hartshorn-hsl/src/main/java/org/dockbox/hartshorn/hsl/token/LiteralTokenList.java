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

package org.dockbox.hartshorn.hsl.token;

import java.util.Set;

import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * Represents a list of literal tokens that are used in the HSL language. This list is used to define the different types
 * of literal tokens so that they can be recognized by the lexer and parser.
 *
 * @since 0.6.0
 *
 * @see TokenType
 *
 * @author Guus Lieben
 */
public interface LiteralTokenList {

    /**
     * Returns a set of all literal token types that are used in the HSL language.
     *
     * @return a set of literal token types
     */
    Set<TokenType> literals();

    /**
     * Returns the literal token type that represents the end of a file.
     *
     * @return the end of file token type
     */
    TokenType eof();

    /**
     * Returns the literal token type that represents an identifier literal.
     *
     * @return the identifier literal token type
     */
    TokenType identifier();

    /**
     * Returns the literal token type that represents a string literal.
     *
     * @return the string literal token type
     */
    TokenType string();

    /**
     * Returns the literal token type that represents a character literal.
     *
     * @return the character literal token type
     */
    TokenType character();

    /**
     * Returns the literal token type that represents a number literal.
     *
     * @return the number literal token type
     */
    TokenType number();

    /**
     * Returns the literal token type that represents a null literal.
     *
     * @return the null literal token type
     */
    TokenType nullLiteral();
}
