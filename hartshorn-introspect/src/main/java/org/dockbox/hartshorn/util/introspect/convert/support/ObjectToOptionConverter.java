package org.dockbox.hartshorn.util.introspect.convert.support;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.option.Option;

public class ObjectToOptionConverter implements Converter<Object, Option<?>> {

    @Override
    public Option<?> convert(final @Nullable Object input) {
        return Option.of(input);
    }
}
