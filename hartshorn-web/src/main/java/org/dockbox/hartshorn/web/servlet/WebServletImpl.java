/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.web.servlet;

import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.annotations.inject.Bound;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.web.HttpAction;
import org.dockbox.hartshorn.web.HttpMethod;
import org.dockbox.hartshorn.web.HttpWebServer;
import org.dockbox.hartshorn.web.RequestHandlerContext;
import org.dockbox.hartshorn.web.ServletFactory;
import org.dockbox.hartshorn.web.ServletHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;

@Binds(WebServletImpl.class)
public class WebServletImpl implements WebServlet {

    @Getter
    private final ServletHandler handler;

    @Bound
    public WebServletImpl(final HttpWebServer starter, final RequestHandlerContext context) {
        this.handler = context.applicationContext().get(ServletFactory.class)
                .servletHandler(starter, context.httpRequest().method(), context.methodContext());
    }

    @Override
    public void get(final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallback) throws ApplicationException {
        this.handler.processRequest(HttpMethod.GET, req, res, fallback);
    }

    @Override
    public void head(final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallback) throws ApplicationException {
        this.handler.processRequest(HttpMethod.HEAD, req, res, fallback);
    }

    @Override
    public void post(final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallback) throws ApplicationException {
        this.handler.processRequest(HttpMethod.POST, req, res, fallback);
    }

    @Override
    public void put(final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallback) throws ApplicationException {
        this.handler.processRequest(HttpMethod.PUT, req, res, fallback);
    }

    @Override
    public void delete(final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallback) throws ApplicationException {
        this.handler.processRequest(HttpMethod.DELETE, req, res, fallback);
    }

    @Override
    public void options(final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallback) throws ApplicationException {
        this.handler.processRequest(HttpMethod.OPTIONS, req, res, fallback);
    }

    @Override
    public void trace(final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallback) throws ApplicationException {
        this.handler.processRequest(HttpMethod.TRACE, req, res, fallback);
    }
}
