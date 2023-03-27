package org.dockbox.hartshorn.util.introspect.convert.support;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.option.Option;

public class ObjectToOptionConverter implements Converter<Object, Option<?>> {

    @Override
    public Option<?> convert(final @NonNull Object input) {
        return Option.of(input);
    }
}
