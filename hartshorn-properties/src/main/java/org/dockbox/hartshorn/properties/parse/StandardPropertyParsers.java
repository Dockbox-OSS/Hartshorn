package org.dockbox.hartshorn.properties.parse;

import org.dockbox.hartshorn.properties.ConfiguredProperty;

public final class StandardPropertyParsers {

    private StandardPropertyParsers() {
        // Static access only
    }

    public static final ConfiguredPropertyParser<Boolean> BOOLEAN = property -> property.value().map(Boolean::parseBoolean);

    public static final ConfiguredPropertyParser<Integer> INTEGER = property -> property.value().map(Integer::parseInt);

    public static final ConfiguredPropertyParser<Long> LONG = property -> property.value().map(Long::parseLong);

    public static final ConfiguredPropertyParser<Double> DOUBLE = property -> property.value().map(Double::parseDouble);

    public static final ConfiguredPropertyParser<Float> FLOAT = property -> property.value().map(Float::parseFloat);

    public static final ConfiguredPropertyParser<String> STRING = ConfiguredProperty::value;

    public static final ConfiguredPropertyParser<Character> CHARACTER = property -> property.value().map(value -> value.charAt(0));

    public static final ConfiguredPropertyParser<Short> SHORT = property -> property.value().map(Short::parseShort);

    public static final ConfiguredPropertyParser<Byte> BYTE = property -> property.value().map(Byte::parseByte);

    public static final ConfiguredPropertyParser<Byte> HEX_BYTE = property -> property.value().map(value -> (byte) Integer.parseInt(value, 16));

}
