package org.dockbox.hartshorn.util.introspect.convert;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface DefaultValueProvider<T> extends Converter<Null, T> {

    @Override
    default @Nullable T convert(@Nullable Null input) {
        assert input == null;
        return this.defaultValue();
    }

    @Nullable T defaultValue();
}
