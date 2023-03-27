package org.dockbox.hartshorn.util.introspect.convert.support;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.util.introspect.convert.Converter;

import java.util.Optional;

public class ObjectToOptionalConverter implements Converter<Object, Optional<?>> {

    @Override
    public Optional<?> convert(final @NonNull Object input) {
        return Optional.of(input);
    }
}
