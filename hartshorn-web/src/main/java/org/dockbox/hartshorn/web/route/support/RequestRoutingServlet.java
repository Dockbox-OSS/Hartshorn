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

public class RequestRoutingServlet extends HttpServlet {

    private final HandlerMappingResolver registry;

    public RequestRoutingServlet(HandlerMappingResolver resolver) {
        this.registry = resolver;
    }

    protected void handleRequest(
            HttpMethod method,
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {
        Option<HandlerMapping> mapping = this.registry.resolve(method, req.getPathInfo(), req);
        if (mapping.present()) {
            try {
                mapping.get().handler().handle(req, resp);
            }
            catch (Exception e) {
                throw new ServletException("Failed to handle request", e);
            }
        }
        else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.handleRequest(HttpMethod.GET, req, resp);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.handleRequest(HttpMethod.HEAD, req, resp);
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.handleRequest(HttpMethod.PATCH, req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.handleRequest(HttpMethod.POST, req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.handleRequest(HttpMethod.PUT, req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.handleRequest(HttpMethod.DELETE, req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.handleRequest(HttpMethod.OPTIONS, req, resp);
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.handleRequest(HttpMethod.TRACE, req, resp);
    }
}
