package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.DefaultCarrierContext;
import org.dockbox.hartshorn.di.context.element.MethodContext;
import org.dockbox.hartshorn.web.annotations.HttpRequest;
import org.dockbox.hartshorn.web.annotations.RestController;

import lombok.Getter;

public class RequestHandlerContext extends DefaultCarrierContext {

    @Getter private final MethodContext<?, ?> methodContext;
    @Getter private final HttpRequest httpRequest;
    @Getter private final String pathSpec;

    public RequestHandlerContext(final ApplicationContext applicationContext, final MethodContext<?, ?> methodContext) {
        super(applicationContext);
        this.methodContext = methodContext;
        final Exceptional<HttpRequest> request = methodContext.annotation(HttpRequest.class);
        if (request.absent()) throw new IllegalArgumentException(methodContext.parent().name() + "#" + methodContext.name() + " is not annotated with @Request or an extension of it.");
        this.httpRequest = request.get();

        final Exceptional<RestController> annotation = methodContext.parent().annotation(RestController.class);
        String spec = this.httpRequest().value();
        spec = spec.startsWith("/") ? spec : '/' + spec;

        if (annotation.present()) {
            String root = annotation.get().value();
            if (root.endsWith("/")) root = root.substring(0, root.length()-1);
            spec = root + spec;
        }

        this.pathSpec = spec.startsWith("/") ? spec : '/' + spec;
    }
}
