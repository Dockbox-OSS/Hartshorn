package org.dockbox.hartshorn.web.jetty;

import jakarta.servlet.ServletException;
import org.dockbox.hartshorn.web.error.RequestErrorHandler;
import org.eclipse.jetty.ee10.servlet.ServletApiResponse;
import org.eclipse.jetty.ee10.servlet.ServletContextRequest;
import org.eclipse.jetty.ee10.servlet.ServletContextResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.util.Callback;

public class JettyErrorHandlerAdapter implements Request.Handler {

    private final RequestErrorHandler<Throwable> errorHandler;

    public JettyErrorHandlerAdapter(RequestErrorHandler<Throwable> errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public boolean handle(Request request, Response response, Callback callback) throws Exception {
        ServletException servletException = (ServletException) request.getAttribute(
                ErrorHandler.ERROR_EXCEPTION
        );
        ServletContextRequest servletContextRequest = (ServletContextRequest) request;
        ServletContextResponse servletContextResponse = (ServletContextResponse) response;

        ServletApiResponse servletApiResponse = servletContextResponse.getServletApiResponse();
        this.errorHandler.handle(
                servletException.getCause(),
                servletContextRequest.getServletApiRequest(),
                servletApiResponse
        );
//
//        if (!servletApiResponse.getServletChannel().getHttpOutput().isClosed()) {
//            servletApiResponse.flushBuffer();
//        }
        return true;
    }
}
