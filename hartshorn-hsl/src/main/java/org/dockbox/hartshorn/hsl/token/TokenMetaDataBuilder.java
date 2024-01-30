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

import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * Utility-class to easily build new {@link TokenMetaData} instances.
 *
 * @author Guus Lieben
 * @since 0.4.12
 */
public class TokenMetaDataBuilder {

    private final TokenType type;
    private String representation;
    private boolean keyword;
    private boolean standaloneStatement;
    private boolean reserved;
    private TokenType assignsWith;
    private String defaultLexeme;
    private TokenCharacter[] characters = new TokenCharacter[0];

    TokenMetaDataBuilder(TokenType type) {
        this.type = type;
        this.representation = type.tokenName();
    }

    public TokenMetaDataBuilder representation(String representation) {
        this.representation = representation;
        return this;
    }

    public TokenMetaDataBuilder combines(TokenType... types) {
        StringBuilder combined = new StringBuilder();
        List<TokenCharacter> characters = new LinkedList<>();
        boolean inheritCharacters = true;
        for(TokenType type : types) {
            combined.append(type.representation());
            if(type.characters().length > 0) {
                characters.addAll(List.of(type.characters()));
            }
            else {
                inheritCharacters = false;
            }
        }
        this.representation = combined.toString();
        if (inheritCharacters) {
            this.characters = characters.toArray(TokenCharacter[]::new);
        }
        return this;
    }

    public TokenMetaDataBuilder repeats(TokenType type) {
        return this.combines(type, type);
    }

    public TokenMetaDataBuilder combines(TokenCharacter... characters) {
        StringBuilder combined = new StringBuilder();
        for (TokenCharacter type : characters) {
            combined.append(type.character());
        }
        this.representation = combined.toString();
        this.characters = characters;
        return this;
    }

    public TokenMetaDataBuilder repeats(TokenCharacter type) {
        return this.combines(type, type);
    }

    public TokenMetaDataBuilder keyword(boolean keyword) {
        this.keyword = keyword;
        return this;
    }

    public TokenMetaDataBuilder standaloneStatement(boolean standaloneStatement) {
        this.standaloneStatement = standaloneStatement;
        return this;
    }

    public TokenMetaDataBuilder reserved(boolean reserved) {
        this.reserved = reserved;
        return this;
    }

    public TokenMetaDataBuilder assignsWith(TokenType assignsWith) {
        this.assignsWith = assignsWith;
        return this;
    }

    public TokenMetaDataBuilder defaultLexeme(String defaultLexeme) {
        this.defaultLexeme = defaultLexeme;
        return this;
    }

    public TokenMetaDataBuilder characters(TokenCharacter... characters) {
        this.characters = characters;
        if (this.representation == null) {
            StringBuilder builder = new StringBuilder();
            for (TokenCharacter character : characters) {
                builder.append(character.character());
            }
            this.representation = builder.toString();
        }
        return this;
    }

    public TokenMetaData ok() {
        return new TokenMetaData(this.type, this.representation, this.keyword, this.standaloneStatement, this.reserved, this.assignsWith, this.defaultLexeme, this.characters);
    }
}
