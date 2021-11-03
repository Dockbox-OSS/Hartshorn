package org.dockbox.hartshorn.web.processing;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;

@Getter
public class HttpRequestParameterLoaderContext extends ParameterLoaderContext {

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public HttpRequestParameterLoaderContext(final MethodContext<?, ?> method, final TypeContext<?> type, final Object instance, final ApplicationContext applicationContext, final HttpServletRequest request, final HttpServletResponse response) {
        super(method, type, instance, applicationContext);
        this.request = request;
        this.response = response;
    }
}
