package org.dockbox.hartshorn.properties.parse.support;

import org.dockbox.hartshorn.util.introspect.convert.support.StringToEnumConverterFactory;

public class EnumConfiguredPropertyParser<E extends Enum<E>> extends ConverterConfiguredPropertyParser<E> {

    public EnumConfiguredPropertyParser(Class<E> type) {
        super(new StringToEnumConverterFactory().create(type));
    }
}
