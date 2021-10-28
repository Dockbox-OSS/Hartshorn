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

package org.dockbox.hartshorn.jetty;

import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.annotations.inject.Bound;
import org.dockbox.hartshorn.core.annotations.inject.Enable;
import org.dockbox.hartshorn.persistence.properties.ModifiersAttribute;
import org.dockbox.hartshorn.web.HttpMethod;
import org.dockbox.hartshorn.web.RequestHandlerContext;
import org.dockbox.hartshorn.web.ServletHandler;
import org.dockbox.hartshorn.web.HttpWebServer;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Binds(JettyServletAdapter.class)
public class JettyServletAdapter extends HttpServlet {

    @Enable(delegate = ModifiersAttribute.class)
    private final ServletHandler handler;

    @Bound
    public JettyServletAdapter(final HttpWebServer starter, final RequestHandlerContext context) {
        this.handler = context.applicationContext().get(ServletHandler.class, starter, context.httpRequest().method(), context.methodContext());
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.handler.processRequest(HttpMethod.GET, req, res, super::doGet);
    }

    @Override
    protected void doHead(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.handler.processRequest(HttpMethod.HEAD, req, res, super::doHead);
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.handler.processRequest(HttpMethod.POST, req, res, super::doPost);
    }

    @Override
    protected void doPut(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.handler.processRequest(HttpMethod.PUT, req, res, super::doPut);
    }

    @Override
    protected void doDelete(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.handler.processRequest(HttpMethod.DELETE, req, res, super::doDelete);
    }

    @Override
    protected void doOptions(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.handler.processRequest(HttpMethod.OPTIONS, req, res, super::doOptions);
    }

    @Override
    protected void doTrace(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.handler.processRequest(HttpMethod.TRACE, req, res, super::doTrace);
    }
}
