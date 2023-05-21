package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.proxy.MethodInterceptorContext;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.option.Option;

import java.io.OutputStream;

public class SerializerMethodInterceptor<T, R> implements MethodInterceptor<T, R> {
    private final SerializationSourceConverter converter;
    private final MethodView<T, R> method;
    private final boolean returnsStringOrWrapper;
    private final ObjectMapper mapper;
    private final ConversionService conversionService;

    public SerializerMethodInterceptor(final SerializationSourceConverter converter, final MethodView<T, R> method,
                                       final boolean returnsStringOrWrapper, final ObjectMapper mapper,
                                       final ConversionService conversionService) {
        this.converter = converter;
        this.method = method;
        this.returnsStringOrWrapper = returnsStringOrWrapper;
        this.mapper = mapper;
        this.conversionService = conversionService;
    }

    @Override
    public R intercept(final MethodInterceptorContext<T, R> interceptorContext) throws Throwable {
        final Object[] arguments = interceptorContext.args();

        try (final OutputStream outputStream = this.converter.outputStream(this.method, arguments)) {
            final Option<?> result;

            if (outputStream == null && this.returnsStringOrWrapper) result = this.mapper.write(arguments[0]);
            else result = this.mapper.write(outputStream, arguments[0]);

            return this.conversionService.convert(result, this.method.returnType().type());
        }
    }
}
