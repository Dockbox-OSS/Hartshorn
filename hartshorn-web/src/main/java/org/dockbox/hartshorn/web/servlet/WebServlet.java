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

import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.web.HttpAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WebServlet {

    void get(final HttpServletRequest req, final HttpServletResponse res, HttpAction fallback) throws ApplicationException;
    void head(final HttpServletRequest req, final HttpServletResponse res, HttpAction fallback) throws ApplicationException;
    void post(final HttpServletRequest req, final HttpServletResponse res, HttpAction fallback) throws ApplicationException;
    void put(final HttpServletRequest req, final HttpServletResponse res, HttpAction fallback) throws ApplicationException;
    void delete(final HttpServletRequest req, final HttpServletResponse res, HttpAction fallback) throws ApplicationException;
    void options(final HttpServletRequest req, final HttpServletResponse res, HttpAction fallback) throws ApplicationException;
    void trace(final HttpServletRequest req, final HttpServletResponse res, HttpAction fallback) throws ApplicationException;
    
}
