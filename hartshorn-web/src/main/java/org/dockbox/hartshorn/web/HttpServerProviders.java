package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.core.annotations.inject.Provider;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoader;
import org.dockbox.hartshorn.web.annotations.UseHttpServer;
import org.dockbox.hartshorn.web.processing.HttpServletParameterLoader;
import org.dockbox.hartshorn.web.servlet.ErrorServlet;
import org.dockbox.hartshorn.web.servlet.WebServlet;
import org.dockbox.hartshorn.web.servlet.WebServletImpl;

@Service(activators = UseHttpServer.class)
public abstract class HttpServerProviders {

    @Provider
    public ErrorServlet errorServlet() {
        return new ErrorServletImpl();
    }

    @Provider
    public Class<? extends WebServlet> webServlet() {
        return WebServletImpl.class;
    }

    @Provider
    public Class<WebServletImpl> webServletImpl() {
        return WebServletImpl.class;
    }

    @Provider
    public Class<ServletHandler> servletHandler() {
        return ServletHandler.class;
    }

    @Provider("http_webserver")
    public ParameterLoader parameterLoader() {
        return new HttpServletParameterLoader();
    }
}
