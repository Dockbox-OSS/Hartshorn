package org.dockbox.hartshorn.profiles.parse.support;

import org.dockbox.hartshorn.profiles.ValueProfileProperty;
import org.dockbox.hartshorn.profiles.parse.ValueProfilePropertyParser;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.option.Option;

public class ConverterValueProfilePropertyParser<T> implements ValueProfilePropertyParser<T> {

    private final Converter<String, T> converter;

    public ConverterValueProfilePropertyParser(Converter<String, T> converter) {
        this.converter = converter;
    }

    @Override
    public Option<T> parse(ValueProfileProperty property) {
        return property.rawValue().map(this.converter::convert);
    }
}
