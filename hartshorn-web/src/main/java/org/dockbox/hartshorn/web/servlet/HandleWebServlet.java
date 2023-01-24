package org.dockbox.hartshorn.web.servlet;

import org.dockbox.hartshorn.web.ServletHandler;

public interface HandleWebServlet extends WebServlet {

    ServletHandler handler();
}
