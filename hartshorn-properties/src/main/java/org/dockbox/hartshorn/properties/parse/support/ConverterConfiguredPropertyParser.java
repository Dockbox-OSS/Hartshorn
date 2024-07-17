package org.dockbox.hartshorn.properties.parse.support;

import org.dockbox.hartshorn.properties.ConfiguredProperty;
import org.dockbox.hartshorn.properties.parse.ConfiguredPropertyParser;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.option.Option;

public class ConverterConfiguredPropertyParser<T> implements ConfiguredPropertyParser<T> {

    private final Converter<String, T> converter;

    public ConverterConfiguredPropertyParser(Converter<String, T> converter) {
        this.converter = converter;
    }

    @Override
    public Option<T> parse(ConfiguredProperty property) {
        return property.value().map(converter::convert);
    }
}
