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

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.web.HttpStatus;
import org.dockbox.hartshorn.web.servlet.DirectoryServlet;
import org.eclipse.jetty.http.HttpContent;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.ResourceService;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.resource.Resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;

import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JettyResourceService extends ResourceService {

    @Inject
    private DirectoryServlet servlet;

    @Inject
    private ApplicationContext applicationContext;

    @Override
    protected void sendDirectory(final HttpServletRequest req,
                                 final HttpServletResponse res,
                                 final Resource resource,
                                 final String pathInContext)
            throws IOException {
        if (!this.isDirAllowed()) {
            res.sendError(HttpStatus.FORBIDDEN.value());
            return;
        }
        this.applicationContext.log().debug("Received " + req.getMethod() + " " + req.getRequestURI());
        this.servlet.handle(req, res, resource.getURI(), pathInContext);
        this.applicationContext.log().debug("Request " + req.getMethod() + " " + req.getRequestURI() + " completed");
    }

    @Override
    public boolean doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String servletPath;
        String pathInfo;
        Enumeration<String> reqRanges = null;
        final boolean included = request.getAttribute(RequestDispatcher.INCLUDE_REQUEST_URI) != null;
        if (included)
        {
            servletPath = this.isPathInfoOnly() ? "/" : (String)request.getAttribute(RequestDispatcher.INCLUDE_SERVLET_PATH);
            pathInfo = (String)request.getAttribute(RequestDispatcher.INCLUDE_PATH_INFO);
            if (servletPath == null)
            {
                servletPath = request.getServletPath();
                pathInfo = request.getPathInfo();
            }
        }
        else
        {
            servletPath = this.isPathInfoOnly() ? "/" : request.getServletPath();
            pathInfo = request.getPathInfo();

            // Is this a Range request?
            reqRanges = request.getHeaders(HttpHeader.RANGE.asString());
            if (!(reqRanges != null && reqRanges.hasMoreElements()))
                reqRanges = null;
        }

        String pathInContext = URIUtil.addPaths(servletPath, pathInfo);

        final boolean endsWithSlash = (pathInfo == null ? (this.isPathInfoOnly() ? "" : servletPath) : pathInfo).endsWith(URIUtil.SLASH);
        final boolean checkPrecompressedVariants = this.getPrecompressedFormats().length > 0 && !endsWithSlash && !included && reqRanges == null;

        HttpContent content = null;
        boolean releaseContent = true;
        try
        {
            // Find the content
            content = this.getContentFactory().getContent(pathInContext, response.getBufferSize());

            // Not found?
            if (content == null || !content.getResource().exists())
            {
                if (included)
                    throw new FileNotFoundException("!" + pathInContext);
                // Allow secondary handler to handle this request
                return false;
            }

            // Directory?
            if (content.getResource().isDirectory())
            {
                this.sendWelcome(content, pathInContext, endsWithSlash, included, request, response);
                return true;
            }

            // Strip slash?
            if (!included && endsWithSlash && pathInContext.length() > 1)
            {
                final String q = request.getQueryString();
                pathInContext = pathInContext.substring(0, pathInContext.length() - 1);
                if (q != null && !q.isEmpty())
                    pathInContext += "?" + q;
                response.sendRedirect(response.encodeRedirectURL(URIUtil.addPaths(request.getContextPath(), pathInContext)));
                return true;
            }

            // Conditional response?
            if (!included && !this.passConditionalHeaders(request, response, content))
                return true;

            if (this.isGzippedContent(pathInContext))
                response.setHeader(HttpHeader.CONTENT_ENCODING.asString(), "gzip");

            // Send the data
            releaseContent = this.sendData(request, response, included, content, reqRanges);
        }
        catch (final IllegalArgumentException e)
        {
            this.applicationContext.handle(e);
            if (!response.isCommitted())
                response.sendError(500, e.getMessage());
        }
        finally
        {
            if (releaseContent)
            {
                if (content != null)
                    content.release();
            }
        }

        return true;
    }
}
