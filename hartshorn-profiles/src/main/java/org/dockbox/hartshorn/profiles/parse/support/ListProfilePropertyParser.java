package org.dockbox.hartshorn.profiles.parse.support;

import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.profiles.CompositeProfileProperty;
import org.dockbox.hartshorn.profiles.ProfileProperty;
import org.dockbox.hartshorn.profiles.SimpleProfileProperty;
import org.dockbox.hartshorn.profiles.ValueProfileProperty;
import org.dockbox.hartshorn.profiles.parse.ProfilePropertyParser;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToArrayConverter;
import org.dockbox.hartshorn.util.option.Option;

public class ListProfilePropertyParser implements ProfilePropertyParser<List<ProfileProperty>> {

    private static final Converter<String, String[]> helperConverter = new StringToArrayConverter();

    @Override
    public Option<List<ProfileProperty>> parse(ValueProfileProperty property) {
        return property.rawValue().map(rawValue -> {
            List<ProfileProperty> properties = new ArrayList<>();
            String @Nullable [] convert = helperConverter.convert(rawValue);
            for (int i = 0; i < convert.length; i++) {
                String value = convert[i];
                ProfileProperty valueProperty = new SimpleProfileProperty(property.name() + "[" + i + "]", value);
                properties.add(valueProperty);
            }
            return properties;
        });
    }

    @Override
    public Option<List<ProfileProperty>> parse(CompositeProfileProperty property) {
        List<ProfileProperty> properties = new ArrayList<>();
        for (ValueProfileProperty valueProperty : property.properties()) {
            if (valueProperty.name().matches(".*\\[\\d+]")) {
                properties.add(valueProperty);
            }
        }
        return Option.of(properties);
    }
}
