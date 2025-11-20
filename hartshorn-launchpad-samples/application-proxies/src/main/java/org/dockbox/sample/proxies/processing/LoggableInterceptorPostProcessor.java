package org.dockbox.sample.proxies.processing;

import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.inject.processing.proxy.AnnotatedMethodInterceptorPostProcessor;
import org.dockbox.hartshorn.inject.processing.proxy.MethodProxyContext;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggableInterceptorPostProcessor
    extends AnnotatedMethodInterceptorPostProcessor<Loggable> {

    @Override
    public Class<Loggable> annotation() {
        return Loggable.class;
    }

    @Override
    public <T> boolean preconditions(
        InjectionCapableApplication application,
        MethodProxyContext<T> methodContext,
        ComponentProcessingContext<T> processingContext
    ) {
        return true; // Just the presence of the annotation is enough
    }

    @Override
    public <T, R> MethodInterceptor<T, R> process(
        InjectionCapableApplication application,
        MethodProxyContext<T> methodContext,
        ComponentProcessingContext<T> processingContext
    ) {
        Logger logger = processingContext.computeIfAbsent(
            Logger.class,
            key -> LoggerFactory.getLogger(processingContext.type().type())
        );
        return context -> {
            logger.info("Intercepting method: {}", methodContext.method().qualifiedName());
            // Delegate to the original method
            return context.invokeDefault(context.args());
        };
    }
}
