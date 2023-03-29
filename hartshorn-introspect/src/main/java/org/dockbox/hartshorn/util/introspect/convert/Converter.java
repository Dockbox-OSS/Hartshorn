package org.dockbox.hartshorn.util.introspect.convert;

import org.checkerframework.checker.nullness.qual.Nullable;

@FunctionalInterface
public interface Converter<I, O> {

    @Nullable
    O convert(@Nullable I input);

    default <T> Converter<I, T> andThen(final Converter<O, T> after) {
        return (final I i) -> {
            final O result = this.convert(i);
            return result != null ? after.convert(result) : null;
        };
    }
}
