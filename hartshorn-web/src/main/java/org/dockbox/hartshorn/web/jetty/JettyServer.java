package org.dockbox.hartshorn.web.jetty;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.exceptions.Except;
import org.dockbox.hartshorn.web.HttpStatus;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

public class JettyServer extends Server {

    @Inject
    private JettyErrorHandler errorHandler;

    @Inject
    private ApplicationContext applicationContext;

    @Override
    public void handle(final HttpChannel channel) throws IOException, ServletException {
        final String target = channel.getRequest().getPathInfo();
        final Request request = channel.getRequest();
        final Response response = channel.getResponse();

        if (HttpMethod.OPTIONS.is(request.getMethod()) || "*".equals(target)) {
            if (!HttpMethod.OPTIONS.is(request.getMethod())) {
                request.setHandled(true);
                response.sendError(HttpStatus.BAD_REQUEST.value());
            }
            else {
                this.handleOptions(request, response);
                if (!request.isHandled())
                    this.handle(target, request, request, response);
            }
        }
        else {
            try {
                this.handle(target, request, request, response);
            }
            catch (final Throwable e) {
                Except.handle("Encountered unexpected exception while handling request", e);
                String contentType = response.getContentType();
                if (contentType == null) contentType = "";
                Throwable cause = e;
                // Ensure we unwrap internal exceptions before proceeding
                if (e instanceof ServletException) cause = e.getCause();
                if (cause instanceof ApplicationException) cause = cause.getCause();

                request.setAttribute(RequestDispatcher.ERROR_EXCEPTION, cause);
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                this.errorHandler.generateAcceptableResponse(request, request, response, HttpStatus.INTERNAL_SERVER_ERROR.value(), cause.getMessage(), contentType);
            }
        }
    }
}
