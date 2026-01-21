package org.dockbox.hartshorn.web.jetty.message;

import org.dockbox.hartshorn.web.HttpMethod;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.eclipse.jetty.server.Request;

public class JettyWebRequest implements WebRequest {

    private final Request request;

    public JettyWebRequest(Request request) {
        this.request = request;
    }

    @Override
    public HttpMethod method() {
        String method = request.getMethod();
        return HttpMethod.fromString(method).orElse(null);
    }

    @Override
    public String path() {
        return Request.getPathInContext(request);
    }
}
