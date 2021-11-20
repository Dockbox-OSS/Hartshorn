package org.dockbox.hartshorn.web.servlet;

import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.web.HttpAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@FunctionalInterface
public interface HttpServletAction {
    void perform(HttpServletRequest request, HttpServletResponse response, HttpAction fallback) throws ApplicationException;
}
