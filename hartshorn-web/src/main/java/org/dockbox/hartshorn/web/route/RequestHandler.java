package org.dockbox.hartshorn.web.route;

import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;

public interface RequestHandler {

    void handle(WebRequest request, WebResponse response) throws Exception;
}
