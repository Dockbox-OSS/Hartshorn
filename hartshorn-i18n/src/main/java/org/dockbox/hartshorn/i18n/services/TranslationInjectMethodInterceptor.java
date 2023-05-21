package org.dockbox.hartshorn.i18n.services;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.proxy.MethodProxyContext;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.i18n.TranslationService;
import org.dockbox.hartshorn.i18n.annotations.InjectTranslation;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.proxy.MethodInterceptorContext;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;

public class TranslationInjectMethodInterceptor<T, R> implements MethodInterceptor<T, R> {
    private final ApplicationContext context;
    private final String key;
    private final InjectTranslation annotation;
    private final ConversionService conversionService;
    private final MethodProxyContext<T> methodContext;

    public TranslationInjectMethodInterceptor(final ApplicationContext context, final String key,
                                              final InjectTranslation annotation, final ConversionService conversionService,
                                              final MethodProxyContext<T> methodContext) {
        this.context = context;
        this.key = key;
        this.annotation = annotation;
        this.conversionService = conversionService;
        this.methodContext = methodContext;
    }

    @Override
    public R intercept(final MethodInterceptorContext<T, R> interceptorContext) {
        // Prevents NPE when formatting cached resources without arguments
        final Object[] args = interceptorContext.args();
        final Object[] objects = null == args ? TranslationInjectPostProcessor.EMPTY_ARGS : args;
        final Message message = this.context.get(TranslationService.class).getOrCreate(this.key, this.annotation.value()).format(objects);

        //noinspection unchecked
        return (R) this.conversionService.convert(message, this.methodContext.method().returnType().type());
    }
}
