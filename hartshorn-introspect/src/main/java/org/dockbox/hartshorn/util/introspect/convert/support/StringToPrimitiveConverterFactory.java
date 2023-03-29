package org.dockbox.hartshorn.util.introspect.convert.support;

import org.dockbox.hartshorn.util.introspect.convert.ConditionalConverter;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;

import java.util.Map;

public class StringToPrimitiveConverterFactory implements ConverterFactory<String, Object>, ConditionalConverter {

    private static final ConverterFactory<String, Number> NUMBER_CONVERTER_FACTORY = new StringToNumberConverterFactory();
    private static final Map<Class<?>, Converter<String, ?>> PRIMITIVE_CONVERTERS = Map.ofEntries(
            Map.entry(boolean.class, new StringToBooleanConverter().andThen(aBoolean -> aBoolean.booleanValue())),
            Map.entry(char.class, new StringToCharacterConverter().andThen(character -> character.charValue())),
            Map.entry(byte.class, NUMBER_CONVERTER_FACTORY.create(Byte.class).andThen(aByte -> aByte.byteValue())),
            Map.entry(double.class, NUMBER_CONVERTER_FACTORY.create(Double.class).andThen(aDouble -> aDouble.doubleValue())),
            Map.entry(float.class, NUMBER_CONVERTER_FACTORY.create(Float.class).andThen(aFloat -> aFloat.floatValue())),
            Map.entry(int.class, NUMBER_CONVERTER_FACTORY.create(Integer.class).andThen(integer -> integer.intValue())),
            Map.entry(long.class, NUMBER_CONVERTER_FACTORY.create(Long.class).andThen(aLong -> aLong.longValue())),
            Map.entry(short.class, NUMBER_CONVERTER_FACTORY.create(Short.class).andThen(aShort -> aShort.shortValue()))
    );

    @SuppressWarnings("unchecked")
    @Override
    public <O> Converter<String, O> create(final Class<O> targetType) {
        return (Converter<String, O>) PRIMITIVE_CONVERTERS.get(targetType);
    }

    @Override
    public boolean canConvert(final Object source, final Class<?> targetType) {
        if (!targetType.isPrimitive()) {
            return false;
        }
        if (PRIMITIVE_CONVERTERS.containsKey(targetType)) {
            return true;
        }
        // Should never encounter this case, but just in case
        throw new IllegalArgumentException("No primitive converter found for primitive type " + targetType.getName());
    }
}
