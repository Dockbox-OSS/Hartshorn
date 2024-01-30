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

public record SimpleTokenType(
        String tokenName,
        String representation,
        boolean keyword,
        boolean standaloneStatement,
        boolean reserved,
        TokenType assignsWith,
        String defaultLexeme,
        TokenCharacter[] characters
) implements TokenType {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String tokenName;
        private String representation;
        private boolean keyword;
        private boolean standaloneStatement;
        private boolean reserved;
        private TokenType assignsWith;
        private String defaultLexeme;
        private TokenCharacter[] characters = new TokenCharacter[0];

        public Builder tokenName(String tokenName) {
            this.tokenName = tokenName;
            return this;
        }

        public Builder representation(String representation) {
            this.representation = representation;
            return this;
        }

        public Builder keyword(boolean keyword) {
            this.keyword = keyword;
            return this;
        }

        public Builder standaloneStatement(boolean standaloneStatement) {
            this.standaloneStatement = standaloneStatement;
            return this;
        }

        public Builder reserved(boolean reserved) {
            this.reserved = reserved;
            return this;
        }

        public Builder assignsWith(TokenType assignsWith) {
            this.assignsWith = assignsWith;
            return this;
        }

        public Builder defaultLexeme(String defaultLexeme) {
            this.defaultLexeme = defaultLexeme;
            return this;
        }

        public Builder characters(TokenCharacter... characters) {
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

        public SimpleTokenType build() {
            return new SimpleTokenType(this.tokenName, this.representation, this.keyword, this.standaloneStatement, this.reserved,
                    this.assignsWith, this.defaultLexeme, this.characters);
        }
    }
}
