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

import java.util.Arrays;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        SimpleTokenType that = (SimpleTokenType) o;
        return this.keyword == that.keyword
            && this.standaloneStatement == that.standaloneStatement
            && this.reserved == that.reserved
            && Objects.equals(this.tokenName, that.tokenName)
            && Objects.equals(this.representation, that.representation)
            && Objects.equals(this.assignsWith, that.assignsWith)
            && Objects.equals(this.defaultLexeme, that.defaultLexeme)
            && Arrays.equals(this.characters, that.characters);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(this.tokenName, this.representation, this.keyword, this.standaloneStatement, this.reserved, this.assignsWith, this.defaultLexeme);
        result = 31 * result + Arrays.hashCode(this.characters);
        return result;
    }

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
