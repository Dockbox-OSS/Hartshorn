package org.dockbox.hartshorn.jetty;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@FunctionalInterface
public interface ServletFallback {
    void fallback(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
