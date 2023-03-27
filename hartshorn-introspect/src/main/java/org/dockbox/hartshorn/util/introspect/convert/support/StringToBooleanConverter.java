package org.dockbox.hartshorn.util.introspect.convert.support;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.util.introspect.convert.Converter;

public class StringToBooleanConverter implements Converter<String, Boolean> {

    @Override
    public Boolean convert(final @NonNull String input) {
        if (input != null) {
            return Boolean.parseBoolean(input);
        }
        return null;
    }
}
