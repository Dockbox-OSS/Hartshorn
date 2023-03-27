package org.dockbox.hartshorn.util.introspect.convert.support;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.util.introspect.convert.Converter;

public class StringToCharacterConverter implements Converter<String, Character> {

    @Override
    public Character convert(final @NonNull String input) {
        if (input != null) {
            return input.charAt(0);
        }
        return null;
    }
}
