package org.dockbox.hartshorn.component.factory;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.proxy.MethodProxyContext;
import org.dockbox.hartshorn.proxy.MethodInterceptorContext;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

public class ConstructorFactoryConcreteMethodInterceptor<T, R> extends ConstructorFactoryMethodInterceptor<T, R> {
    private final MethodProxyContext<T> methodContext;
    private final ConversionService conversionService;
    private final MethodView<T, R> method;
    private final ApplicationContext context;
    private final boolean enable;

    public ConstructorFactoryConcreteMethodInterceptor(final MethodProxyContext<T> methodContext,
                                                       final ConversionService conversionService,
                                                       final MethodView<T, R> method, final ApplicationContext context,
                                                       final boolean enable) {
        this.methodContext = methodContext;
        this.conversionService = conversionService;
        this.method = method;
        this.context = context;
        this.enable = enable;
    }

    @Override
    public R intercept(final MethodInterceptorContext<T, R> interceptorContext) throws Throwable {
        final T instance = interceptorContext.instance();
        final Object result = this.methodContext.method().invoke(instance, interceptorContext.args()).orNull();
        final R convertedResult = this.conversionService.convert(result, this.method.returnType().type());
        return this.processInstance(this.context, convertedResult, this.enable);
    }
}
