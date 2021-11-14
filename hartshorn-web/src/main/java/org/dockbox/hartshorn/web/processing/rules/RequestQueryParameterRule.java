package org.dockbox.hartshorn.web.processing.rules;

import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.services.parameter.AnnotatedParameterLoaderRule;
import org.dockbox.hartshorn.web.annotations.RequestParam;
import org.dockbox.hartshorn.web.processing.HttpRequestParameterLoaderContext;

public class RequestQueryParameterRule extends AnnotatedParameterLoaderRule<RequestParam, HttpRequestParameterLoaderContext> {

    @Override
    protected Class<RequestParam> annotation() {
        return RequestParam.class;
    }

    @Override
    public <T> Exceptional<T> load(final ParameterContext<T> parameter, final int index, final HttpRequestParameterLoaderContext context, final Object... args) {
        return Exceptional.of(() -> {
            final RequestParam requestParam = parameter.annotation(RequestParam.class).get();
            String value = context.request().getParameter(requestParam.value());
            if (value == null) value = requestParam.or();

            if (parameter.type().is(String.class)) return (T) value;
            else if (parameter.type().isPrimitive()) {
                return TypeContext.toPrimitive(parameter.type(), value);
            }
            return null;
        });
    }
}
