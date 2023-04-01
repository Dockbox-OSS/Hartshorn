package org.dockbox.hartshorn.util.introspect.convert.support;

import org.dockbox.hartshorn.util.introspect.convert.ConditionalConverter;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.option.Option;

public class OptionToObjectConverterFactory implements ConverterFactory<Option<?>, Object>, ConditionalConverter {

    @Override
    public <O> Converter<Option<?>, O> create(final Class<O> targetType) {
        return input -> input.map(targetType::cast).orNull();
    }

    @Override
    public boolean canConvert(final Object source, final Class<?> targetType) {
        final Option<?> option = (Option<?>) source;
        final Object value = option.orNull();
        return value == null || targetType.isAssignableFrom(value.getClass());
    }
}
