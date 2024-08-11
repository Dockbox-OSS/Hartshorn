package org.dockbox.hartshorn.properties.value.support;

import org.dockbox.hartshorn.properties.ValueProperty;
import org.dockbox.hartshorn.properties.value.ValuePropertyParser;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.option.Option;

public class ConverterValuePropertyParser<T> implements ValuePropertyParser<T> {

    private final Converter<String, T> converter;

    public ConverterValuePropertyParser(Converter<String, T> converter) {
        this.converter = converter;
    }

    @Override
    public Option<T> parse(ValueProperty property) {
        return property.value().map(converter::convert);
    }
}
