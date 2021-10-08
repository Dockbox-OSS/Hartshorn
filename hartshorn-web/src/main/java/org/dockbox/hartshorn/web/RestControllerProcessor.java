package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.MethodContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.services.ServiceProcessor;
import org.dockbox.hartshorn.web.annotations.HttpRequest;
import org.dockbox.hartshorn.web.annotations.UseWebStarter;

public class RestControllerProcessor implements ServiceProcessor<UseWebStarter> {
    @Override
    public Class<UseWebStarter> activator() {
        return UseWebStarter.class;
    }

    @Override
    public boolean preconditions(final ApplicationContext context, final TypeContext<?> type) {
        return !type.flatMethods(HttpRequest.class).isEmpty();
    }

    @Override
    public <T> void process(final ApplicationContext context, final TypeContext<T> type) {
        final ControllerContext controllerContext = context.first(ControllerContext.class).get();
        for (final MethodContext<?, T> method : type.flatMethods(HttpRequest.class)) {
            controllerContext.add(new RequestHandlerContext(context, method));
        }
    }
}
