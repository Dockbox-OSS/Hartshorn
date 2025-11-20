package org.dockbox.sample.proxies.processing;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.inject.processing.proxy.ProxyCallbackPostProcessor;
import org.dockbox.hartshorn.proxy.advice.wrap.ProxyCallback;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggableCallbackPostProcessor extends ProxyCallbackPostProcessor {

    @Override
    public <T> boolean preconditions(
        InjectionCapableApplication application,
        MethodView<T, ?> method,
        ComponentKey<T> key,
        @Nullable T instance,
        ComponentProcessingContext<T> processingContext
    ) {
        return method.annotations().has(Loggable.class);
    }

    @Override
    public @Nullable <T> ProxyCallback<T> doBefore(
        InjectionCapableApplication application,
        MethodView<T, ?> method,
        ComponentKey<T> key,
        @Nullable T instance,
        ComponentProcessingContext<T> processingContext
    ) {
        return context -> getLogger(processingContext).info("Before: {}", method.qualifiedName());
    }

    @Override
    public @Nullable <T> ProxyCallback<T> doAfter(
        InjectionCapableApplication application,
        MethodView<T, ?> method,
        ComponentKey<T> key,
        @Nullable T instance,
        ComponentProcessingContext<T> processingContext
    ) {
        return context -> getLogger(processingContext).info("After: {}", method.qualifiedName());
    }

    @Override
    public @Nullable <T> ProxyCallback<T> doAfterThrowing(
        InjectionCapableApplication application,
        MethodView<T, ?> method,
        ComponentKey<T> key,
        @Nullable T instance,
        ComponentProcessingContext<T> processingContext
    ) {
        return context -> {
            Throwable error = context.error();
            String message = error != null ? error.getMessage() : "Unknown error";
            getLogger(processingContext).info("Error: {}: {}", method.qualifiedName(), message);
        };
    }

    private Logger getLogger(ComponentProcessingContext<?> processingContext) {
        return processingContext.computeIfAbsent(
            Logger.class,
            key -> LoggerFactory.getLogger(processingContext.type().type())
        );
    }
}
