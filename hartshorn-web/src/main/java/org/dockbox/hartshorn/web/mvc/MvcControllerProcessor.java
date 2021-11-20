package org.dockbox.hartshorn.web.mvc;

import org.dockbox.hartshorn.core.annotations.service.AutomaticActivation;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.services.ServiceProcessor;
import org.dockbox.hartshorn.web.MvcControllerContext;
import org.dockbox.hartshorn.web.RequestHandlerContext;
import org.dockbox.hartshorn.web.annotations.MvcController;
import org.dockbox.hartshorn.web.annotations.UseMvcServer;
import org.dockbox.hartshorn.web.annotations.http.HttpRequest;

@AutomaticActivation
public class MvcControllerProcessor implements ServiceProcessor<UseMvcServer> {

    @Override
    public Class<UseMvcServer> activator() {
        return UseMvcServer.class;
    }

    @Override
    public boolean preconditions(final ApplicationContext context, final TypeContext<?> type) {
        return type.annotation(MvcController.class).present() && !type.methods(HttpRequest.class).isEmpty();
    }

    @Override
    public <T> void process(final ApplicationContext context, final TypeContext<T> type) {
        final MvcControllerContext controllerContext = context.first(MvcControllerContext.class).get();
        for (final MethodContext<?, T> method : type.methods(HttpRequest.class)) {
            if (method.returnType().childOf(ViewTemplate.class)) {
                final RequestHandlerContext handlerContext = new RequestHandlerContext(context, method);
                controllerContext.add(handlerContext);
            }
            else {
                throw new IllegalArgumentException("Method " + method.name() + " must return a ViewTemplate");
            }
        }
    }
}
