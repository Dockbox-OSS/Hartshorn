package org.dockbox.hartshorn.web.jetty.route;

import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.web.chain.RequestHandlerChain;
import org.dockbox.hartshorn.web.jetty.message.JettyWebRequest;
import org.dockbox.hartshorn.web.jetty.message.JettyWebResponse;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

public class JettyRequestHandler extends Handler.Abstract {

    private final RequestHandlerChain chain;

    @Inject
    public JettyRequestHandler(RequestHandlerChain chain) {
        this.chain = chain;
    }

    @Override
    public boolean handle(Request request, Response response, Callback callback) throws Exception {
        WebRequest webRequest = new JettyWebRequest(request);
        WebResponse webResponse = new JettyWebResponse(response);
        this.chain.accept(webRequest, webResponse);
        return true;
    }
}
