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
import java.util.function.Predicate;

import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * A registry of all tokens and token types that may be used in an HSL file. This registry is used to
 * tokenize and parse the input given to an HSL runtime. Registries are immutable by default, though
 * may opt to be mutable if required.
 *
 * @see TokenCharacter
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public interface TokenRegistry {

    /**
     * Returns a collection of all characters that are known to the registry. This may be used by lexers
     * to determine whether a character is valid or not.
     *
     * @return a collection of all characters that are known to the registry
     */
    Set<TokenCharacter> characters();

    /**
     * Indicates whether the given character is a line separator. This is used by the lexer to determine
     * whether a line has ended or not, and the line number should be incremented.
     *
     * @param character the character to check
     * @return {@code true} if the character is a line separator, {@code false} otherwise
     */
    boolean isLineSeparator(TokenCharacter character);

    /**
     * Returns the {@link TokenCharacter} that is associated with the given character. If the character is
     * not known to the registry, a new empty character is returned. The new character may or may not be
     * added to the registry, depending on the implementation.
     *
     * @param character the character to get the token character for
     * @return the token character associated with the given character
     */
    TokenCharacter character(char character);

    /**
     * Returns the {@link TokenCharacterList} that is associated with the current registry. This list is
     * used to represent common characters used for literals.
     *
     * @return the token character list associated with the current registry
     *
     * @see TokenCharacterList
     */
    TokenCharacterList characterList();

    /**
     * Returns a collection of all token types that are known to the registry. This may be used by lexers
     * to determine whether a token is valid or not.
     *
     * @return a collection of all token types that are known to the registry
     */
    Set<TokenType> tokenTypes();

    /**
     * Returns a collection of all token types that match the given predicate. This may be used by lexers to
     * determine whether a token is valid or not.
     *
     * @param predicate the predicate to match
     * @return a collection of all token types that match the given predicate
     */
    Set<TokenType> tokenTypes(Predicate<TokenType> predicate);

    /**
     * Returns the {@link LiteralTokenList} that is associated with the current registry. This list is used
     * to determine the token types of literals.
     *
     * @return the literal token list associated with the current registry
     */
    LiteralTokenList literals();

    /**
     * Returns the {@link CommentTokenList} that is associated with the current registry. This list is used
     * to determine the token types and comment type of comments.
     *
     * @return the comment token list associated with the current registry
     */
    CommentTokenList comments();

    /**
     * Returns the {@link TokenPairList} that is associated with the current registry. This list is used to
     * determine the token types of opening and closing pairs for common constructs such as blocks and
     * lists.
     *
     * @return the token pair list associated with the current registry
     */
    TokenPairList tokenPairs();

    /**
     * Returns the {@link TokenGraph} that is associated with the current registry. This graph may be used by
     * lexers to determine the structure of the current registry.
     *
     * @return the token graph associated with the current registry
     */
    TokenGraph tokenGraph();

    /**
     * Returns the token type that indicates the termination of a statement. This may be used by parsers to
     * determine when a statement has ended.
     *
     * @return the token type that indicates the termination of a statement
     */
    TokenType statementEnd();
}
