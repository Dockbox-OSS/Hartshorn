package org.dockbox.hartshorn.properties.value.support;

import org.dockbox.hartshorn.util.introspect.convert.support.StringToEnumConverterFactory;

public class EnumValuePropertyParser<E extends Enum<E>> extends ConverterValuePropertyParser<E> {

    public EnumValuePropertyParser(Class<E> type) {
        super(new StringToEnumConverterFactory().create(type));
    }
}
