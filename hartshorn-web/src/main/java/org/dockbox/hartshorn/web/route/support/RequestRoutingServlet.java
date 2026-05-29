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
import org.dockbox.hartshorn.web.HttpMethod;
import org.dockbox.hartshorn.web.message.RequestAttributes;
import org.dockbox.hartshorn.web.route.RequestHandler;
import org.dockbox.hartshorn.web.spec.PathSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A servlet that routes incoming HTTP requests to the appropriate request handlers based on the
 * HTTP method. The provided {@link PathSpec} is used to match the request path and extract any path
 * parameters, which are then made available to the request handlers via the
 * {@link RequestAttributes attributes} of the request. If no matching handler is found for the
 * request, the servlet falls back to the default behavior of the superclass, allowing it to handle
 * the request as usual.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class RequestRoutingServlet extends HttpServlet {

    private final PathSpec pathSpec;
    private final Map<HttpMethod, RequestHandler> handlers;

    public RequestRoutingServlet(PathSpec pathSpec, Map<HttpMethod, RequestHandler> handlers) {
        this.pathSpec = pathSpec;
        this.handlers = handlers;
    }

    private interface ServletFunction {
        void handle(
            HttpServletRequest request,
            HttpServletResponse response
        ) throws ServletException, IOException;
    }

    /**
     * Handles the incoming request by matching the request path against the servlet's path
     * specification and invoking the corresponding request handler if a match is found. If no match
     * is found, the provided fallback function is invoked to allow the superclass to handle the
     * request as usual.
     *
     * @param method the HTTP method of the request
     * @param req the servlet request
     * @param resp the servlet response
     *
     * @throws ServletException if an error occurs while handling the request
     */
    private void handleRequest(
        HttpMethod method,
        HttpServletRequest req,
        HttpServletResponse resp,
        ServletFunction fallback
    ) throws ServletException, IOException {
        Map<String, String> pathParameters = new HashMap<>();
        String fullPath = req.getServletPath();
        if (req.getPathInfo() != null) {
            fullPath += req.getPathInfo();
        }
        if (pathSpec.matches(fullPath, pathParameters)
                && this.handlers.containsKey(method)) {
            RequestAttributes.setPathParameters(req, pathParameters);
            try {
                this.handlers.get(method).handle(req, resp);
                resp.flushBuffer();
            } catch (Exception e) {
                // TODO: Allow custom error handling
                throw new ServletException("Failed to handle request", e);
            }
        }
        else {
            fallback.handle(req, resp);
        }
    }

    @Override
    protected void doGet(
        HttpServletRequest req,
        HttpServletResponse resp
    ) throws ServletException, IOException {
        this.handleRequest(HttpMethod.GET, req, resp, super::doGet);
    }

    @Override
    protected void doHead(
        HttpServletRequest req,
        HttpServletResponse resp
    ) throws ServletException, IOException {
        this.handleRequest(HttpMethod.HEAD, req, resp, super::doHead);
    }

    @Override
    protected void doPatch(
        HttpServletRequest req,
        HttpServletResponse resp
    ) throws ServletException, IOException {
        this.handleRequest(HttpMethod.PATCH, req, resp, super::doPatch);
    }

    @Override
    protected void doPost(
        HttpServletRequest req,
        HttpServletResponse resp
    ) throws ServletException, IOException {
        this.handleRequest(HttpMethod.POST, req, resp, super::doPost);
    }

    @Override
    protected void doPut(
        HttpServletRequest req,
        HttpServletResponse resp
    ) throws ServletException, IOException {
        this.handleRequest(HttpMethod.PUT, req, resp, super::doPut);
    }

    @Override
    protected void doDelete(
        HttpServletRequest req,
        HttpServletResponse resp
    ) throws ServletException, IOException {
        this.handleRequest(HttpMethod.DELETE, req, resp, super::doDelete);
    }

    @Override
    protected void doOptions(
        HttpServletRequest req,
        HttpServletResponse resp
    ) throws ServletException, IOException {
        this.handleRequest(HttpMethod.OPTIONS, req, resp, super::doOptions);
    }

    @Override
    protected void doTrace(
        HttpServletRequest req,
        HttpServletResponse resp
    ) throws ServletException, IOException {
        this.handleRequest(HttpMethod.TRACE, req, resp, super::doTrace);
    }
}
