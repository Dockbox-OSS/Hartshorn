package org.dockbox.hartshorn.profiles.parse.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.convert.ConditionalConverter;
import org.dockbox.hartshorn.util.introspect.convert.ConvertibleTypePair;
import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;

public class JacksonStringToObjectConverter implements GenericConverter, ConditionalConverter {

    private final ObjectMapper objectMapper;

    public JacksonStringToObjectConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean canConvert(Object source, Class<?> targetType) {
        return source instanceof String;
    }

    @Override
    public Set<ConvertibleTypePair> convertibleTypes() {
        return Set.of(ConvertibleTypePair.of(String.class, Object.class));
    }

    @Override
    public @Nullable <I, O> Object convert(@Nullable Object source, @NonNull Class<I> sourceType, @NonNull Class<O> targetType) {
        if (source instanceof String string) {
            try {
                return this.objectMapper.readValue(string, targetType);
            }
            catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
