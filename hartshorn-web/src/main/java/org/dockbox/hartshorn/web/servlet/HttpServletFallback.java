package org.dockbox.hartshorn.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HttpServletFallback {
    void accept(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
