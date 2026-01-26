package org.dockbox.hartshorn.web.jetty.message;

import org.dockbox.hartshorn.web.message.WebRequestClient;
import org.eclipse.jetty.server.Request;

class JettyWebRequestClient implements WebRequestClient {

    private final Request request;

    JettyWebRequestClient(Request request) {
        this.request = request;
    }

    @Override
    public String address() {
        return Request.getRemoteAddr(this.request);
    }

    @Override
    public int port() {
        return Request.getRemotePort(this.request);
    }
}
