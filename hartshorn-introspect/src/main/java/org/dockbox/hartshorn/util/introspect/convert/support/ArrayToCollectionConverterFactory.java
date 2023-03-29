package org.dockbox.hartshorn.util.introspect.convert.support;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;

import java.util.Arrays;
import java.util.Collection;

public class ArrayToCollectionConverterFactory implements ConverterFactory<Object[], Collection<?>> {

    private final CollectionFactory collectionFactory;

    public ArrayToCollectionConverterFactory(final Introspector introspector) {
        this.collectionFactory = new CollectionFactory(introspector);
    }

    @Override
    public <O extends Collection<?>> Converter<Object[], O> create(final Class<O> targetType) {
        return new ArrayToCollectionConverter<>(targetType);
    }

    private class ArrayToCollectionConverter<O extends Collection<?>> implements Converter<Object[], O> {

        private final Class<O> targetType;

        public ArrayToCollectionConverter(final Class<O> targetType) {
            this.targetType = targetType;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public O convert(final Object @Nullable [] source) {
            assert source != null;
            final Class<?> componentType = source.getClass().getComponentType();
            final CollectionFactory collectionFactory = ArrayToCollectionConverterFactory.this.collectionFactory;
            final Collection collection = collectionFactory.createCollection(this.targetType, componentType, source.length);
            collection.addAll(Arrays.asList(source));
            return (O) collection;
        }
    }
}
