package org.dockbox.hartshorn.util.introspect.convert.support;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;

import java.lang.reflect.Array;
import java.util.Collection;

public class ObjectToCollectionConverterFactory implements ConverterFactory<Object, Collection<?>> {

    private final ArrayToCollectionConverterFactory helperConverterFactory;

    public ObjectToCollectionConverterFactory(final ArrayToCollectionConverterFactory helperConverterFactory) {
        this.helperConverterFactory = helperConverterFactory;
    }

    @Override
    public <O extends Collection<?>> Converter<Object, O> create(final Class<O> targetType) {
        final Converter<Object[], O> converter = ObjectToCollectionConverterFactory.this.helperConverterFactory.create(targetType);
        return new ObjectToCollectionConverter<>(converter);
    }

    private static class ObjectToCollectionConverter<O extends Collection<?>> implements Converter<Object, O> {

        private final Converter<Object[], O> helperConverter;

        public ObjectToCollectionConverter(final Converter<Object[], O> helperConverter) {
            this.helperConverter = helperConverter;
        }

        @Override
        public O convert(final @NonNull Object source) {
            final Object[] array = (Object[]) Array.newInstance(source.getClass(), 1);
            array[0] = source;
            return this.helperConverter.convert(array);
        }
    }
}
