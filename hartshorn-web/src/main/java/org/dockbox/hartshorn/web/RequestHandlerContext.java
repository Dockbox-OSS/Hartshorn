package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.DefaultCarrierContext;
import org.dockbox.hartshorn.di.context.element.MethodContext;
import org.dockbox.hartshorn.web.annotations.Request;

import lombok.Getter;

public class RequestHandlerContext extends DefaultCarrierContext {

    @Getter private final MethodContext<?, ?> methodContext;
    @Getter private final Request request;

    public RequestHandlerContext(ApplicationContext applicationContext, MethodContext<?, ?> methodContext) {
        super(applicationContext);
        this.methodContext = methodContext;
        Exceptional<Request> request = methodContext.annotation(Request.class);
        if (request.absent()) throw new IllegalArgumentException(methodContext.parent().name() + "#" + methodContext.name() + " is not annotated with @Request or an extension of it.");
        this.request = request.get();
    }

    public String pathSpec() {
        String spec = this.request().value();
        spec = spec.startsWith("/") ? spec : '/' + spec;
        return spec;
    }
}
