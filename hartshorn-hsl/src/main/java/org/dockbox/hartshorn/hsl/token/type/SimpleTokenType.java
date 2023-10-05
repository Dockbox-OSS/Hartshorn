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

        public Builder tokenName(final String tokenName) {
            this.tokenName = tokenName;
            return this;
        }

        public Builder representation(final String representation) {
            this.representation = representation;
            return this;
        }

        public Builder keyword(final boolean keyword) {
            this.keyword = keyword;
            return this;
        }

        public Builder standaloneStatement(final boolean standaloneStatement) {
            this.standaloneStatement = standaloneStatement;
            return this;
        }

        public Builder reserved(final boolean reserved) {
            this.reserved = reserved;
            return this;
        }

        public Builder assignsWith(final TokenType assignsWith) {
            this.assignsWith = assignsWith;
            return this;
        }

        public Builder defaultLexeme(final String defaultLexeme) {
            this.defaultLexeme = defaultLexeme;
            return this;
        }

        public Builder characters(final TokenCharacter... characters) {
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
