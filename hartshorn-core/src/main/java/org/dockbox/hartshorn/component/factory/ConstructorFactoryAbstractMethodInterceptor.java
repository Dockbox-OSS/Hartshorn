package org.dockbox.hartshorn.component.factory;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.proxy.MethodInterceptorContext;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

public class ConstructorFactoryAbstractMethodInterceptor<T, R> extends ConstructorFactoryMethodInterceptor<T, R> {
    private final ConstructorView<?> constructor;
    private final ConversionService conversionService;
    private final MethodView<T, R> method;
    private final ApplicationContext context;
    private final boolean enable;

    public ConstructorFactoryAbstractMethodInterceptor(final ConstructorView<?> constructor,
                                                       final ConversionService conversionService,
                                                       final MethodView<T, R> method, final ApplicationContext context,
                                                       final boolean enable) {
        this.constructor = constructor;
        this.conversionService = conversionService;
        this.method = method;
        this.context = context;
        this.enable = enable;
    }

    @Override
    public R intercept(final MethodInterceptorContext<T, R> interceptorContext) throws Throwable {
        final Object instance = this.constructor.create(interceptorContext.args()).rethrow().orNull();
        final R result = this.conversionService.convert(instance, this.method.returnType().type());
        return this.processInstance(this.context, result, this.enable);
    }
}
