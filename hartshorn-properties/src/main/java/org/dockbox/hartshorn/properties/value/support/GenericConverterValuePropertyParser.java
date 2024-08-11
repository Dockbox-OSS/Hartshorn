package org.dockbox.hartshorn.properties.value.support;

import org.dockbox.hartshorn.properties.ValueProperty;
import org.dockbox.hartshorn.properties.value.ValuePropertyParser;
import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;
import org.dockbox.hartshorn.util.option.Option;

public record GenericConverterValuePropertyParser<T>(
        GenericConverter converter,
        Class<T> targetType
) implements ValuePropertyParser<T> {

    @Override
    public Option<T> parse(ValueProperty property) {
        return property.value()
                .map(value -> converter.convert(value, String.class, this.targetType()))
                .cast(this.targetType());
    }
}
