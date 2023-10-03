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
    ANGLE_OPEN(DefaultTokenCharacter.LESS, PairPosition.LEFT) {
        @Override
        public TokenType inverse() {
            return ANGLE_CLOSE;
        }
    },
    ANGLE_CLOSE(DefaultTokenCharacter.GREATER, PairPosition.RIGHT) {
        @Override
        public TokenType inverse() {
            return ANGLE_OPEN;
        }
    },
    ;

    private final TokenMetaData metaData;
    private final PairPosition position;

    PairTokenType(TokenCharacter character, PairPosition position) {
        this.metaData = TokenMetaData.builder(this)
                .representation(String.valueOf(character.character()))
                .defaultLexeme(String.valueOf(character.character()))
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
