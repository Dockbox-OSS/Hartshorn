package org.dockbox.hartshorn.util.introspect.convert.support;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.DefaultValueProviderFactory;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Collection;

public class OptionToCollectionConverterFactory implements ConverterFactory<Option<?>, Collection<?>> {

    private final DefaultValueProviderFactory<Collection<?>> defaultValueProviderFactory;

    public OptionToCollectionConverterFactory(final Introspector introspector) {
        this(new CollectionDefaultValueProviderFactory(introspector).withDefaults());
    }

    public OptionToCollectionConverterFactory(final DefaultValueProviderFactory<Collection<?>> defaultValueProviderFactory) {
        this.defaultValueProviderFactory = defaultValueProviderFactory;
    }

    @Override
    public <O extends Collection<?>> Converter<Option<?>, O> create(final Class<O> targetType) {
        return input -> {
            //noinspection unchecked
            final Collection<Object> collection = (Collection<Object>) this.defaultValueProviderFactory.create(targetType).defaultValue();
            if (input.present()) {
                collection.add(input.get());
            }
            return targetType.cast(collection);
        };
    }
}
