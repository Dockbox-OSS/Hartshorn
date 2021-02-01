package org.dockbox.selene.core;

import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.util.SeleneUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public final class PlatformConversionService {

    private static final Map<Class<?>, Function<?, ?>> mappers = SeleneUtils.emptyConcurrentMap();

    private PlatformConversionService() {}

    public static <I, O> O map(I in) {
        return (O) mapSafely(in).orNull();
    }

    public static <I, O> Exceptional<O> mapSafely(I in) {
        return mapSafely(in, in.getClass());
    }

    public static <I, O> O map(I in, Class<?> forceLookupType) {
        return (O) mapSafely(in, forceLookupType).orNull();
    }

    public static <I, O> Exceptional<O> mapSafely(I in, Class<?> forceLookupType) {
        try {
            Function<I, O> mapper = findMapper(forceLookupType);
            if (null == mapper) {
                throw new UnsupportedOperationException("No mapper for type [" + forceLookupType.getCanonicalName() + "] registered. " +
                        "Note that this is a internal mapping provider and should not be used outside of platform implementations!");
            }
            O out = mapper.apply(in);
            if (out instanceof Exceptional) {
                return (Exceptional<O>) out;
            }
            return Exceptional.ofNullable(out);
        } catch (Throwable e) {
            Selene.handle(e);
        }
        return Exceptional.empty();
    }

    private static <I, O> @Nullable Function<I, O> findMapper(Class<?> type) {
        if (mappers.containsKey(type)) {
            return (Function<I, O>) mappers.get(type);
        } else if (null != type.getSuperclass()) {
            return findMapper(type.getSuperclass());
        } else return null;
    }

    public static <Native, S> void register(Class<S> type, Function<S, Native> mapper) {
        if (mappers.containsKey(type)) {
            throw new IllegalArgumentException("Type [" + type.getCanonicalName() + "] is already registered!");
        }
        mappers.put(type, mapper);
    }

}
