/*
 * Copyright 2019-2022 the original author or authors.
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

import jakarta.inject.Inject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
