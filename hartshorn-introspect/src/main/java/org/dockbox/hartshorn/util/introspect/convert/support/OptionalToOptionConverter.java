package org.dockbox.hartshorn.util.introspect.convert.support;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Optional;

public class OptionalToOptionConverter implements Converter<Optional<?>, Option<?>> {

    @Override
    public @Nullable Option<?> convert(@Nullable final Optional<?> input) {
        return Option.of(input);
    }
}
