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
