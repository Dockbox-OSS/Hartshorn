package org.dockbox.hartshorn.properties.parse.support;

import org.dockbox.hartshorn.properties.ConfiguredProperty;
import org.dockbox.hartshorn.properties.parse.ConfiguredPropertyParser;
import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;
import org.dockbox.hartshorn.util.option.Option;

public record GenericConverterConfiguredPropertyParser<T>(
        GenericConverter converter,
        Class<T> targetType
) implements ConfiguredPropertyParser<T> {

    @Override
    public Option<T> parse(ConfiguredProperty property) {
        return property.value()
                .map(value -> converter.convert(value, String.class, this.targetType()))
                .cast(this.targetType());
    }
}
