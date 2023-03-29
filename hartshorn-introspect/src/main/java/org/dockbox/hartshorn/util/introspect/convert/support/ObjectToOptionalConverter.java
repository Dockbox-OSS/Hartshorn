package org.dockbox.hartshorn.util.introspect.convert.support;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.convert.Converter;

import java.util.Optional;

public class ObjectToOptionalConverter implements Converter<Object, Optional<?>> {

    @Override
    public Optional<?> convert(final @Nullable Object input) {
        return Optional.ofNullable(input);
    }
}
