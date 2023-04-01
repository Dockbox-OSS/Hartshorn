package org.dockbox.hartshorn.util.introspect.convert.support;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Collection;
import java.util.Optional;

public class OptionalToCollectionConverterFactory implements ConverterFactory<Optional<?>, Collection<?>> {

    private final Converter<Optional<?>, Option<?>> helperOptionalToOptionConverter;
    private final ConverterFactory<Option<?>, Collection<?>> helperOptionToCollectionConverterFactory;

    public OptionalToCollectionConverterFactory(final Introspector introspector) {
        this(new OptionalToOptionConverter(), new OptionToCollectionConverterFactory(introspector));
    }

    public OptionalToCollectionConverterFactory(final Converter<Optional<?>, Option<?>> helperOptionalToOptionConverter, final ConverterFactory<Option<?>, Collection<?>> helperOptionToCollectionConverterFactory) {
        this.helperOptionalToOptionConverter = helperOptionalToOptionConverter;
        this.helperOptionToCollectionConverterFactory = helperOptionToCollectionConverterFactory;
    }

    @Override
    public <O extends Collection<?>> Converter<Optional<?>, O> create(final Class<O> targetType) {
        final Converter<Option<?>, O> optionToCollectionConverter = this.helperOptionToCollectionConverterFactory.create(targetType);
        return input -> {
            final Option<?> option = this.helperOptionalToOptionConverter.convert(input);
            return optionToCollectionConverter.convert(option);
        };
    }
}
