package org.dockbox.hartshorn.web.servlet;

import java.io.IOException;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface DirectoryServlet {
    void handle(HttpServletRequest request, HttpServletResponse response, URI uri, String path) throws IOException;
}
