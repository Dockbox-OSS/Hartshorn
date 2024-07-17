package org.dockbox.hartshorn.properties.value;

import org.dockbox.hartshorn.properties.ValueProperty;
import org.dockbox.hartshorn.properties.value.support.GenericConverterValuePropertyParser;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToArrayConverter;

public final class StandardPropertyParsers {

    private StandardPropertyParsers() {
        // Static access only
    }

    public static final ValuePropertyParser<Boolean> BOOLEAN = property -> property.value().map(Boolean::parseBoolean);

    public static final ValuePropertyParser<Integer> INTEGER = property -> property.value().map(Integer::parseInt);

    public static final ValuePropertyParser<Long> LONG = property -> property.value().map(Long::parseLong);

    public static final ValuePropertyParser<Double> DOUBLE = property -> property.value().map(Double::parseDouble);

    public static final ValuePropertyParser<Float> FLOAT = property -> property.value().map(Float::parseFloat);

    public static final ValuePropertyParser<String> STRING = ValueProperty::value;

    public static final ValuePropertyParser<Character> CHARACTER = property -> property.value().map(value -> value.charAt(0));

    public static final ValuePropertyParser<Short> SHORT = property -> property.value().map(Short::parseShort);

    public static final ValuePropertyParser<Byte> BYTE = property -> property.value().map(Byte::parseByte);

    public static final ValuePropertyParser<Byte> HEX_BYTE = property -> property.value().map(value -> (byte) Integer.parseInt(value, 16));

    public static final ValuePropertyParser<String[]> STRING_LIST = new GenericConverterValuePropertyParser<>(
            new StringToArrayConverter(), String[].class);
}
