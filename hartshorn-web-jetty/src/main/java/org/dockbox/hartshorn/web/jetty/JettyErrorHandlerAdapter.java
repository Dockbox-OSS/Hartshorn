/*
 * Copyright 2019-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

/**
 * An adapter that allows a {@link RequestErrorHandler} to be used as a Jetty
 * {@link Request.Handler}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class JettyErrorHandlerAdapter implements Request.Handler {

    private final RequestErrorHandler<Throwable> errorHandler;

    public JettyErrorHandlerAdapter(RequestErrorHandler<Throwable> errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public boolean handle(Request request, Response response, Callback callback) throws Exception {
        ServletException servletException = (ServletException) request.getAttribute(ErrorHandler.ERROR_EXCEPTION);
        ServletContextRequest servletContextRequest = (ServletContextRequest) request;
        ServletContextResponse servletContextResponse = (ServletContextResponse) response;

        ServletApiResponse servletApiResponse = servletContextResponse.getServletApiResponse();
        this.errorHandler.handle(
                servletException.getCause(), servletContextRequest.getServletApiRequest(), servletApiResponse);
        return true;
    }
}
