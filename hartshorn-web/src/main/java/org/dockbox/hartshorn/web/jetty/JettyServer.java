/*
 * Copyright 2019-2023 the original author or authors.
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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.inject.Required;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.web.HttpStatus;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;

import java.io.IOException;

import jakarta.inject.Inject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;

@Component
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
                this.applicationContext.handle("Encountered unexpected exception while handling request", e);
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
