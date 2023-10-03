package org.dockbox.hartshorn.hsl.token.type;

import java.util.Locale;

public interface EnumTokenType extends DelegateTokenType {

    String name();

    @Override
    default String tokenName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
