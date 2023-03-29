package org.dockbox.hartshorn.util.introspect.convert.support;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Optional;

public class OptionToOptionalConverter implements Converter<Option<?>, Optional<?>> {

    @Override
    public @Nullable Optional<?> convert(final @Nullable Option<?> input) {
        assert input != null;
        return input.optional();
    }
}
