package org.dockbox.hartshorn.web.servlet;

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
    
    private final WebServlet webServlet;

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.perform(this.webServlet::get, req, res, super::doGet);
    }

    @Override
    protected void doHead(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.perform(this.webServlet::head, req, res, super::doHead);
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.perform(this.webServlet::post, req, res, super::doPost);
    }

    @Override
    protected void doPut(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.perform(this.webServlet::put, req, res, super::doPut);
    }

    @Override
    protected void doDelete(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.perform(this.webServlet::delete, req, res, super::doDelete);
    }

    @Override
    protected void doOptions(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.perform(this.webServlet::options, req, res, super::doOptions);
    }

    @Override
    protected void doTrace(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.perform(this.webServlet::trace, req, res, super::doTrace);
    }

    private void perform(final HttpServletAction action, final HttpServletRequest req, final HttpServletResponse res, final HttpServletFallback fallback) throws ServletException, IOException {
        try {
            action.perform(req, res, this.wrap(fallback));
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
