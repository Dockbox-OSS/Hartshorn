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

import java.util.LinkedList;
import java.util.List;

import org.dockbox.hartshorn.hsl.token.type.SimpleTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * Utility-class to easily build new {@link TokenMetaData} instances.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class TokenMetaDataBuilder extends SimpleTokenType.Builder<TokenMetaDataBuilder> {

    private final TokenType type;

    TokenMetaDataBuilder(TokenType type) {
        this.type = type;
        this.representation(type.tokenName());
    }

    /**
     * Combines multiple token types into a single token type. This merges the
     * representations and characters of the given types.
     *
     * @param types the types to combine
     * @return the builder
     */
    public TokenMetaDataBuilder combines(TokenType... types) {
        StringBuilder combined = new StringBuilder();
        List<TokenCharacter> tokenCharacters = new LinkedList<>();
        boolean inheritCharacters = true;
        for(TokenType type : types) {
            combined.append(type.representation());
            if(type.characters().length > 0) {
                tokenCharacters.addAll(List.of(type.characters()));
            }
            else {
                inheritCharacters = false;
            }
        }
        this.representation(combined.toString());
        if (inheritCharacters) {
            this.characters(tokenCharacters.toArray(TokenCharacter[]::new));
        }
        return this;
    }

    /**
     * Repeats a token type. This is a shortcut for combining a type with itself.
     *
     * @param type the type to repeat
     * @return the builder
     */
    public TokenMetaDataBuilder repeats(TokenType type) {
        return this.combines(type, type);
    }

    /**
     * Repeats multiple characters. This merges the characters to determine the
     * representation, and uses the given characters as the characters of the new
     * token type.
     *
     * @param characters the characters to repeat
     * @return the builder
     */
    public TokenMetaDataBuilder combines(TokenCharacter... characters) {
        StringBuilder combined = new StringBuilder();
        for (TokenCharacter tokenCharacter : characters) {
            combined.append(tokenCharacter.character());
        }
        this.representation(combined.toString());
        this.characters(characters);
        return this;
    }

    /**
     * Repeats a character. This is a shortcut for combining a character with itself.
     *
     * @param type the type to repeat
     * @return the builder
     */
    public TokenMetaDataBuilder repeats(TokenCharacter type) {
        return this.combines(type, type);
    }

    @Override
    public TokenMetaData build() {
        TokenType tokenType = super.build();
        return new TokenMetaData(this.type, tokenType);
    }
}
