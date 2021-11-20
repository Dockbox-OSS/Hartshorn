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

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.web.HttpAction;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HttpWebServletAdapter extends HttpServlet {

    private final ApplicationContext applicationContext;
    private final WebServlet webServlet;

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
            else throw e.runtime();
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
