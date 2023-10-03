package org.dockbox.hartshorn.hsl.token;

import org.dockbox.hartshorn.hsl.token.type.TokenTypePair;

public interface TokenPairList {

    TokenTypePair block();

    TokenTypePair parameters();

    TokenTypePair array();

}
