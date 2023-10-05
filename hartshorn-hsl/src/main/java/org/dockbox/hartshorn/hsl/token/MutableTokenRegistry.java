package org.dockbox.hartshorn.hsl.token;

import org.dockbox.hartshorn.hsl.token.type.TokenType;

public interface MutableTokenRegistry extends TokenRegistry {

    void addTokens(TokenType... types);
}
