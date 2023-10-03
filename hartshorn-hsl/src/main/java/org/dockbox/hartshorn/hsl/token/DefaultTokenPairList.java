package org.dockbox.hartshorn.hsl.token;

import org.dockbox.hartshorn.hsl.token.type.PairTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenTypePair;

public class DefaultTokenPairList implements TokenPairList {

    @Override
    public TokenTypePair block() {
        return PairTokenType.LEFT_BRACE.pair();
    }

    @Override
    public TokenTypePair parameters() {
        return PairTokenType.LEFT_PAREN.pair();
    }

    @Override
    public TokenTypePair array() {
        return PairTokenType.ARRAY_OPEN.pair();
    }

    @Override
    public TokenTypePair generics() {
        return PairTokenType.ANGLE_OPEN.pair();
    }
}
