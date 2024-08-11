package org.dockbox.hartshorn.util.introspect.convert.support;

import java.util.Set;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.convert.ConvertibleTypePair;
import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;

public class StringToArrayConverter implements GenericConverter {

    private final Pattern delimiter;

    public StringToArrayConverter() {
        this.delimiter = Pattern.compile(",");
    }

    public StringToArrayConverter(Pattern delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public Set<ConvertibleTypePair> convertibleTypes() {
        return Set.of(ConvertibleTypePair.of(String.class, String[].class));
    }

    @Override
    public @Nullable <I, O> Object convert(@Nullable Object source, @NonNull Class<I> sourceType, @NonNull Class<O> targetType) {
        if (source instanceof String charSequence) {
            return charSequence.split(delimiter.pattern());
        }
        return null;
    }
}
