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

package org.dockbox.hartshorn.web.jetty;

import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.web.HttpStatus;
import org.dockbox.hartshorn.web.HttpWebServer;
import org.dockbox.hartshorn.web.servlet.DirectoryServlet;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.resource.Resource;

import java.io.IOException;
import java.net.URI;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Binds(DirectoryServlet.class)
public class JettyDirectoryServlet implements DirectoryServlet {

    @Inject
    private ApplicationContext applicationContext;

    @Override
    public void handle(final HttpServletRequest request, final HttpServletResponse response, final URI uri, final String path) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/";
        }
        final Resource resource = Resource.newClassPathResource(HttpWebServer.STATIC_CONTENT + pathInfo);
        if (resource.isDirectory()) {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().println("<html><head><title>Directory listing for " + pathInfo + "</title></head><body>");
            response.getWriter().println("<h1>Directory listing for " + pathInfo + "</h1>");
            response.getWriter().println("<ul>");
            for (final String child : resource.list()) {
                response.getWriter().println("<li><a href=\"" + URIUtil.encodePath(child) + "\">" + child + "</a></li>");
            }
            response.getWriter().println("</ul>");
            response.getWriter().println("</body></html>");
        } else {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
    }
}