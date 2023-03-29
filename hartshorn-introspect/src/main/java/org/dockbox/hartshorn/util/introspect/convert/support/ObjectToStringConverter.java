package org.dockbox.hartshorn.util.introspect.convert.support;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.convert.Converter;

public class ObjectToStringConverter implements Converter<Object, String> {

    @Override
    public @Nullable String convert(@Nullable final Object input) {
        return String.valueOf(input);
    }
}
