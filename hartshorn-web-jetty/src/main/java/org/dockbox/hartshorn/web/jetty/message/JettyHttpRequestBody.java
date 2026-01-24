package org.dockbox.hartshorn.web.jetty.message;

import org.dockbox.hartshorn.web.message.HttpRequestBody;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Request;

import java.io.IOException;

public class JettyHttpRequestBody implements HttpRequestBody {

    private final Request request;

    public JettyHttpRequestBody(Request request) {
        this.request = request;
    }

    @Override
    public byte[] bytes() throws IOException {
        return Content.Source.asByteBuffer(request).array();
    }

    @Override
    public String asString() throws IOException {
        return Content.Source.asString(request);
    }
}
