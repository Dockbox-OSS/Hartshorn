package org.dockbox.hartshorn.web.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dockbox.hartshorn.util.types.TypeUtils;

import java.io.IOException;

public class ConfigurableRequestErrorHandler implements RequestErrorHandler<Throwable> {

    private final ErrorHandlerRegistry registry;
    private final RequestErrorHandler<Throwable> defaultHandler;

    public ConfigurableRequestErrorHandler(
            ErrorHandlerRegistry registry,
            RequestErrorHandler<Throwable> defaultHandler
    ) {
        this.registry = registry;
        this.defaultHandler = defaultHandler;
    }

    @Override
    public void handle(
            Throwable exception,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        this.registry.getHandler(exception.getClass())
                .orElse(this.defaultHandler)
                .handle(
                        TypeUtils.unchecked(exception, Throwable.class),
                        request,
                        response
                );
    }
}
