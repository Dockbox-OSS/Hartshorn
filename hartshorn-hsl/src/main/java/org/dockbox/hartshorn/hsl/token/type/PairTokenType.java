/*
 * Copyright 2019-2023 the original author or authors.
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

public enum PairTokenType implements EnumTokenType {
    LEFT_PAREN(DefaultTokenCharacter.LEFT_PAREN, PairPosition.LEFT) {
        @Override
        public TokenType inverse() {
            return RIGHT_PAREN;
        }
    },
    RIGHT_PAREN(DefaultTokenCharacter.RIGHT_PAREN, PairPosition.RIGHT) {
        @Override
        public TokenType inverse() {
            return LEFT_PAREN;
        }
    },
    LEFT_BRACE(DefaultTokenCharacter.LEFT_BRACE, PairPosition.LEFT) {
        @Override
        public TokenType inverse() {
            return RIGHT_BRACE;
        }
    },
    RIGHT_BRACE(DefaultTokenCharacter.RIGHT_BRACE, PairPosition.RIGHT) {
        @Override
        public TokenType inverse() {
            return LEFT_BRACE;
        }
    },
    ARRAY_OPEN(DefaultTokenCharacter.ARRAY_OPEN, PairPosition.LEFT) {
        @Override
        public TokenType inverse() {
            return ARRAY_CLOSE;
        }
    },
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
                .ok();
        this.position = position;
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }

    public PairPosition position() {
        return this.position;
    }

    public TokenTypePair pair() {
        return new TokenTypePair(this, this.inverse());
    }

    public abstract TokenType inverse();

    public enum PairPosition {
        LEFT,
        RIGHT,
        ;
    }
}
