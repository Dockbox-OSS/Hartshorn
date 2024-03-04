package org.dockbox.hartshorn.util.introspect.convert.support;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.convert.Converter;

public class StringToArrayConverter implements Converter<String, String[]> {

    private final String delimiter;

    public StringToArrayConverter() {
        this(",");
    }

    public StringToArrayConverter(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public String @Nullable [] convert(@Nullable String input) {
        if (null != input) {
            return input.split(this.delimiter);
        }
        return new String[0];
    }
}
