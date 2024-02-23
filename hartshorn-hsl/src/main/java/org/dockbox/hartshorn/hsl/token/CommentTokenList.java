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

import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenTypePair;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.option.Option;

/**
 * Represents a list of comment tokens that can be used in the HSL language. A comment token is a token that is used to
 * indicate a comment in the source code. This list is used to resolve the type of comment token from an open token, and
 * optionally resolve the token pair from an open token to determine how a comment is closed.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public interface CommentTokenList {

    /**
     * Returns a map of comment types to token pairs. The key of the map is the type of comment, and the value is a
     * list of token pairs that represent the open and (optional) close tokens for that type of comment.
     *
     * @return a map of comment types to token pairs
     */
    MultiMap<CommentType, TokenTypePair> commentTypes();

    /**
     * Resolves the type of comment token from an open token. The open token is the token that indicates the start of a
     * comment. The result is an {@link Option} that contains the type of comment token, or is empty if the token is not
     * a comment token.
     *
     * @param tokenType the open token
     * @return the type of comment token, or empty if the token is not a comment token
     */
    Option<CommentType> resolveFromOpenToken(TokenType tokenType);

    /**
     * Resolves the token pair from an open token. The open token is the token that indicates the start of a comment. The
     * result is an {@link Option} that contains the token pair, or is empty if the token is not a comment token.
     *
     * @param tokenType the open token
     * @return the token pair, or empty if the token is not a comment token
     */
    Option<TokenTypePair> resolveTokenPairFromOpen(TokenType tokenType);

    /**
     * Represents the different types of comments that can be used in the HSL language. A comment can be a line comment
     * or a block comment.
     *
     * @since 0.6.0
     *
     * @author Guus Lieben
     */
    public enum CommentType {
        /**
         * A line comment, indicated by a single token that starts a comment and ends at the end of the line.
         */
        LINE,
        /**
         * A block comment, indicated by a pair of tokens that start and end a comment.
         */
        BLOCK,
    }
}
