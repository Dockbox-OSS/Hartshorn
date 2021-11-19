package org.dockbox.hartshorn.web.processing;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.web.mvc.ViewModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;

public class MvcParameterLoaderContext extends HttpRequestParameterLoaderContext{

    @Getter
    private final ViewModel viewModel;

    public MvcParameterLoaderContext(final MethodContext<?, ?> method, final TypeContext<?> type, final Object instance, final ApplicationContext applicationContext, final HttpServletRequest request, final HttpServletResponse response,
                                     final ViewModel viewModel) {
        super(method, type, instance, applicationContext, request, response);
        this.viewModel = viewModel;
    }
}
