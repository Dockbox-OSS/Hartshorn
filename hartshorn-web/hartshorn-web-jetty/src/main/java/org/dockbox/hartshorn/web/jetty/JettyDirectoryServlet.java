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

import org.dockbox.hartshorn.web.HttpStatus;
import org.dockbox.hartshorn.web.HttpWebServer;
import org.dockbox.hartshorn.web.servlet.DirectoryServlet;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.resource.Resource;

import java.io.IOException;
import java.net.URI;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JettyDirectoryServlet implements DirectoryServlet {

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
