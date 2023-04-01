package org.dockbox.hartshorn.util.introspect.convert.support;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.DefaultValueProvider;
import org.dockbox.hartshorn.util.introspect.convert.DefaultValueProviderFactory;

import java.util.Collection;

public class CollectionToCollectionConverterFactory implements ConverterFactory<Collection<?>, Collection<?>> {

    private final DefaultValueProviderFactory<Collection<?>> defaultValueProviderFactory;

    public CollectionToCollectionConverterFactory(final Introspector introspector) {
        this(new CollectionDefaultValueProviderFactory(introspector).withDefaults());
    }

    public CollectionToCollectionConverterFactory(final DefaultValueProviderFactory<Collection<?>> defaultValueProviderFactory) {
        this.defaultValueProviderFactory = defaultValueProviderFactory;
    }

    @Override
    public <O extends Collection<?>> Converter<Collection<?>, O> create(final Class<O> targetType) {
        return new CollectionToCollectionConverter<>(this.defaultValueProviderFactory.create(targetType), targetType);
    }

    private static class CollectionToCollectionConverter<O extends Collection<?>> implements Converter<Collection<?>, O> {

        private final DefaultValueProvider<O> defaultValueProvider;
        private final Class<O> targetType;

        public CollectionToCollectionConverter(final DefaultValueProvider<O> defaultValueProvider, final Class<O> targetType) {
            this.defaultValueProvider = defaultValueProvider;
            this.targetType = targetType;
        }

        @Override
        public O convert(final Collection<?> source) {
            //noinspection unchecked
            final Collection<Object> collection = (Collection<Object>) this.defaultValueProvider.defaultValue();
            collection.addAll(source);
            return this.targetType.cast(collection);
        }
    }
}
