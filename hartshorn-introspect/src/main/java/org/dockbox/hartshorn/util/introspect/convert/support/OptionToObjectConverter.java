package org.dockbox.hartshorn.util.introspect.convert.support;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.option.Option;

public class OptionToObjectConverter implements Converter<Option<?>, Object> {

    @Override
    public @Nullable Object convert(final @NonNull Option<?> input) {
        return input.orNull();
    }
}
