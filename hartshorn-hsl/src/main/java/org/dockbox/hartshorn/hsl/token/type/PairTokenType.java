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

import org.dockbox.hartshorn.hsl.token.DefaultTokenCharacter;
import org.dockbox.hartshorn.hsl.token.TokenCharacter;
import org.dockbox.hartshorn.hsl.token.TokenMetaData;
import org.dockbox.hartshorn.hsl.token.TokenPairList;

/**
 * Represents the different types of pair tokens that can be used in the HSL language. A pair token is a token that
 * represents the start or end of a pair of tokens, such as parentheses, braces, or brackets. These tokens are used to
 * create {@link TokenTypePair pairs} of tokens, which can be used to match the start and end of a block of code.
 *
 * @see TokenType
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public enum PairTokenType implements EnumTokenType {
    /**
     * '(' token, representing the start of a range of parameters.
     *
     * @see TokenPairList#parameters()
     */
    LEFT_PAREN(DefaultTokenCharacter.LEFT_PAREN, PairPosition.LEFT) {
        @Override
        public TokenType inverse() {
            return RIGHT_PAREN;
        }
    },
    /**
     * ')' token, representing the end of a range of parameters.
     *
     * @see TokenPairList#parameters()
     */
    RIGHT_PAREN(DefaultTokenCharacter.RIGHT_PAREN, PairPosition.RIGHT) {
        @Override
        public TokenType inverse() {
            return LEFT_PAREN;
        }
    },
    /**
     * '{' token, representing the start of a block of code.
     *
     * @see TokenPairList#block()
     */
    LEFT_BRACE(DefaultTokenCharacter.LEFT_BRACE, PairPosition.LEFT) {
        @Override
        public TokenType inverse() {
            return RIGHT_BRACE;
        }
    },
    /**
     * '}' token, representing the end of a block of code.
     *
     * @see TokenPairList#block()
     */
    RIGHT_BRACE(DefaultTokenCharacter.RIGHT_BRACE, PairPosition.RIGHT) {
        @Override
        public TokenType inverse() {
            return LEFT_BRACE;
        }
    },
    /**
     * '[' token, representing the start of an array.
     *
     * @see TokenPairList#array()
     */
    ARRAY_OPEN(DefaultTokenCharacter.ARRAY_OPEN, PairPosition.LEFT) {
        @Override
        public TokenType inverse() {
            return ARRAY_CLOSE;
        }
    },
    /**
     * ']' token, representing the end of an array.
     *
     * @see TokenPairList#array()
     */
    ARRAY_CLOSE(DefaultTokenCharacter.ARRAY_CLOSE, PairPosition.RIGHT) {
        @Override
        public TokenType inverse() {
            return ARRAY_OPEN;
        }
    },
    ;

    private final TokenMetaData metaData;
    private final PairPosition position;

    PairTokenType(TokenCharacter character, PairPosition position) {
        this.metaData = TokenMetaData.builder(this)
                .representation(String.valueOf(character.character()))
                .defaultLexeme(String.valueOf(character.character()))
                .characters(character)
                .build();
        this.position = position;
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }

    /**
     * Returns the position of this pair token. This is either {@link PairPosition#LEFT} or {@link PairPosition#RIGHT}
     * indicating that this token is the start or end of a pair of tokens.
     *
     * @return the position of this pair token
     */
    public PairPosition position() {
        return this.position;
    }

    /**
     * Returns the current token and its inverse as a {@link TokenTypePair pair}.
     *
     * @return the current token and its inverse as a pair
     */
    public TokenTypePair pair() {
        return switch(this.position) {
            case LEFT -> new TokenTypePair(this, this.inverse());
            case RIGHT -> new TokenTypePair(this.inverse(), this);
        };
    }

    /**
     * Returns the inverse of this token. This is the token that represents the end of the pair that this token
     * represents the start of, or vice versa.
     *
     * @return the inverse of this token
     */
    public abstract TokenType inverse();

    /**
     * Represents the position of a pair token. This is either the start or end of a pair of tokens.
     *
     * @since 0.6.0
     *
     * @see PairTokenType#position()
     *
     * @author Guus Lieben
     */
    public enum PairPosition {
        /**
         * Indicates that a token is on the left side of an expression or statement, and is thus the
         * start of a pair of tokens.
         */
        LEFT,
        /**
         * Indicates that a token is on the right side of an expression or statement, and is thus the
         * end of a pair of tokens.
         */
        RIGHT,
    }
}
