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

package org.dockbox.hartshorn.web.route.support;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.HttpMethod;
import org.dockbox.hartshorn.web.route.HandlerMapping;
import org.dockbox.hartshorn.web.route.HandlerMappingResolver;

import java.io.IOException;

/**
 * Standard servlet for capturing servlet requests and routing them based on available
 * {@link HandlerMapping handler mappings}. This captures all available HTTP methods and maps them
 * to {@link org.dockbox.hartshorn.web.route.RouteMapping route mappings} through the provided
 * {@link HandlerMappingResolver}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class RequestRoutingServlet extends HttpServlet {

    private final HandlerMappingResolver registry;

    public RequestRoutingServlet(HandlerMappingResolver resolver) {
        this.registry = resolver;
    }

    /**
     * Handles the incoming request by resolving the corresponding {@link HandlerMapping} for the
     * given HTTP method and request path, and invoking the corresponding handler. If no handler is
     * found, a 404 response is returned.
     *
     * @param method the HTTP method of the request
     * @param req the servlet request
     * @param resp the servlet response
     *
     * @throws ServletException if an error occurs while handling the request
     * @throws IOException if an I/O error occurs while handling the request
     */
    protected void handleRequest(
            HttpMethod method,
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {
        Option<HandlerMapping> mapping = this.registry.resolve(method, req.getPathInfo(), req);
        if (mapping.present()) {
            try {
                mapping.get().handler().handle(req, resp);
            } catch (Exception e) {
                // TODO: Allow custom error handling
                throw new ServletException("Failed to handle request", e);
            }
        } else {
            // TODO: Allow custom 404 handling
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {
        this.handleRequest(HttpMethod.GET, req, resp);
    }

    @Override
    protected void doHead(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {
        this.handleRequest(HttpMethod.HEAD, req, resp);
    }

    @Override
    protected void doPatch(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {
        this.handleRequest(HttpMethod.PATCH, req, resp);
    }

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {
        this.handleRequest(HttpMethod.POST, req, resp);
    }

    @Override
    protected void doPut(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {
        this.handleRequest(HttpMethod.PUT, req, resp);
    }

    @Override
    protected void doDelete(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {
        this.handleRequest(HttpMethod.DELETE, req, resp);
    }

    @Override
    protected void doOptions(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {
        this.handleRequest(HttpMethod.OPTIONS, req, resp);
    }

    @Override
    protected void doTrace(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {
        this.handleRequest(HttpMethod.TRACE, req, resp);
    }
}
