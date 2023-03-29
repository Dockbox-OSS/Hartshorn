package org.dockbox.hartshorn.util.introspect.convert.support;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.convert.Converter;

import java.util.UUID;

public class StringToUUIDConverter implements Converter<String, UUID> {

    @Override
    public @Nullable UUID convert(final @Nullable String input) {
        if (input != null) {
            return UUID.fromString(input);
        }
        return null;
    }
}
