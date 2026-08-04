package org.dockbox.hartshorn.web.error;

import org.dockbox.hartshorn.util.option.Option;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SimpleErrorHandlerRegistry implements ErrorHandlerRegistry {

    private final Map<
            Class<? extends Throwable>,
            RequestErrorHandler<? extends Throwable>
            > handlers = new ConcurrentHashMap<>();

    @Override
    public <E extends Throwable> void register(
            Class<E> exceptionType,
            RequestErrorHandler<E> handler
    ) {
        this.handlers.put(exceptionType, handler);
    }

    @Override
    public Option<RequestErrorHandler<? extends Throwable>> getHandler(
            Class<? extends Throwable> exceptionType
    ) {
        // If there's an exact match, always prefer that
        if (this.handlers.containsKey(exceptionType)) {
            return Option.of(handlers.get(exceptionType));
        }
        // Type-match for nearest compatible handler
        return Option.of(this.handlers.keySet().stream()
                .filter(handlerType -> handlerType.isAssignableFrom(exceptionType))
                .collect(Collectors.toMap(
                        handlerType -> handlerType,
                        handlerType -> this.distance(exceptionType, handlerType)
                ))
                .entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .map(handlers::get));
    }

    /**
     * The distance in type hierarchy between the target (requested type) and source (in registry).
     * -1 if the source is not assignable from the target (source is not a parent of target).
     *
     * @param targetType
     * @param sourceType
     * @return
     */
    private int distance(
            Class<? extends Throwable> targetType,
            Class<? extends Throwable> sourceType
    ) {
        if (!sourceType.isAssignableFrom(targetType)) {
            return -1;
        }
        int distance = 0;
        Class<?> current = targetType;
        while (current != null && !current.equals(sourceType)) {
            current = current.getSuperclass();
            distance++;
        }
        return distance;
    }
}
