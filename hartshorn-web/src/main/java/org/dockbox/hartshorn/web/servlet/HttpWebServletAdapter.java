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

package org.dockbox.hartshorn.web.servlet;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.web.HttpAction;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpWebServletAdapter extends HttpServlet {

    private final ApplicationContext applicationContext;
    private final WebServlet webServlet;

    public HttpWebServletAdapter(final ApplicationContext applicationContext, final WebServlet webServlet) {
        this.applicationContext = applicationContext;
        this.webServlet = webServlet;
    }

    @Override
    protected synchronized void doGet(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.perform(this.webServlet::get, req, res, super::doGet);
    }

    @Override
    protected synchronized void doHead(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.perform(this.webServlet::head, req, res, super::doHead);
    }

    @Override
    protected synchronized void doPost(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.perform(this.webServlet::post, req, res, super::doPost);
    }

    @Override
    protected synchronized void doPut(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.perform(this.webServlet::put, req, res, super::doPut);
    }

    @Override
    protected synchronized void doDelete(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.perform(this.webServlet::delete, req, res, super::doDelete);
    }

    @Override
    protected synchronized void doOptions(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.perform(this.webServlet::options, req, res, super::doOptions);
    }

    @Override
    protected synchronized void doTrace(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.perform(this.webServlet::trace, req, res, super::doTrace);
    }

    private synchronized void perform(final HttpServletAction action, final HttpServletRequest req, final HttpServletResponse res, final HttpServletFallback fallback) throws ServletException, IOException {
        try {
            this.applicationContext.log().debug("Received " + req.getMethod() + " " + req.getRequestURI());
            action.perform(req, res, this.wrap(fallback));
            this.applicationContext.log().debug("Request " + req.getMethod() + " " + req.getRequestURI() + " completed");
        }
        catch (final ApplicationException e) {
            if (e.getCause() instanceof ServletException servletException) throw servletException;
            else if (e.getCause() instanceof IOException ioException) throw ioException;
            else throw new ServletException(e.unwrap());
        }
    }

    private HttpAction wrap(final HttpServletFallback fallback) {
        return (req, res) -> {
            try {
                fallback.accept(req, res);
            }
            catch (final ServletException | IOException e) {
                throw new ApplicationException(e);
            }
        };
    }
}
